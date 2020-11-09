package com.xattacker.binary;

public interface BinarySerializable
{
	byte[] toBinary();

	boolean fromBinary(byte[] aContent);
}
