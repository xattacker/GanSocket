package com.xattacker.gan;

import java.net.ServerSocket;
import java.net.Socket;

import com.xattacker.gan.msg.MsgManager;
import com.xattacker.gan.session.SessionPool;
import com.xattacker.util.Logger;

public class GanServer extends Thread
{
	private ServerSocket _server = null;

	public static void main(String arg[]) throws Exception
	{
		GanServer server = new GanServer();
		server.start();
		
		String imp_version = server.getClass().getPackage().getImplementationVersion();
		if (imp_version != null && imp_version.length() > 0)
		{
			imp_version = "[v: " + imp_version + "]";
		}
		else
		{
			imp_version = "";
		}
		
		Logger.instance().log("GanServer " + imp_version + " started on port " + server._server.getLocalPort() + " ~~");
	}

	public GanServer() throws Exception
	{
		Logger.initial();
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
				
				ClientSocketProcess process = new ClientSocketProcess(socket);
				process.start();
				Logger.instance().debug("got connection");
				
				Thread.sleep(500);
			}
			catch (Exception ex)
			{
				Logger.instance().except(ex);
			}
		}
	}
}
