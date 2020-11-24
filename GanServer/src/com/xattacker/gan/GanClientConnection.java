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

final class GanClientConnection extends Thread
{
	private Socket _socket = null;

	public GanClientConnection(Socket aSocket)
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
				
				if (PackChecker.isValidPack(in, false))
				{
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
								System.out.println(account + ", " + password);
								
								StringBuilder session_id = new StringBuilder();
								boolean result = SessionPool.instance().addSession(account, session_id);
								
								response = new ResponsePack();
								response.setResult(result);
								
								if (result)
								{
									String temp = session_id.toString();
									response.setContent(temp.getBytes());
								}
							}
								break;
								
							case CONNECTION:
							{
								handleConnection(request.getOwner(), request.getSessionId());
							}
								break;
								
							case LOGOUT:
							{
								String account = ibb.readString();
								SessionPool.instance().removeSession(account);
								
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
								MsgManager.instance().addSms(receiver, sms);
								
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
				System.out.println("got ConnectEOFException");
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
				 Thread.sleep(2000);
				 
				 if (SessionPool.instance() != null && !SessionPool.instance().checkSession(aAccount, aSessionId))
				 {
					 ResponsePack response = new ResponsePack();
					 response.setResult(true);
					 response.setId(2);
					 
					 OutputStream out = _socket.getOutputStream();
					 out.write(PackChecker.HEAD_BYTE);
					 
					 OutputBinaryBuffer obb = new OutputBinaryBuffer(out);
					 response.toBinary(obb);
						
					 obb.flush();
					 response = null;
					 
					 break;
				 }
				
				 if (MsgManager.instance() != null && MsgManager.instance().hasSms(aAccount))
				 {
					 ArrayList<MsgData> list = MsgManager.instance().getSms(aAccount);
					 if (list != null && !list.isEmpty())
					 {
						 for (MsgData sms : list)
						 {
							 OutputStream out = _socket.getOutputStream();
							 out.write(PackChecker.HEAD_BYTE);
								
							 ResponsePack response = new ResponsePack();
							 response.setResult(true);
							 response.setId(1);
							 
							 BinaryBuffer buffer = new BinaryBuffer();
							 buffer.writeString(sms.getSender());
							 buffer.writeLong(sms.getTime());
							 buffer.writeString(sms.getMessage());
							 response.setContent(buffer.getData());
							 
							 OutputBinaryBuffer obb = new OutputBinaryBuffer(out);
							 response.toBinary(obb);
								
							 obb.flush();
							 response = null;
						    buffer = null;
							 
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
