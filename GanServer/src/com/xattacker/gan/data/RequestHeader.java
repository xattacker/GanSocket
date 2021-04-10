package com.xattacker.gan.data;

import java.io.InputStream;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.xattacker.binary.InputBinaryBuffer;
import com.xattacker.json.JsonBuilderVisitor;
import com.xattacker.json.JsonObject;
import com.xattacker.json.JsonUtility;

public final class RequestHeader extends JsonObject
{
	@SerializedName("Type")
	private FunctionType _type;
	
	@SerializedName("Owner")
	private String _owner;
	
	@SerializedName("SessionId")
	private String _sessionId;
	
	@SerializedName("DeviceType")
	private int _deviceType;
	
	public static RequestHeader parseHeader(InputStream in) throws Exception
	{
		InputBinaryBuffer ibb = new InputBinaryBuffer(in);
		String json = ibb.readString();
		
		RequestHeader request = JsonUtility.fromJson(json, 
										RequestHeader.class, 		
										new JsonBuilderVisitor() 
										{
											@Override
											public void onBuilderPrepared(GsonBuilder aBuilder)
											{
												aBuilder.registerTypeAdapter(FunctionType.class, new FunctionTypeJsonSerializer());
											}
										});
		
		return request;
	}
	
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
	public void onBuilderPrepared(GsonBuilder aBuilder)
	{	
		aBuilder.registerTypeAdapter(FunctionType.class, new FunctionTypeJsonSerializer());
	}
}
