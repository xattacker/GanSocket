package com.xattacker.gan.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.xattacker.binary.BinaryBuffer;
import com.xattacker.binary.InputBinaryBuffer;
import com.xattacker.binary.OutputBinaryBuffer;
import com.xattacker.binary.TypeConverter;
import com.xattacker.gan.exception.ConnectEOFException;

public final class PackChecker
{
	private final static String HEAD = "<GAN_PACK>";
   private final static byte[] HEAD_BYTE = HEAD.getBytes();
   
	public class ValidResult
	{
		public boolean valid = false;
		public int length = 0;
	}

   public static ValidResult isValidPack(InputStream aIn, boolean aMarkable) throws ConnectEOFException
   {
   	ValidResult result = new PackChecker().new ValidResult();

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
	         	 
	         	 result.valid = Arrays.equals(HEAD_BYTE, temp);
	         	 
	 	         if (result.valid)
		         {
		         	InputBinaryBuffer buffer = new InputBinaryBuffer(aIn);
		         	result.length = buffer.readInteger();
		         }
	 	         
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
