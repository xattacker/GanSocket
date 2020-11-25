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
		if (_instance != null)
		{
			_instance.doRelease();
		}
		
		_instance = null;
	}
	
	public void addMsg(String aReceiver, MsgData aMsg)
	{
		if (aReceiver != null && aMsg != null)
		{
			synchronized(_messages)
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
				
				list.add(aMsg);
			}
		}
	}
	
	public boolean hasMsg(String aReceiver)
	{
		synchronized(_messages)
		{
			return _messages != null ? _messages.containsKey(aReceiver) : false;
		}
	}
	
	public ArrayList<MsgData> getMsgs(String aReceiver)
	{
		ArrayList<MsgData> list = null; 
		
		synchronized(_messages)
		{
			if (_messages.containsKey(aReceiver))
			{
				ArrayList<MsgData> existed = _messages.get(aReceiver);
				list = new ArrayList<MsgData>(existed);
				
				existed.clear();
			}
		}
		
		return list;
	}
	
	private void doRelease()
	{
		_messages.clear();
		_messages = null;
	}
}
