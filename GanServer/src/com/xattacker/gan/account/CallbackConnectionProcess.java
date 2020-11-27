package com.xattacker.gan.account;

import java.net.Socket;
import java.util.ArrayList;

import com.xattacker.binary.BinaryBuffer;
import com.xattacker.binary.OutputBinaryBuffer;
import com.xattacker.gan.data.FunctionType;
import com.xattacker.gan.data.PackChecker;
import com.xattacker.gan.data.ResponsePack;
import com.xattacker.gan.msg.MsgData;
import com.xattacker.gan.msg.MsgManager;

final class CallbackConnectionProcess extends Thread
{
	public interface CallbackConnectionProcessListener
	{
		String getSessionId(String account);
	}
	
	
	private String _account;
	private String _sessionId;
	private Socket _socket = null;

	public CallbackConnectionProcess(String anAccount, String aSessionId, Socket aSocket)
	{
		_account = anAccount;
		_sessionId = aSessionId;
		_socket = aSocket;
	}
	
	public void run()
	{
		try
		{
			while (_socket != null && !_socket.isClosed() && _account != null)
			{
				 Thread.sleep(800);
				 
				 if (SessionPool.instance() != null && !SessionPool.instance().checkSession(_account, _sessionId))
				 {
					 // session id 失效了, 結束前一個 connection
					 ResponsePack response = new ResponsePack();
					 response.setResult(true);
					 response.setId(FunctionType.LOGOUT.value());
					 response.setContent(_account.getBytes());
					 
					 BinaryBuffer buffer = new BinaryBuffer();
					 response.toBinary(buffer);
					 
					 PackChecker.addHeaderPack((int)buffer.getLength(), _socket.getOutputStream());
					 
					 OutputBinaryBuffer obb = new OutputBinaryBuffer(_socket.getOutputStream());
					 obb.writeBinary(buffer.getData(), 0, (int)buffer.getLength());
					 obb.flush();

					 break;
				 }
				
				 if (MsgManager.instance() != null && MsgManager.instance().hasMsg(_account))
				 {
					 ArrayList<MsgData> list = MsgManager.instance().getMsgs(_account);
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
		
		System.out.println("CallbackConnectionProcess end for account: " + _account + ", session: " + _sessionId);
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
}
