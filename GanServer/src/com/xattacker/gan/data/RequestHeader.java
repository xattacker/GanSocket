package com.xattacker.gan.data;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.xattacker.json.JsonBuilderVisitor;
import com.xattacker.json.JsonSerializable;
import com.xattacker.json.JsonUtility;

public final class RequestHeader implements JsonSerializable
{
	@SerializedName("Type")
	private FunctionType _type;
	
	@SerializedName("Owner")
	private String _owner;
	
	@SerializedName("SessionId")
	private String _sessionId;
	
	@SerializedName("DeviceType")
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
		return JsonUtility.createGson(
			new JsonBuilderVisitor() 
			{
				@Override
				public void onBuilderPrepared(GsonBuilder aBuilder)
				{
					aBuilder.registerTypeAdapter(FunctionType.class, new FunctionTypeJsonSerializer());
				}
			}).toJson(this);
	}
}
