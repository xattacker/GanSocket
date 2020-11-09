package com.xattacker.gan.data;

import com.xattacker.binary.BinaryReadable;
import com.xattacker.binary.BinarySerializable2;
import com.xattacker.binary.BinaryWritable;

public class ResponsePack implements BinarySerializable2
{
	private boolean _result;
	private int _id;
	private byte[] _content;

	public boolean getResult()
	{
		return _result;
	}

	public void setResult(boolean aResult)
	{
		_result = aResult;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public void setId(int aId)
	{
		_id = aId;
	}
	
	public void setContent(byte[] aContent)
	{
		_content = aContent;
	}
	
	public byte[] getContent()
	{
		return _content;
	}

	@Override
	public void toBinary(BinaryWritable aWritable)
	{
		if (aWritable != null)
		{
			try
			{
				aWritable.writeShort(_result ? (short)1 : (short)0);
				aWritable.writeInteger(_id);
				
				if (_content != null && _content.length > 0)
				{
					int size = _content.length;
					
					aWritable.writeInteger(size);
					aWritable.writeBinary(_content, 0, size);
				}
				else
				{
					aWritable.writeInteger(0);
				}
			}
			catch (Exception ex)
			{
			}
		}
	}

	@Override
	public boolean fromBinary(BinaryReadable aReadable)
	{
		boolean result = false;
		
		if (aReadable != null)
		{
			try
			{
				_result = aReadable.readShort() == 1;
				_id = aReadable.readInteger();
				
				int size = aReadable.readInteger();
				if (size > 0)
				{
					_content = aReadable.readBinary(size);
				}
				else
				{
					_content = null;
				}
				
				result = true;
			}
			catch (Exception ex)
			{
				result = false;
			}
		}
		
		return result;
	}
}
