package com.xattacker.gan.data;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;

public final class FunctionTypeJsonSerializer implements JsonSerializer<FunctionType>, JsonDeserializer<FunctionType>
{
	@Override
	public FunctionType deserialize(JsonElement aArg0, Type aArg1, JsonDeserializationContext aArg2) throws JsonParseException
	{
		return FunctionType.parse(aArg0.getAsInt());
	}

	@Override
	public JsonElement serialize(FunctionType aArg0, Type aArg1, JsonSerializationContext aArg2)
	{
		return new JsonPrimitive(aArg0.value());
	}
}
