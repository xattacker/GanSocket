package com.xattacker.binary;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface BinaryWritable
{
   void writeBinary(byte[] aData, int aOffset, int aLength) throws IOException;
	void writeShort(short aShort) throws IOException;
	void writeInteger(int aInt) throws IOException;
	void writeLong(long aLong) throws IOException;
	void writeDouble(double aDouble) throws IOException;
	void writeString(String aStr) throws IOException, UnsupportedEncodingException;
}
