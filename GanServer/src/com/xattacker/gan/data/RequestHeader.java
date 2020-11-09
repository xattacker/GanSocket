package com.xattacker.gan.data;

import org.json.JSONObject;

import com.xattacker.json.JsonSerializable;

public final class RequestHeader implements JsonSerializable
{
	private FunctionType _type;
	private String _owner;
	private String _sessionId;
	private int _deviceType;
	
	public FunctionType getType()
	{
		return _type;
	}
	
	public void setType(FunctionType aType)
	{
		_type = aType;
	}
	
	public String getOwner()
	{
		return _owner;
	}
	
	public void setOwner(String aOwner)
	{
		_owner = aOwner;
	}
	
	public String getSessionId()
	{
		return _sessionId;
	}
	
	public void setSessionId(String aSessionId)
	{
		_sessionId = aSessionId;
	}
	
	public int getDeviceType()
	{
		return _deviceType;
	}
	
	public void setDeviceType(int aType)
	{
		_deviceType = aType;
	}

	@Override
	public String toJson()
	{
		String json = null;
		
		try
		{
			JSONObject jobj = new JSONObject();
			jobj.put("Type", _type.value());
			jobj.put("Owner", _owner != null ? _owner : "");
			jobj.put("SessionId", _sessionId != null ? _sessionId : "");
			
			json = jobj.toString();
		}
		catch (Exception ex)
		{
		}
		
		return json;
	}

	@Override
	public boolean fromJson(String aJson)
	{
		boolean result = false;
		
		if (aJson != null && aJson.length() > 0)
		{
			try
			{
				JSONObject jobj = new JSONObject(aJson);
				_type = FunctionType.parse(jobj.optInt("Type", 0));
				_owner = jobj.optString("Owner", "");
				_sessionId = jobj.optString("SessionId", "");
				
				result = true;
			}
			catch (Exception ex)
			{
				result = false;
			}
		}
		
		return result;
	}
}
