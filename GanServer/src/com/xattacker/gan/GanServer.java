package com.xattacker.gan;

import java.net.ServerSocket;
import java.net.Socket;

import com.xattacker.gan.account.SessionPool;
import com.xattacker.gan.msg.MsgManager;

public class GanServer extends Thread
{
	private ServerSocket _server = null;

	public static void main(String arg[]) throws Exception
	{
		GanServer server = new GanServer();
		server.start();
		System.out.println("GanServer started ~~");
	}

	public GanServer() throws Exception
	{
		SessionPool.initial();
		MsgManager.constructInstance();
		
		_server = new ServerSocket(5999, 1);
	}

	public void terminate()
	{
		try
		{
			_server.close();
		}
		catch (Exception ex)
		{
		}
	}

	public void run()
	{
		Socket socket = null;

		while (true)
		{
			try
			{
				socket = _server.accept();
				socket.setOOBInline(true);
				socket.setKeepAlive(true);
				
				GanClientConnection process = new GanClientConnection(socket);
				process.start();
				System.out.println("got connection");
				
				Thread.sleep(500);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
