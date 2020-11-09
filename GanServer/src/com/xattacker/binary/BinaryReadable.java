package com.xattacker.binary;

import java.io.IOException;

public interface BinaryReadable
{
	long available();

	byte[] readBinary(int aLength) throws IOException; 
	short readShort() throws IOException;
	int readInteger() throws IOException;
	long readLong() throws IOException;
	double readDouble() throws IOException;
	String readString() throws IOException;
}
