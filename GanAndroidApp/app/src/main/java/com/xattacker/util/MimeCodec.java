package com.xattacker.util;

import java.io.InputStream;
import java.io.OutputStream;

public class MimeCodec
{
	private final static int BUFFER_SIZE = 5120; // bytes
	
	// in order to hide constructor
	private MimeCodec()
	{
	}

	public static byte[] encode(byte aSrc[], int aLength)
	{
		byte[] buffer = new byte[aLength % 3 == 0 ? 4 * (aLength / 3) : 4 * (aLength / 3) + 4];
		byte[] temp = new byte[3];
		int index = 0;

		for (int cycle = 0; index < aLength; cycle++)
		{
			int i;

			for (i = 0; i < 3 && index < aLength; index++)
			{
				temp[i] = aSrc[index];
				i++;
			}

			byte[] encoded = new byte[4];
			getEncodedData(temp, encoded, i);

			for (i = 0; i < 4; i++)
			{
				buffer[4 * cycle + i] = encoded[i];
			}
		}

		return buffer;
	}
	
	public static String encode(byte aSrc[])
	{
		return new String(encode(aSrc, aSrc.length));
	}
	
	public static void encode(InputStream aIn, OutputStream aOut)  throws Exception
	{
		if (aIn == null || aOut == null)
		{
			return;
		}

		byte[] buffer = new byte[3 * BUFFER_SIZE]; // must be a multiple of 3
		byte[] encoded = null;
		int index = 0;
		
		while ((index = aIn.read(buffer)) != -1)
		{
			encoded = encode(buffer, index);
			
			aOut.write(encoded, 0, encoded.length);
			aOut.flush();
		}
		
		aOut.close();
		aIn.close();
		buffer = encoded = null;
		System.gc();
	}
	
	public static String encode(String aSrc)
	{
		return encode(aSrc.getBytes());
	}
	
	public static byte[] decode(byte[] aSrc, int aLength)
	{
		byte[] buffer = null;

		if (aSrc == null || aSrc.length == 0 || aLength < 1)
		{
			buffer = new byte[0];

			return buffer;
		}

		if (aSrc[aLength - 1] == 61 && aSrc[aLength - 2] == 61)
		{
			buffer = new byte[3 * (aLength / 4) - 2];
		}
		else if (aSrc[aLength - 1] == 61)
		{
			buffer = new byte[3 * (aLength / 4) - 1];
		}
		else
		{
			buffer = new byte[3 * (aLength / 4)];
		}

		byte temp[] = new byte[4];
		int index = 0;

		for (int cycle = 0; index < aLength; cycle++)
		{
			byte decoded[] = new byte[3];

			for (int i = 0; i < 4;)
			{
				temp[i] = aSrc[index];
				i++;
				index++;
			}

			int num = getDecodedData(temp, decoded);

			for (int i = 0; i < num; i++)
			{
				buffer[3 * cycle + i] = decoded[i];
			}
		}

		return buffer;
	}
	
	public static byte[] decode(String aSrc)
	{
		if (aSrc == null || aSrc.length() == 0)
		{
			return new byte[0];
		}
		
		byte[] src = aSrc.getBytes();
		int length = src.length;

		return decode(src, length);
	}
	
	public static void deocde(InputStream aIn, OutputStream aOut) throws Exception
	{
		if (aIn == null || aOut == null)
		{
			return;
		}

		byte[] buffer = new byte[4 * BUFFER_SIZE]; // must be a multiple of 4
		byte[] decoded = null;
		int index = 0;
		
		while ((index = aIn.read(buffer)) != -1)
		{
			decoded = decode(buffer, index);
			
			aOut.write(decoded, 0, decoded.length);
			aOut.flush();
		}
		
		aOut.close();
		aIn.close();
		buffer = decoded = null;
		System.gc();
	}
	
	public static String decodeToString(String aSrc)
	{
		return new String(decode(aSrc));
	}
	
	private static void getEncodedData(byte[] aSrc, byte[] aOut, int aNum)
	{
		for (int i = 0; i < 4; i++)
		{
			aOut[i] = 0;
		}
		
		byte temp = 0;

		aOut[0] |= aSrc[0] >> 2;
		aOut[0] &= 0x3f;
		aOut[1] |= (aSrc[0] & 3) << 4;
		aOut[1] &= 0x30;
		
		temp |= aSrc[1] >> 4;
		temp &= 0xf;
		
		aOut[1] |= temp;

		if (aNum != 1)
		{
			aOut[2] |= (aSrc[1] & 0xf) << 2;
			aOut[2] &= 0x3c;
			temp = 0;
			temp |= aSrc[2] >> 6;
			temp &= 3;
			aOut[2] |= temp;

			if (aNum != 2)
			{
				aOut[3] |= aSrc[2] & 0x3f;
			}
			else
			{
				aOut[3] |= 0x6a;
			}
		}
		else
		{
			aOut[2] |= 0x6a;
			aOut[3] |= 0x6a;
		}

		for (int i = 0; i < 4; i++)
		{
			if (aOut[i] < 26)
			{
				temp = (byte) (65 + (aOut[i] - 0));
			}
			else if (aOut[i] < 52 && aOut[i] >= 26)
			{
				temp = (byte) (97 + (aOut[i] - 26));
			}
			else if (aOut[i] < 62 && aOut[i] >= 52)
			{
				temp = (byte) (48 + (aOut[i] - 52));
			}
			else if (aOut[i] == 62)
			{
				temp = 43;
			}
			else if (aOut[i] == 63)
			{
				temp = 47;
			}
			else if (aOut[i] == 106)
			{
				temp = 61;
			}
			else
			{
				temp = 0;
			}

			aOut[i] = temp;
		}
	}

	private static int getDecodedData(byte aSrc[], byte aOut[])
	{
		byte temp;

		for (int i = 0; i < 4; i++)
		{
			if (aSrc[i] <= 90 && aSrc[i] >= 65)
			{
				temp = (byte) (0 + (aSrc[i] - 65));
			}
			else if (aSrc[i] <= 122 && aSrc[i] >= 97)
			{
				temp = (byte) (26 + (aSrc[i] - 97));
			}
			else if (aSrc[i] <= 57 && aSrc[i] >= 48)
			{
				temp = (byte) (52 + (aSrc[i] - 48));
			}
			else if (aSrc[i] == 43)
			{
				temp = 62;
			}
			else if (aSrc[i] == 47)
			{
				temp = 63;
			}
			else if (aSrc[i] == 61)
			{
				temp = 106;
			}
			else
			{
				temp = 0;
			}
			
			aSrc[i] = temp;
		}

		for (int i = 0; i < 3; i++)
		{
			aOut[i] = 0;
		}
		
		aOut[0] |= (aSrc[0] & 0x3f) << 2;
		aOut[0] &= 0xfc;
		
		temp = 0;
		temp |= aSrc[1] >> 4;
		temp &= 3;
		
		aOut[0] |= temp;

		if (aSrc[2] != 106)
		{
			aOut[1] |= (aSrc[1] & 0xf) << 4;
			aOut[1] &= 0xf0;
			
			temp = 0;
			temp |= aSrc[2] >> 2;
			temp &= 0xf;
			
			aOut[1] |= temp;

			if (aSrc[3] != 106)
			{
				aOut[2] |= (aSrc[2] & 3) << 6;
				aOut[2] &= 0xc0;
				
				temp = 0;
				temp |= aSrc[3];
				temp &= 0x3f;
				
				aOut[2] |= temp;
			}
			else
			{
				return 2;
			}
		}
		else
		{
			return 1;
		}

		return 3;
	}
}
