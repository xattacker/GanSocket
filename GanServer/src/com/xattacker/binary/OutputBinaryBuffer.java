package com.xattacker.binary;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class OutputBinaryBuffer implements Closeable, BinaryWritable
{
	private OutputStream _out;
	
	public OutputBinaryBuffer(OutputStream aOut)
	{
		_out = aOut;
	}
	
	public void flush() throws IOException
	{
		if (_out != null)
		{
			_out.flush();
		}
	}

	public void close() throws IOException
	{
		if (_out != null)
		{
			_out.close();
		}
	}

	public void writeBinary(byte[] aData, int aOffset, int aLength) throws IOException
	{
		_out.write(aData, aOffset, aLength);
	}
	
	public void writeShort(short aShort) throws IOException
	{
		byte[] data = TypeConverter.shortToByte(aShort);

		writeBinary(data, 0, data.length);
	}
	
	public void writeInteger(int aInt) throws IOException
	{
		byte[] data = TypeConverter.intToByte(aInt);

		writeBinary(data, 0, data.length);
	}
	
	public void writeLong(long aLong) throws IOException
	{
		byte[] data = TypeConverter.longToByte(aLong);

		writeBinary(data, 0, data.length);
	}
	
	public void writeDouble(double aDouble) throws IOException
	{
		byte[] data = TypeConverter.doubleToByte(aDouble);

		writeBinary(data, 0, data.length);
	}
	
	public void writeString(String aStr) throws IOException, UnsupportedEncodingException
	{
		if (aStr == null || aStr.length() == 0)
		{
			writeInteger(0);
		}
		else
		{
			byte[] data = aStr.getBytes("UTF8");
			int length = data.length;

			writeInteger(length);
			writeBinary(data, 0, length);
		}
	}
	
	public void writeString(InputStream aIn, int aBufferSize) throws IOException
	{
		if (aIn == null || aIn.available() == 0)
		{
			writeInteger(0);
		}
		else
		{
			int length = aIn.available();
			writeInteger(length);

			byte temp[] = new byte[aBufferSize];
			int index = 0;
			
			while ((index = aIn.read(temp)) != -1)
			{
				writeBinary(temp, 0, index);
				flush();
			}
			
			temp = null;
		}
	}
	
	public void writeString(InputStream aIn) throws IOException
	{
		writeString(aIn, 5120);
	}
}
