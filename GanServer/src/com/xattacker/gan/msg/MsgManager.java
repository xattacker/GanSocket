package com.xattacker.gan.msg;

import java.util.ArrayList;
import java.util.Hashtable;

public final class MsgManager
{
	private static MsgManager _instance;
	
	private Hashtable<String, ArrayList<MsgData>> _messages;
	
	private MsgManager()
	{
		_messages = new Hashtable<String, ArrayList<MsgData>>();
	}
	
	public static void constructInstance()
	{
		if (_instance == null)
		{
			_instance = new MsgManager();
		}
	}
	
	public static MsgManager instance()
	{
		return _instance;
	}
	
	public static void release()
	{
		
	}
	
	public void addSms(String aReceiver, MsgData aSms)
	{
		if (aReceiver != null && aSms != null)
		{
			ArrayList<MsgData> list = null;
			
			if (!_messages.containsKey(aReceiver))
			{
				list = new ArrayList<MsgData>();
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
	
	public ArrayList<MsgData> getSms(String aReceiver)
	{
		ArrayList<MsgData> list = null; 
		
		if (_messages.containsKey(aReceiver))
		{
			ArrayList<MsgData> existed = _messages.get(aReceiver);
			
			list = new ArrayList<MsgData>(existed);
			
			existed.clear();
		}
	
		return list;
	}
}
