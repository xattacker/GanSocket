package com.xattacker.json;

public interface JsonSerializable
{
	String toJson();
	
	boolean fromJson(String aJson);
}
