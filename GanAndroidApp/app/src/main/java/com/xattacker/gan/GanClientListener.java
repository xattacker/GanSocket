package com.xattacker.gan;

public interface GanClientListener
{
	void onAccountLogined(String aAccount);
	void onAccountClosed(String aAccount);
	
	void onSMSReceived(String aSender, long aTime, String aMsg);
}
