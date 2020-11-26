package com.xattacker.gan;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.GsonBuilder;

import com.xattacker.binary.BinaryBuffer;
import com.xattacker.binary.InputBinaryBuffer;
import com.xattacker.binary.OutputBinaryBuffer;
import com.xattacker.binary.TypeConverter;
import com.xattacker.gan.account.SessionPool;
import com.xattacker.gan.data.FunctionType;
import com.xattacker.gan.data.FunctionTypeJsonSerializer;
import com.xattacker.gan.data.PackChecker;
import com.xattacker.gan.data.RequestHeader;
import com.xattacker.gan.data.ResponsePack;
import com.xattacker.gan.exception.ConnectEOFException;
import com.xattacker.gan.msg.MsgData;
import com.xattacker.gan.msg.MsgManager;
import com.xattacker.json.JsonBuilderVisitor;
import com.xattacker.json.JsonUtility;

final class GanClientConnectionProcess extends Thread
{
	private Socket _socket = null;

	public GanClientConnectionProcess(Socket aSocket)
	{
		_socket = aSocket;
	}

	public void run()
	{
		InputStream in = null;
		OutputStream out = null;

		while (true)
		{
			try
			{
				in = _socket.getInputStream();
				
				PackChecker.ValidResult valid = PackChecker.isValidPack(in, false);
				if (valid.valid && valid.length > 0)
				{
					int wait_count = 0;
		      	while (in.available() < valid.length && wait_count < 20)
		      	{
		      		wait_count++;
		      		Thread.sleep(50);
		      	}
		      	
		      	if (in.available() < valid.length)
		      	{
		      		throw new Exception("request data length not enough");
		      	}
		      	
		      	
					ResponsePack response = null;
					InputBinaryBuffer ibb = new InputBinaryBuffer(in);
					String json = ibb.readString();
					
					RequestHeader request = JsonUtility.fromJson(json, 
													RequestHeader.class, 		
													new JsonBuilderVisitor() 
													{
														@Override
														public void onBuilderPrepared(GsonBuilder aBuilder)
														{
															aBuilder.registerTypeAdapter(FunctionType.class, new FunctionTypeJsonSerializer());
														}
													});
					
					if (request != null)
					{
						switch (request.getType())
						{
							case LOGIN:
							{
								String account = ibb.readString();
								String password = ibb.readString();
								System.out.println("login account: " + account + "/" + password);
								
								StringBuilder session_id = new StringBuilder();
								boolean result = SessionPool.instance().addSession(account, session_id);
								
								response = new ResponsePack();
								response.setResult(result);
								
								if (result)
								{
									String id = session_id.toString();
									response.setContent(id.getBytes());
									System.out.println("create session id [" + id + "] for account [" + account + "]");
								}
							}
								break;
								
							case CREATE_CALLBACK_CONNECTION:
								handleConnection(request.getOwner(), request.getSessionId());
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
								InetAddress addr = _socket.getInetAddress();
								String ip = addr.getHostAddress();
								
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
							out = _socket.getOutputStream();
							
							OutputBinaryBuffer obb = new OutputBinaryBuffer(out);
							response.toBinary(obb);
							
							obb.flush();
							response = null;
							
							System.out.println("send response");
						}
					}
					else
					{
						System.out.println("invalid request pack, just ignore it");
					}
				}
			}
			catch (ConnectEOFException ex)
			{
				System.out.println("got ConnectEOFException:");
				ex.printStackTrace();

				break;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();

				break;
			}
		}
		
		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (Exception ex)
			{
			}
			
			in = null;
		}
		
		if (out != null)
		{
			try
			{
				out.close();
			}
			catch (Exception ex)
			{
			}
			
			out = null;
		}
		
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
		
		System.out.println("_socket closed");
	}
	
	private void handleConnection(String aAccount, String aSessionId)
	{
		try
		{
			while (true)
			{
				 Thread.sleep(800);
				 
				 if (SessionPool.instance() != null && !SessionPool.instance().checkSession(aAccount, aSessionId))
				 {
					 // session id 失效了, 結束前一個 connection
					 ResponsePack response = new ResponsePack();
					 response.setResult(true);
					 response.setId(FunctionType.LOGOUT.value());
					 response.setContent(aAccount.getBytes());
					 
					 BinaryBuffer buffer = new BinaryBuffer();
					 response.toBinary(buffer);
					 
					 PackChecker.addHeaderPack((int)buffer.getLength(), _socket.getOutputStream());
					 
					 OutputBinaryBuffer obb = new OutputBinaryBuffer(_socket.getOutputStream());
					 obb.writeBinary(buffer.getData(), 0, (int)buffer.getLength());
					 obb.flush();

					 break;
				 }
				
				 if (MsgManager.instance() != null && MsgManager.instance().hasMsg(aAccount))
				 {
					 ArrayList<MsgData> list = MsgManager.instance().getMsgs(aAccount);
					 if (list != null && !list.isEmpty())
					 {
						 for (MsgData msg : list)
						 {
							 ResponsePack response = new ResponsePack();
							 response.setResult(true);
							 response.setId(FunctionType.RECEIVE_SMS.value());
							 response.setContent(msg.toJson().getBytes("UTF8"));

							 BinaryBuffer buffer = new BinaryBuffer();
							 response.toBinary(buffer);
							 
							 PackChecker.addHeaderPack((int)buffer.getLength(), _socket.getOutputStream());
							 
							 OutputBinaryBuffer obb = new OutputBinaryBuffer(_socket.getOutputStream());
							 obb.writeBinary(buffer.getData(), 0, (int)buffer.getLength());
							 obb.flush();
							 
							 System.out.println("send msg: " + msg.getMessage());
							 Thread.sleep(500);
						 }
						 
						 list.clear();
						 list = null;
						 System.gc();
					 }
				 }
			}
		}
		catch (Exception ex)
		{
		}
	}
}
