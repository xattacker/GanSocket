package com.xattacker.gan.data;

import java.io.InputStream;
import java.util.Arrays;

import com.xattacker.gan.exception.ConnectEOFException;

public final class PackChecker
{
	public final static String HEAD = "<GAN_PACK>";
   public final static byte[] HEAD_BYTE = HEAD.getBytes();

   public static boolean isValidPack(InputStream aIn, boolean aMarkable) throws ConnectEOFException
   {
   	boolean valid = false;

      try
      {
      	 if (aIn != null)
          {
      		 int size = aIn.available();
      		 if (size < 0)
      		 {
      			 throw new ConnectEOFException();
      		 }
      		 
	          if (size > HEAD_BYTE.length)
	          {
	         	 if (aIn.markSupported() && aMarkable)
	         	 {
	         		 aIn.mark(HEAD_BYTE.length);
	         	 }
	         	 
	         	 byte[] temp = new byte[HEAD_BYTE.length];
	         	 int index = aIn.read(temp);
	         	 if (index < 0)
	         	 {
	         		 throw new ConnectEOFException();
	         	 }
	         	 
	         	 valid = Arrays.equals(HEAD_BYTE, temp);
	         	 temp = null;
	          }
      	 }
      }
      catch (ConnectEOFException ex)
      {
      	throw ex;
      }
      catch (Exception ex)
      {
      	valid = false;
      }
      finally
      {
   		try
   		{
   			if (aIn.markSupported() && aMarkable)
   			{
   				aIn.reset(); // return to marked index
   			}
   		}
         catch (Exception ex2)
         {
         }
      }

      return valid;
   }
}
