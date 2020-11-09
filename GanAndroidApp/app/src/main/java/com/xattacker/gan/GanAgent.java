package com.xattacker.gan;

import java.net.Socket;

public interface GanAgent
{
	Socket createSocket() throws Exception;
	String getAccount();
	String getSessionId();
}
