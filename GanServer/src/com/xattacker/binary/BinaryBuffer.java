package com.xattacker.binary;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class BinaryBuffer implements BinaryReadable, BinaryWritable
{	
	private byte _data[];
	private long _index;
	private long _available;
	private long _length;

	public BinaryBuffer()
	{
		clear();
	}

	public BinaryBuffer(byte[] aData)
	{
		_data = aData;
		_index = 0;
		_available = aData != null ? aData.length : 0;
		_length = aData != null ? aData.length : 0;
	}

	public BinaryBuffer(BinaryBuffer aBuffer)
	{
		this(aBuffer.getData());
	}

	public byte[] getData()
	{
		return _data;
	}
	
	public long available()
	{
		return _available;
	}
	
	public long getLength()
	{
		return _length;
	}

	public boolean isEmpty()
	{
		return _length == 0;
	}

	public void clear()
	{
		_index = 0;
		_data = null;
		_available = 0;
		_length = 0;
	}

	public long getCurrentIndex()
	{
		return _index;
	}

	public void seekToHead()
	{
		_index = 0;
	}

	public void seekTo(long aIndex) throws Exception
	{
		if ((aIndex = aIndex > 0 ? aIndex : 0) > _length)
		{
			throw new Exception("Failed to seek to index [" + aIndex + "] in buffer.");
		}
		else
		{
			_index = aIndex;
		}
	}

	public void seekToEnd()
	{
		_index = _length;
	}

	public void writeBuffer(BinaryBuffer aBuffer)
	{
		byte[] data = aBuffer.getData();

		writeBinary(data, 0, data.length);
	}

	public void writeBinary(byte[] aData, int aOffset, int aLength)
	{
		append(aLength);
		System.arraycopy(aData, aOffset, _data, (int)_index, aLength);

		_index += aLength;
		_length = _index <= _length ? _length : _index;
	}

	public byte[] readBinary(int aLength) throws IOException
	{
		if (_index + aLength > _length)
		{
			throw new IOException("Failed to read binary data from a binary array (Index out of bound)");
		}

		byte[] data = new byte[aLength];

		System.arraycopy(_data, (int)_index, data, 0, aLength);
		_index += aLength;

		return data;
	}

	public void writeShort(short aShort)
	{
		byte[] data = TypeConverter.shortToByte(aShort);

		writeBinary(data, 0, data.length);
	}

	public short readShort() throws IOException
	{
		if (_index + 2 > _length)
		{
			throw new IOException("Failed to read a short from a binary array (Index out of bound)");
		}
		else
		{
			byte[] data = new byte[2];

			System.arraycopy(_data, (int)_index, data, 0, 2);
			_index += 2;

			return TypeConverter.byteToShort(data);
		}
	}

	public void writeInteger(int aInt)
	{
		byte[] data = TypeConverter.intToByte(aInt);

		writeBinary(data, 0, data.length);
	}

	public int readInteger() throws IOException
	{
		if (_index + 4 > _length)
		{
			throw new IOException("Failed to read a integer from a binary array (Index out of bound)");
		}
		else
		{
			byte[] data = new byte[4];

			System.arraycopy(_data, (int)_index, data, 0, 4);
			_index += 4;

			return TypeConverter.byteToInt(data);
		}
	}

	public void writeLong(long aLong)
	{
		byte[] data = TypeConverter.longToByte(aLong);

		writeBinary(data, 0, data.length);
	}

	public long readLong() throws IOException
	{
		if (_index + 8 > _length)
		{
			throw new IOException("Failed to read a long from a binary array (Index out of bound)");
		}
		else
		{
			byte[] data = new byte[8];

			System.arraycopy(_data, (int)_index, data, 0, 8);
			_index += 8;

			return TypeConverter.byteToLong(data);
		}
	}

	public void writeDouble(double aDouble)
	{
		byte[] data = TypeConverter.doubleToByte(aDouble);

		writeBinary(data, 0, data.length);
	}

	public double readDouble() throws IOException
	{
		if (_index + 8 > _length)
		{
			throw new IOException("Failed to read a double from a binary array (Index out of bound)");
		}
		else
		{
			byte[] data = new byte[8];

			System.arraycopy(_data, (int)_index, data, 0, 8);
			_index += 8;

			return TypeConverter.byteToDouble(data);
		}
	}

	public void writeString(String aStr) throws UnsupportedEncodingException
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

	private void append(int aLength)
	{
		if (_available < _index + aLength)
		{
			byte[] new_data = new byte[(int)_index + aLength];

			if (_data != null)
			{
				// copy original data to new data
				System.arraycopy(_data, 0, new_data, 0, _data.length);
			}

			_data = new_data;
			_available = new_data.length;
		}
	}
}
