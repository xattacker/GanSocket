package com.xattacker.gan;

import java.net.Socket;
import java.util.Hashtable;

public class ClientConnectiontManager
{
	private static ClientConnectiontManager _instance;

	private Hashtable<String, Socket> _connections = null;
	private final Object _lockObj = new Object();

	private ClientConnectiontManager()
	{
		_connections = new Hashtable<String, Socket>();
	}

	public synchronized static ClientConnectiontManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new ClientConnectiontManager();
		}

		return _instance;
	}

	public boolean isConnectionExisted(String aClientName)
	{
		synchronized (_lockObj)
		{
			return _connections.containsKey(aClientName);
		}
	}

	public Socket getConnectionSocket(String aClientName)
	{
		Socket socket = _connections.get(aClientName);

		return socket;
	}

	public void addConnectiion(String aClientName, Socket aSocket)
	{
		synchronized (_lockObj)
		{
			_connections.put(aClientName, aSocket);
		}
	}
}
