package com.xattacker.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public final class IOUtility
{
	private IOUtility()
	{
		
	}
	
	public static void readResponse(InputStream aIn, ByteArrayOutputStream aBos) throws Exception
	{
		if (aIn != null && aBos != null)
		{
		  byte[] temp = new byte[256];
		  int index = -1;
		  
		  do
		  {
			  while ((index = aIn.read(temp)) != -1)
			  {
				  aBos.write(temp, 0, index);
				  
				  if (index < temp.length)
				  {
					  break;
				  }
			  }
			  
		  } while (aBos.size() == 0);
		  
		  temp = null;
		}
	}
}
