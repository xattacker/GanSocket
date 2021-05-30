package com.xattacker.gan.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.xattacker.binary.BinaryBuffer;
import com.xattacker.binary.InputBinaryBuffer;
import com.xattacker.binary.OutputBinaryBuffer;
import com.xattacker.binary.TypeConverter;
import com.xattacker.gan.exception.ConnectEOFException;
import com.xattacker.util.Logger;

public final class PackChecker
{
	private final static String HEAD = "<GAN_PACK>";
   private final static byte[] HEAD_BYTE = HEAD.getBytes();
	private final static int INT_LENGTH = 4;
   private final static int VALID_LENGTH = HEAD_BYTE.length + INT_LENGTH;
   
	public class ValidResult
	{
		public boolean valid = false;
		public int length = 0;
	}

   public static ValidResult isValidPack(InputStream aIn, int aWaitCount, boolean aMarkable) throws ConnectEOFException
   {
   	ValidResult result = new PackChecker().new ValidResult();

      try
      {
      	 if (aIn != null)
          {
             int wait_count = 0;
         	 while (aIn.available() < VALID_LENGTH && wait_count < aWaitCount)
         	 {
         		  wait_count++;
         		  Thread.sleep(50);
         	 }
         	 
      		 if (aIn.available() > VALID_LENGTH)
      		 {
	         	 if (aIn.markSupported() && aMarkable)
	         	 {
	         		 aIn.mark(HEAD_BYTE.length);
	         	 }
	         	 
	         	 byte[] temp = new byte[HEAD_BYTE.length];
	         	 aIn.read(temp);
	         	 
	         	 result.valid = Arrays.equals(HEAD_BYTE, temp);
	 	          if (result.valid)
		          {
		         	 InputBinaryBuffer buffer = new InputBinaryBuffer(aIn);
		         	 result.length = buffer.readInteger();
		          }
      		 }
      	 }
      }
      catch (ConnectEOFException ex)
      {
      	throw ex;
      }
      catch (Exception ex)
      {
      	Logger.instance().except(ex);
      	result.valid = false;
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

      return result;
   }
   
   public static void packData(ResponsePack response, OutputStream out) throws Exception
   {
		 BinaryBuffer buffer = new BinaryBuffer();
		 response.toBinary(buffer);
		 
		 out.write(PackChecker.HEAD_BYTE);
		 out.write(TypeConverter.intToByte((int)buffer.getLength()));
		 
		 OutputBinaryBuffer obb = new OutputBinaryBuffer(out);
		 obb.writeBinary(buffer.getData(), 0, (int)buffer.getLength());
		 obb.flush();
   }
}
