package com.xattacker.binary

interface BinarySerializable
{
    fun toBinary(): ByteArray
    fun fromBinary(content: ByteArray): Boolean
}


interface BinarySerializable2
{
    fun <T> toBinary(writable: BinaryWritable<T>)
    fun fromBinary(readable: BinaryReadable): Boolean
}
