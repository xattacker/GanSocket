package com.xattacker.gan.msg;

import com.google.gson.annotations.SerializedName;
import com.xattacker.json.JsonObject;
import com.xattacker.util.GUID;

public final class MsgData extends JsonObject
{
	@SerializedName("id")
	private String _id;
	
	@SerializedName("sender")
	private String _sender;
	
	@SerializedName("time")
	private long _time;
	
	@SerializedName("message")
	private String _message;
	
	public MsgData()
	{
		_id = GUID.generateGUID();
		_time = System.currentTimeMillis();
	}
	
	public String getId()
	{
		return _id;
	}
	
	public void setId(String aId)
	{
		_id = aId;
	}
	
	public String getSender()
	{
		return _sender;
	}
	
	public void setSender(String aSender)
	{
		_sender = aSender;
	}
	
	public long getTime()
	{
		return _time;
	}
	
	public void setTime(long aTime)
	{
		_time = aTime;
	}
	
	public String getMessage()
	{
		return _message;
	}
	
	public void setMessage(String aMessage)
	{
		_message = aMessage;
	}
}
