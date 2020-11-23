package com.xattacker.binary

interface BinarySerializable2
{
    fun <T> toBinary(aWritable: BinaryWritable<T>)

    fun fromBinary(aReadable: BinaryReadable): Boolean
}
