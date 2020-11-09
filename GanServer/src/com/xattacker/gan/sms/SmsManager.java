package com.xattacker.gan.sms;

import java.util.ArrayList;
import java.util.Hashtable;

public final class SmsManager
{
	private static SmsManager _instance;
	
	private Hashtable<String, ArrayList<SmsData>> _messages;
	
	private SmsManager()
	{
		_messages = new Hashtable<String, ArrayList<SmsData>>();
	}
	
	public static void constructInstance()
	{
		if (_instance == null)
		{
			_instance = new SmsManager();
		}
	}
	
	public static SmsManager instance()
	{
		return _instance;
	}
	
	public static void release()
	{
		
	}
	
	public void addSms(String aReceiver, SmsData aSms)
	{
		if (aReceiver != null && aSms != null)
		{
			ArrayList<SmsData> list = null;
			
			if (!_messages.containsKey(aReceiver))
			{
				list = new ArrayList<SmsData>();
				_messages.put(aReceiver, list);
			}
			else
			{
				list = _messages.get(aReceiver);
			}
			
			list.add(aSms);
		}
	}
	
	public boolean hasSms(String aReceiver)
	{
		return _messages != null ? _messages.containsKey(aReceiver) : false;
	}
	
	public ArrayList<SmsData> getSms(String aReceiver)
	{
		ArrayList<SmsData> list = null; 
		
		if (_messages.containsKey(aReceiver))
		{
			ArrayList<SmsData> existed = _messages.get(aReceiver);
			
			list = new ArrayList<SmsData>(existed);
			
			existed.clear();
		}
	
		return list;
	}
}
