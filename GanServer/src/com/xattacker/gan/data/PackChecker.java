package com.xattacker.gan.data;

import java.io.InputStream;
import java.util.Arrays;

import com.xattacker.binary.BinaryBuffer;
import com.xattacker.gan.exception.ConnectEOFException;

public final class PackChecker
{
	public final static String HEAD = "<GAN_PACK>";
   public final static byte[] HEAD_BYTE = HEAD.getBytes();
   
   public static boolean isValidPack(byte[] aContent)
   {
   	 boolean valid = false;
   	 BinaryBuffer buffer = null;
   	 
       try
       {
           if (aContent != null && aContent.length > HEAD.length())
           {
               buffer = new BinaryBuffer(aContent);
               if (buffer.readShort() == HEAD.length())
               {
                   buffer.seekToHead();

                   valid = buffer.readString().equals(HEAD);
               }
           }
       }
       catch (Exception ex)
       {
      	 valid = false;
       }
       finally
       {
      	 buffer = null;
       }
       
       return valid;
   }
   
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
