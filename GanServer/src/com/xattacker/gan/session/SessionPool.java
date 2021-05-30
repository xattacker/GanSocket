package com.xattacker.gan.session;

import java.net.Socket;
import java.util.Hashtable;

import com.xattacker.util.Logger;

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
	
	public boolean addSession(String aAccount, Socket aSocket, StringBuilder aSessionId)
	{
		boolean result = false;
		
		if (_sessions != null)
		{
			synchronized(_sessions)
			{
				if (_sessions.containsKey(aAccount))
				{
					SessionInfo session = _sessions.remove(aAccount);
					try
					{
						session.getProcess().close();
					}
					catch (Exception ex)
					{
					}

					Logger.instance().warn("account [" + aAccount + "]  was login by another connection, remove it from session pool");
				}
				
				
				SessionInfo session = new SessionInfo(aAccount);
				_sessions.put(aAccount, session);
				
				SessionConnectionProcess process = new SessionConnectionProcess(aAccount, session._sessionId, aSocket);
				session.setProcess(process);
				process.start();
				
				aSessionId.append(session.getSessionId());
				
				result = true;
			}
		}
		
		return result;
	}
	
	public boolean checkSession(String aAccount, String aSessionId)
	{
		boolean result = false;
		
		if (aSessionId != null && aSessionId.length() > 0 && _sessions != null)
		{
			synchronized(_sessions)
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
		}
		
		return result;
	}
	
	public void removeSession(String aAccount)
	{
		if (_sessions != null)
		{
			synchronized(_sessions)
			{
				try
				{
					SessionInfo session = _sessions.remove(aAccount);
					session.getProcess().close();
					session.setProcess(null);
				}
				catch (Throwable th)
				{
				}
			}
		}
	}
	
	private void doRelease()
	{
		if (_sessions != null)
		{
			_sessions.forEach(
						(account, session) -> {
						try
						{
							session._process.close();
							session.setProcess(null);
						}
						catch (Exception ex)
						{
						}
					});
			
			_sessions.clear();
			_sessions = null;
		}
	}
}
