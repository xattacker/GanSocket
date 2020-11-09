package com.xattacker.gan;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.xattacker.binary.BinaryBuffer;
import com.xattacker.gan.data.PackFormatChecker;
import com.xattacker.gan.data.FunctionType;
import com.xattacker.gan.data.RequestHeader;
import com.xattacker.gan.data.ResponsePack;
import com.xattacker.gan.service.AccountService;
import com.xattacker.gan.service.AccountService.AccountServiceListener;
import com.xattacker.gan.service.MsgService;
import com.xattacker.gan.service.SystemService;
import com.xattacker.util.IOUtility;
import com.xattacker.binary.OutputBinaryBuffer;

public final class GanClient implements Runnable, 
                                        GanAgent, 
                                        AccountServiceListener
{
	private final static int TIMEOUT = 5000;
	
	private static GanClient _instance;
	
	private String _address;
   private int _port;
   private Socket _socket;
   private GanClientListener _listener;
   private String _account;
   private String _sessionId;
   
   private AccountService _accountService;
   private MsgService _msgService;
   private SystemService _systemService;
   
   private GanClient
   (
   String aAddress, 
   int aPort, 
   GanClientListener aListener
   ) throws Exception
   {
   	_address = aAddress;
   	_port = aPort;
   	_listener = aListener;
   }
   
   public static void constructInstance
   (
   String aAddress, 
   int aPort, 
   GanClientListener aListener
   ) throws Exception
   {
   	if (_instance == null)
   	{
   		_instance = new GanClient(aAddress, aPort, aListener);
   	}
   }
   
   public static void release()
   {
   	if (_instance != null)
   	{
   		_instance.doRelease();
   		_instance = null;
   	}
   }
   
   public static GanClient instance()
   {
   	return _instance;
   }
   
   public AccountService accountService()
   {
   	if (_accountService == null)
   	{
   		_accountService = new InnerAccountService(this, this);
   	}
   	
   	return _accountService;
   }
   
   public MsgService smsService()
   {
   	if (_msgService == null)
   	{
   		_msgService = new InnerSmsService(this);
   	}
   	
   	return _msgService;
   }
   
   public SystemService systemService()
   {
   	if (_systemService == null)
   	{
   		_systemService = new InnerSystemService(this);
   	}
   	
   	return _systemService;
   }
   
	@Override
   public void run() 
   {
		try 
		{
			 OutputStream out = _socket.getOutputStream();
			 out.write(PackFormatChecker.HEAD_BYTE);
				
			 OutputBinaryBuffer obb = new OutputBinaryBuffer(out);
			 
			 RequestHeader header = new RequestHeader();
			 header.setType(FunctionType.CONNECTION);
			 header.setOwner(_account);
			 header.setSessionId(_sessionId);
			 
			 obb.writeString(header.toJson());
			 obb.flush();
			 
			 Thread.sleep(500);
			 header = null;
			 
			 int count = 0;
			 
		    while (_account != null) 
		    {		
		   	 if (count >= 20)
		   	 {
		   		 count = 0;
		   		 _socket.sendUrgentData(0xFF);
		   	 }
		   	 
		   	 if (PackFormatChecker.isValidPack(_socket.getInputStream(), false))
				 {
		   		  ByteArrayOutputStream bos = new ByteArrayOutputStream();
		   		  IOUtility.readResponse(_socket.getInputStream(), bos);

		   		  BinaryBuffer binary = new BinaryBuffer(bos.toByteArray());
		   		  
		   		  ResponsePack response = new ResponsePack();
		   		  response.fromBinary(binary);
		   		  
		   		  if (response.getResult())
		   		  {
		   			  switch (response.getId())
		   			  {
		   				  case 1: // get message
		   				  {
				   			  binary = new BinaryBuffer(response.getContent());
				   				
				   			  String sender = binary.readString();
				   			  long time = binary.readLong();
				   			  String msg = binary.readString();
				   				  
								  if (_listener != null)
								  {
									  _listener.onSMSReceived(sender, time, msg);
								  }
		   				  }
		   					  break;
		   					  
		   				  case 2: // force logout
		   				  {
		   					  closeConnection();
		   					  
		   					  if (_listener != null)
								  {
		   						  _listener.onAccountClosed(_account);
								  }
		   					  
		   					  _account = null;
		   					  _sessionId = null;
		   				  }
		   					  break;
		   			  }
		   		  }
		   		  
		   		  bos.close();
		   		  bos = null;
				 }
		   	 
		   	 Thread.sleep(500);
		   	 count++;
		    }
		}
		catch (Exception ex) 
		{
			android.util.Log.i("aaa",  ex.toString());
		}
   }

	@Override
	public Socket createSocket() throws Exception
	{
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(_address, _port), TIMEOUT);
   	
		return socket;
	}
	
	@Override 
	public String getAccount()
	{
		return _account;
	}
	
	@Override 
	public String getSessionId()
	{
		return _sessionId;
	}
	
	@Override
	public void onLoginSucceed(String aAccount, String aSessionId)
	{
		try
		{
			_account = aAccount;
			_sessionId = aSessionId;

			_socket = createSocket();
   		_socket.setKeepAlive(true);
   		_socket.setSoTimeout(0);
   		_socket.setOOBInline(true);

   		if (_listener != null)
   		{
   			_listener.onAccountLogined(_account);
   		}
   		
      	start();
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public void onLogouted(String aAccount)
	{
		_account = null;
		_sessionId = null;
	}
	
   private void start() 
   {
		new Thread(this).start();
   }
   
   private void closeConnection()
   {
		if (_socket != null)
		{
			try
			{
				_socket.getInputStream().close();
			}
			catch (Exception ex)
			{
			}
			
			try
			{
				_socket.getOutputStream().close();
			}
			catch (Exception ex)
			{
			}
			
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
   
	private void doRelease()
   {
		closeConnection();
		
		_account = null;
		_sessionId = null;
		_listener = null;
		
		_accountService = null;
		_msgService = null;
		_systemService = null;
   }
}
