package com.xattacker.gan.session;

import com.xattacker.util.GUID;

public final class SessionInfo
{
	String _account;
	String _sessionId;
	long _loginTime;
	SessionConnectionProcess _process;
	
	SessionInfo(String aAccount)
	{
		_account = aAccount;
		_sessionId = GUID.generateGUID();
		_loginTime = System.currentTimeMillis();
	}
	
	public String getAccount()
	{
		return _account;
	}
	
	public void setAccount(String aAccount)
	{
		_account = aAccount;
	}
	
	public String getSessionId()
	{
		return _sessionId;
	}
	
	public void setSessionId(String aSessionId)
	{
		_sessionId = aSessionId;
	}
	
	public long getLoginTime()
	{
		return _loginTime;
	}
	
	public void setLoginTime(long aTime)
	{
		_loginTime = aTime;
	}

	public SessionConnectionProcess getProcess()
	{
		return _process;
	}

	public void setProcess(SessionConnectionProcess aProcess)
	{
		_process = aProcess;
	}
}
