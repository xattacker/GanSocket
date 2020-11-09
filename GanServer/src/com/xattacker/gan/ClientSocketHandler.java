package com.xattacker.gan;

import java.net.Socket;
import java.util.Hashtable;

public class ClientSocketHandler
{
	private static ClientSocketHandler iInstance;

	private Hashtable iClientSockets = null;

	private ClientSocketHandler()
	{
		iClientSockets = new Hashtable();
	}

	public static ClientSocketHandler getInstance()
	{
		if (iInstance == null)
		{
			iInstance = new ClientSocketHandler();
		}

		return iInstance;
	}

	public Hashtable getHandleClientSockets()
	{
		return iClientSockets;
	}

	public boolean isClientExisted(String aClientName)
	{
		return iClientSockets.containsKey(aClientName);
	}

	public Socket getClientSocket(String aClientName)
	{
		Socket socket = (Socket) iClientSockets.get(aClientName);

		return socket;
	}

	public void addClientSocket(String aClientName, Socket aSocket)
	{
		iClientSockets.put(aClientName, aSocket);
	}
}
