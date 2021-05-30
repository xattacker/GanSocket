package com.xattacker.gan.session;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.xattacker.binary.InputBinaryBuffer;
import com.xattacker.gan.data.FunctionType;
import com.xattacker.gan.data.PackChecker;
import com.xattacker.gan.data.ResponsePack;
import com.xattacker.gan.msg.MsgAck;
import com.xattacker.gan.msg.MsgData;
import com.xattacker.gan.msg.MsgManager;
import com.xattacker.json.JsonUtility;
import com.xattacker.util.Logger;

final class SessionConnectionProcess extends Thread
{
	public interface SessionConnectionProcessListener
	{
		String getSessionId(String account);
	}
	
	private final static int WAITING_COUNT = 20;
	private String _account;
	private String _sessionId;
	private Socket _socket = null;

	public SessionConnectionProcess(String anAccount, String aSessionId, Socket aSocket)
	{
		_account = anAccount;
		_sessionId = aSessionId;
		_socket = aSocket;
	}
	
	public void run()
	{
		try
		{
			while (isValid())
			{
				 Thread.sleep(800);
				 
				 if (!isValid())
				 {
					 Logger.instance().warn("CallbackConnectionProcess terminated");
					 break;
				 }
				 
				 if (SessionPool.instance() != null && !SessionPool.instance().checkSession(_account, _sessionId))
				 {
					 // session id 失效了, 結束前一個 connection
					 ResponsePack response = new ResponsePack();
					 response.setResult(true);
					 response.setId(FunctionType.LOGOUT.value());
					 response.setContent(_account.getBytes());
					 
					 PackChecker.packData(response, _socket.getOutputStream());
					 Logger.instance().warn("closed session " + _sessionId + " for [" + _account + "]");

					 break;
				 }
				 
				 if (!isValid())
				 {
					 Logger.instance().warn("CallbackConnectionProcess terminated");
					 
					 break;
				 }
				 
				 if (MsgManager.instance() != null && MsgManager.instance().hasMsg(_account))
				 {
					 ArrayList<MsgData> messages = MsgManager.instance().getMsgs(_account);
					 if (messages != null && !messages.isEmpty())
					 {
						 Logger.instance().debug("try to send msg: " + messages.size());
						 
						 for (MsgData msg : messages)
						 {
							   ResponsePack response = new ResponsePack();
							   response.setResult(true);
							   response.setId(FunctionType.RECEIVE_SMS.value());
							   response.setContent(msg.toJson().getBytes("UTF8"));
							   PackChecker.packData(response, _socket.getOutputStream());
							   Logger.instance().debug("send msg to [" + _account + "]: " + msg.getMessage());
								
							   Thread.sleep(200);

							   // try to receive client side's ack response
							   InputStream in = _socket.getInputStream();  
								PackChecker.ValidResult valid = PackChecker.isValidPack(in, WAITING_COUNT, false);
								if (valid.valid && valid.length > 0)
								{
							      InputBinaryBuffer ibb = new InputBinaryBuffer(in);
							      byte[] ack_bytes = ibb.readBinary(valid.length);
							      String json = new String(ack_bytes);
							      MsgAck ack = JsonUtility.fromJson(json, MsgAck.class);
							      if (ack != null && ack.getId() != null && ack.getId().equals(msg.getId()))
							      {
	//						      	ResponsePack ack_response = new ResponsePack();
	//						      	ack_response.setResult(true);
	//						      	ack_response.setId(FunctionType.RECEIVE_SMS_ACK.value());
	//						         PackChecker.packData(ack_response, _socket.getOutputStream());
							         
							      	MsgManager.instance().removeMsg(_account, ack.getId());
							      	Logger.instance().debug("got msg ack response form [" + _account + "]");
							      }
								}
						 }
						 
						 System.gc();
					 }
				 }
			}
		}
		catch (Exception ex)
		{
			Logger.instance().except(ex);
		}
		
		Logger.instance().debug("CallbackConnectionProcess end for account: " + _account + ", session: " + _sessionId);
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
		
		_account = null;
		_sessionId = null;
	}
	
	public boolean isValid()
	{
		return _socket != null && !_socket.isClosed() && _account != null;
	}
}
