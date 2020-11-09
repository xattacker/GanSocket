package com.xattacker.util;

import java.util.UUID;

public class GUID
{
	// in order to hide constructor
	private GUID()
	{		
	}

	// generate a 36 bytes guid string
	public static String generateGUID()
	{
		return UUID.randomUUID().toString();
	}
}
