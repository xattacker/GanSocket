package com.xattacker.json;

import com.google.gson.GsonBuilder;

public abstract class JsonObject implements JsonSerializable, JsonBuilderVisitor
{
	public void onBuilderPrepared(GsonBuilder aBuilder)
	{	
	}
	
	@Override
	public String toJson()
	{
		return JsonUtility.createGson(this).toJson(this);
	}
}
