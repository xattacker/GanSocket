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
		
		_server = new ServerSocket(5999, 30);
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
		
		_server = null;
	}

	public void run()
	{
		Socket socket = null;

		while (_server != null)
		{
			try
			{
				socket = _server.accept();
				socket.setOOBInline(true);
				socket.setKeepAlive(true);
				
				GanClientConnectionProcess process = new GanClientConnectionProcess(socket);
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
