package com.xattacker.gan.account;

import java.util.Hashtable;

public final class SessionPool
{
	private static SessionPool _instance;
	
	private Hashtable<String, SessionInfo> _sessions;
	
	private SessionPool()
	{
		_sessions = new Hashtable<String, SessionInfo>();
	}
	
	public static void initial()
	{
		if (_instance == null)
		{
			_instance = new SessionPool();
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
	
	public static SessionPool instance()
	{
		return _instance;
	}
	
	public boolean addSession(String aAccount, StringBuilder aSessionId)
	{
		boolean result = false;
		
		if (_sessions != null)
		{
			if (_sessions.containsKey(aAccount))
			{
				_sessions.remove(aAccount);
				System.out.println("account [" + aAccount + "]  was login by another connection, remove it from session pool");
			}
			
			SessionInfo session = new SessionInfo(aAccount);
			_sessions.put(aAccount, session);
			
			aSessionId.append(session.getSessionId());
			
			result = true;
			session = null;
		}
		
		return result;
	}
	
	public boolean checkSession(String aAccount, String aSessionId)
	{
		boolean result = false;
		
		if (aSessionId != null && aSessionId.length() > 0 && _sessions != null)
		{
			if (_sessions.containsKey(aAccount))
			{
				SessionInfo session = _sessions.get(aAccount);
				result = session._sessionId.equals(aSessionId);
			}
			else
			{
				SessionInfo session = new SessionInfo(aAccount);
				session.setSessionId(aSessionId);
				_sessions.put(aAccount, session);
				
				result = true;
			}
		}
		
		return result;
	}
	
	public void removeSession(String aAccount)
	{
		if (_sessions != null && _sessions.containsKey(aAccount))
		{
			_sessions.remove(aAccount);
		}
	}
	
	private void doRelease()
	{
		if (_sessions != null)
		{
			_sessions.clear();
			_sessions = null;
		}
	}
}
