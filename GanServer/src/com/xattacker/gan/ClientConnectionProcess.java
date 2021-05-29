package com.xattacker.gan;

import java.io.InputStream;
import java.net.Socket;

import com.xattacker.binary.InputBinaryBuffer;
import com.xattacker.binary.TypeConverter;
import com.xattacker.gan.data.PackChecker;
import com.xattacker.gan.data.RequestHeader;
import com.xattacker.gan.data.ResponsePack;
import com.xattacker.gan.exception.ConnectEOFException;
import com.xattacker.gan.msg.MsgData;
import com.xattacker.gan.msg.MsgManager;
import com.xattacker.gan.session.SessionPool;


public final class ClientConnectionProcess extends Thread
{
	private Socket _socket = null;

	public ClientConnectionProcess(Socket aSocket)
	{
		_socket = aSocket;
	}

	public void run()
	{
		InputStream in = null;
		boolean close_connection = true;

		while (true)
		{
			try
			{
				in = _socket.getInputStream();
				
				final int max_waiting_count = 50;

				PackChecker.ValidResult valid = PackChecker.isValidPack(in, max_waiting_count, false);
				if (valid.valid && valid.length > 0)
				{
					int wait_count = 0;
		      	while (in.available() < valid.length && wait_count < max_waiting_count)
		      	{
		      		wait_count++;
		      		Thread.sleep(50);
		      	}
		      	
		      	if (in.available() < valid.length)
		      	{
		      		throw new ConnectEOFException();
		      	}
		      	
		      	
					ResponsePack response = null;
					
					RequestHeader request = RequestHeader.parseHeader(in);
					if (request != null)
					{
						InputBinaryBuffer ibb = new InputBinaryBuffer(in);
						
						switch (request.getType())
						{
							case LOGIN:
							{
								String account = ibb.readString();
								String password = ibb.readString();
								System.out.println("login account: " + account + "/" + password);
								
								StringBuilder session_id = new StringBuilder();
								boolean result = SessionPool.instance().addSession(account, _socket, session_id);
								
								response = new ResponsePack();
								response.setResult(result);
								
								if (result)
								{
									close_connection = false;
									
									String id = session_id.toString();
									response.setContent(id.getBytes());
									System.out.println("create session id [" + id + "] for account [" + account + "]");
								}
							}
								break;
								
							case LOGOUT:
							{
								String account = ibb.readString();
								SessionPool.instance().removeSession(account);
								System.out.println("account logged out: " + account);
								
								response = new ResponsePack();
								response.setResult(true);
							}
								break;
								
							case SEND_SMS:
							{
								String receiver = ibb.readString();
								String msg = ibb.readString();
								
								MsgData sms = new MsgData();
								sms.setSender(request.getOwner());
								sms.setMessage(msg);
								sms.setTime(System.currentTimeMillis());
								MsgManager.instance().addMsg(receiver, sms);
								System.out.println("got message: " + msg);
								
								response = new ResponsePack();
								response.setResult(true);
							}
								break;
								
							case GET_IP:
							{
								String ip = _socket.getInetAddress().getHostAddress();
								
								response = new ResponsePack();
								response.setResult(true);
								response.setContent(ip.getBytes());
							}
								break;
								
							case GET_SYSTEM_TIME:
							{
								response = new ResponsePack();
								response.setResult(true);
								response.setContent(TypeConverter.longToByte(System.currentTimeMillis()));
							}
								break;
								
							default:
								System.out.println("unhandled request type: " + request.getType());
								break;
						}
						
						if (response != null)
						{
						   PackChecker.packData(response, _socket.getOutputStream());
							System.out.println("send response " + response.getResult());
							
							break;
						}
					}
					else
					{
						System.out.println("invalid request pack, just ignore it");
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();

				break;
			}
		}
		
		if (close_connection)
		{
			close();
		}
		
		System.out.println("_socket closed");
	}
	
	public void close()
	{
		if (_socket != null)
		{
			try
			{
				_socket.close();
			}
			catch (Exception ex)
			{
			}
			
			_socket = null;
		}
	}
}
