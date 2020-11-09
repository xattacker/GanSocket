package com.xattacker.gan;

import java.net.Socket;
import java.util.Hashtable;

public class ClientConnectiontManager
{
	private static ClientConnectiontManager iInstance;

	private Hashtable<String, Socket> iClientSockets = null;

	private ClientConnectiontManager()
	{
		iClientSockets = new Hashtable<String, Socket>();
	}

	public static ClientConnectiontManager getInstance()
	{
		if (iInstance == null)
		{
			iInstance = new ClientConnectiontManager();
		}

		return iInstance;
	}

	public Hashtable<String, Socket> getHandleClientSockets()
	{
		return iClientSockets;
	}

	public boolean isClientExisted(String aClientName)
	{
		return iClientSockets.containsKey(aClientName);
	}

	public Socket getClientSocket(String aClientName)
	{
		Socket socket = iClientSockets.get(aClientName);

		return socket;
	}

	public void addClientSocket(String aClientName, Socket aSocket)
	{
		iClientSockets.put(aClientName, aSocket);
	}
}
