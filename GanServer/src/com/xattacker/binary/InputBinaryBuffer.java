package com.xattacker.binary;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class InputBinaryBuffer implements Closeable, BinaryReadable
{
	private InputStream _in;

	public InputBinaryBuffer(InputStream aIn)
	{
		_in = aIn;
	}
	
	public long available()
	{
		int available = 0;
		
		try
		{
			available = _in.available();
		}
		catch (Exception ex)
		{
			available = 0;
		}

		return available;
	}

	public void close() throws IOException
	{
		if (_in != null)
		{
			_in.close();
		}
	}

	public int read(byte[] aData) throws IOException
	{
		return _in.read(aData);
	}
	
	public int read(byte[] aData, int aOffset, int aLength) throws IOException
	{
		return _in.read(aData, aOffset, aLength);
	}

	public byte[] readBinary(int aLength) throws IOException
	{
		if (_in.available() < aLength)
		{
			throw new IOException("Failed to read binary data from a input stream (Index out of bound)");
		}

		byte[] data = new byte[aLength];
		_in.read(data);

		return data;
	}

	public short readShort() throws IOException
	{
		if (_in.available() < 2)
		{
			throw new IOException("Failed to read a short from a input stream (Index out of bound)");
		}
		else
		{
			byte[] data = new byte[2];
			_in.read(data);

			return TypeConverter.byteToShort(data);
		}
	}

	public int readInteger() throws IOException
	{
		if (_in.available() < 4)
		{
			throw new IOException("Failed to read a integer from a input stream (Index out of bound)");
		}
		else
		{
			byte[] data = new byte[4];
			_in.read(data);

			return TypeConverter.byteToInt(data);
		}
	}

	public long readLong() throws IOException
	{
		if (_in.available() < 8)
		{
			throw new IOException("Failed to read a long from a input stream (Index out of bound)");
		}
		else
		{
			byte[] data = new byte[8];
			_in.read(data);

			return TypeConverter.byteToLong(data);
		}
	}

	public double readDouble() throws IOException
	{
		if (_in.available() < 8)
		{
			throw new IOException("Failed to read a double from a input stream (Index out of bound)");
		}
		else
		{
			byte[] data = new byte[8];
			_in.read(data);

			return TypeConverter.byteToDouble(data);
		}
	}

	public String readString() throws IOException
	{
		int length = readInteger();

		if (length == 0)
		{
			return "";
		}

		byte[] data = readBinary(length);

		return new String(data, "UTF8");
	}
}
