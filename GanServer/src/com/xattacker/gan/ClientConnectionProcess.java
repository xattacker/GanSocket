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
import com.xattacker.util.Logger;


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
								Logger.instance().debug("login account: " + account + "/" + password);
								
								StringBuilder session_id = new StringBuilder();
								boolean result = SessionPool.instance().addSession(account, _socket, session_id);
								
								response = new ResponsePack();
								response.setResult(result);
								
								if (result)
								{
									close_connection = false;
									
									String id = session_id.toString();
									response.setContent(id.getBytes());
									Logger.instance().log("create session id [" + id + "] for account [" + account + "]");
								}
							}
								break;
								
							case LOGOUT:
							{
								String account = ibb.readString();
								SessionPool.instance().removeSession(account);
								Logger.instance().log("account logged out: " + account);
								
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
								Logger.instance().debug("got message: " + msg);
								
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
								Logger.instance().error("unhandled request type: " + request.getType());
								break;
						}
						
						if (response != null)
						{
						   PackChecker.packData(response, _socket.getOutputStream());
						   Logger.instance().debug("send response " + response.getResult());
							
							break;
						}
					}
					else
					{
						Logger.instance().warn("invalid request pack, just ignore it");
					}
				}
			}
			catch (Exception ex)
			{
				Logger.instance().except(ex);

				break;
			}
		}
		
		if (close_connection)
		{
			close();
		}
		
		Logger.instance().debug("ClientConnectionProcess ended");
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
