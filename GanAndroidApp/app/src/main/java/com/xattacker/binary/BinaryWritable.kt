package com.xattacker.binary

import java.io.IOException

// fluent interface
interface BinaryWritable<T>
{
    @Throws(IOException::class)
    fun writeBinary(aData: ByteArray, aOffset: Int, aLength: Int): T

    @Throws(IOException::class)
    fun writeShort(aShort: Short): T

    @Throws(IOException::class)
    fun writeInteger(aInt: Int): T

    @Throws(IOException::class)
    fun writeLong(aLong: Long): T

    @Throws(IOException::class)
    fun writeDouble(aDouble: Double): T

    @Throws(IOException::class)
    fun writeString(aStr: String): T
}
