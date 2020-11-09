package com.xattacker.binary;

public interface BinarySerializable2
{
	void toBinary(BinaryWritable aWritable);

	boolean fromBinary(BinaryReadable aReadable); 
}
