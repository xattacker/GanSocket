package com.xattacker.binary;

public class TypeConverter
{
	// in order to make constructor hide
	protected TypeConverter()
	{
	}

	public static byte[] charToByte(char aChar)
	{
		byte[] bytes = new byte[2];
		bytes[0] = (byte) ((aChar & 0xff00) >> 8);  
		bytes[1] = (byte) (aChar & 0x00ff);   

		return bytes;
	}
	
	public static byte[] charArrayToByteArray(char[] aChars)
	{
		byte[] bytes = new byte[aChars.length*2]; 
		
		for (int i = 0, length = aChars.length; i < length; i++) 
		{    
			bytes[2*i] = (byte) ((aChars[i] & 0xff00) >> 8);    
			bytes[2*i+1] = (byte) (aChars[i] & 0x00ff); 
		}
		
		return bytes;
	}

	public static char byteToChar(byte[] aBytes)
	{
		int value = aBytes[0];
		
		value &= 0xff;
		value |= ((short) aBytes[1] << 8);

		return (char) value;
	}

	public static byte[] shortToByte(short aShort)
	{
		short temp = aShort;
		byte[] bytes = new byte[2];

		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = new Integer(temp & 0xff).byteValue();
			temp = (short)(temp >> 8);
		}

		return bytes;
	}

	public static short byteToShort(byte[] aBytes)
	{
		short value = (short) aBytes[0];
		
		value &= 0xff;
		value |= ((short) aBytes[1] << 8);

		return value;
	}
	
	public static byte[] intToByte(int aInt)
	{
		int temp = aInt;
		byte[] bytes = new byte[4];

		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}

		return bytes;
	}

	public static int byteToInt(byte[] aBytes)
	{
		int value = aBytes[0];
		
		value &= 0xff;
		value |= (aBytes[1] << 8);
		
		value &= 0xffff;
		value |= (aBytes[2] << 16);
		
		value &= 0xffffff;
		value |= (aBytes[3] << 24);

		return value;
	}

	public static byte[] longToByte(long aLong)
	{
		long temp = aLong;
		byte[] bytes = new byte[8];
		
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = new Integer((int)temp & 0xff).byteValue();
			temp = temp >> 8;
		}

		return bytes;
	}

	public static long byteToLong(byte[] aBytes)
	{
		long value = aBytes[0];
		
		value &= 0xff;
		value |= ((long) aBytes[1] << 8);
		
		value &= 0xffff;
		value |= ((long) aBytes[2] << 16);
		
		value &= 0xffffff;
		value |= ((long) aBytes[3] << 24);
		
		value &= 0xffffffffl;
		value |= ((long) aBytes[4] << 32);
		
		value &= 0xffffffffffl;
		value |= ((long) aBytes[5] << 40);
		
		value &= 0xffffffffffffl;
		value |= ((long) aBytes[6] << 48);
		
		value &= 0xffffffffffffffl;
		value |= ((long) aBytes[7] << 56);
		
		return value;
	}
	
	public static byte[] doubleToByte(double aDouble)
	{
		return longToByte(Double.doubleToLongBits(aDouble));
	}
	
	public static double byteToDouble(byte[] aBytes)
	{
		return Double.longBitsToDouble(byteToLong(aBytes));
	}
}
