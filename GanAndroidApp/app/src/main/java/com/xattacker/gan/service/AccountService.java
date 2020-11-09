package com.xattacker.gan.service;

import com.xattacker.gan.GanAgent;
import com.xattacker.gan.data.FunctionType;
import com.xattacker.gan.data.ResponsePack;
import com.xattacker.binary.BinaryBuffer;

public abstract class AccountService extends ServiceFoundation
{
	public interface AccountServiceListener
	{
		void onLoginSucceed(String aAccount, String aSessionId);
		void onLogouted(String aAccount);
	}
	
	
	private String _account;
	private AccountServiceListener _listener;
	
	protected AccountService(GanAgent aAgent, AccountServiceListener aListener)
	{
		super(aAgent);
		
		_account = null;
		_listener = aListener;
	}
	
	public boolean login(String aAccount, String aPassword)
	{
		boolean result = false;

		try
		{
			BinaryBuffer buffer = new BinaryBuffer();
			buffer.writeString(aAccount);
			buffer.writeString(aPassword);
			
			ResponsePack response = send(FunctionType.LOGIN, buffer.getData());
			if (response != null)
			{
				result = response.getResult();
				
				if (result && _listener != null)
				{
					String session_id = new String(response.getContent());
					_account = aAccount;
					_listener.onLoginSucceed(aAccount, session_id);
				}
				
				response = null;
			}
			
			buffer = null;
		}
		catch (Exception ex)
		{
			android.util.Log.i("aaa", ex.toString());
		}
		
		return result;
	}
	
	public boolean logout()
	{
		boolean result = false;
		
		if (_account != null)
		{
			try
			{
				BinaryBuffer buffer = new BinaryBuffer();
				buffer.writeString(_account);
	
				ResponsePack response = send(FunctionType.LOGOUT, buffer.getData());
				if (response != null)
				{
					result = response.getResult();
					
					if (result && _listener != null)
					{
						_listener.onLogouted(_account);
						_account = null;
					}
				}
				
				buffer = null;
			}
			catch (Exception ex)
			{
				android.util.Log.i("aaa", ex.toString());
			}
		}
		
		return result;
	}
	
	public boolean isAccountExisted(String aAccount)
	{
		boolean existed = false;
		
		
		return existed;
	}
	
	public boolean registerAccount
	(
	String aAccout, 
	String aPassword, 
	byte[] aExtra
	)
	{
		boolean result = false;
		
		try
		{
			BinaryBuffer buffer = new BinaryBuffer();
			buffer.writeString(aAccout);
			buffer.writeString(aPassword);
			
			if (aExtra != null && aExtra.length > 0)
			{
				int size = aExtra.length;
				buffer.writeInteger(size);
				buffer.writeBinary(aExtra, 0, size);
			}
			else
			{
				buffer.writeInteger(0);
			}

			ResponsePack response = send(FunctionType.REGISTER_ACCOUNT, buffer.getData());
			if (response != null)
			{
				result = response.getResult();
			}
			
			buffer = null;
		}
		catch (Exception ex)
		{
			android.util.Log.i("aaa", ex.toString());
		}
		
		return result;
	}
}
