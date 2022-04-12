package com.xattacker.binary

import java.io.IOException

// fluent interface
interface BinaryWritable<T>
{
    @Throws(IOException::class)
    fun writeBinary(data: ByteArray, offset: Int, length: Int): T

    @Throws(IOException::class)
    fun writeShort(short: Short): T

    @Throws(IOException::class)
    fun writeInteger(int: Int): T

    @Throws(IOException::class)
    fun writeLong(long: Long): T

    @Throws(IOException::class)
    fun writeDouble(double: Double): T

    @Throws(IOException::class)
    fun writeString(str: String): T
}
