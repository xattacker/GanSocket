package com.xattacker.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;


public final class JsonUtility
{
	public static String serialize(Object obj) throws Exception
	{
		return serialize(obj, null);
	}
	
	public static String serialize(Object obj, JsonBuilderVisitor aVisitor) throws Exception
	{
//		ObjectMapper mapper = new ObjectMapper();
//		return mapper.writeValueAsString(obj);

		return createGson(aVisitor).toJson(obj);
	}
	
	public static Gson createGson(JsonBuilderVisitor aVisitor)
	{
	   GsonBuilder builder = new GsonBuilder();  
	   
	   if (aVisitor != null)
	   {
	   	aVisitor.onBuilderPrepared(builder);
	   }

		builder .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		
	   // ignore expose field to serialize
	   builder.setExclusionStrategies
      (
          new ExclusionStrategy()
          {
              @Override
              public boolean shouldSkipField(FieldAttributes f)
              {
                  return f.getAnnotation(Expose.class) != null;
              }

              @Override
              public boolean shouldSkipClass(Class<?> clazz)
              {
                  return false;
              }
          }
      );
	   
	   return builder.create();   
	}
	
	public static Gson createGson()
	{
		return createGson(null);
	}
	
	public static <T> T fromJson(String aJson, TypeToken<T> aToken, JsonBuilderVisitor aVisitor)
	{
		Gson gson = createGson(aVisitor);
		return gson.fromJson(aJson, aToken.getType());
	}
	
	public static <T> T fromJson(String aJson, TypeToken<T> aToken)
	{
		return fromJson(aJson, aToken, null);
	}
	
	public static <T> T fromJson(String aJson, Class<T> aType, JsonBuilderVisitor aVisitor)
	{
		Gson gson = createGson(aVisitor);
		return gson.fromJson(aJson, aType);
	}
	
	public static <T> T fromJson(String aJson, Class<T> aType)
	{
		return fromJson(aJson, aType, null);
	}
}
