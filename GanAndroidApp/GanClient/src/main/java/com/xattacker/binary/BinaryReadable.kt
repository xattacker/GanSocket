package com.xattacker.binary

import java.io.IOException

interface BinaryReadable
{
    val available: Long

    @Throws(IOException::class)
    fun readBinary(aLength: Int): ByteArray?

    @Throws(IOException::class)
    fun readShort(): Short?

    @Throws(IOException::class)
    fun readInteger(): Int?

    @Throws(IOException::class)
    fun readLong(): Long?

    @Throws(IOException::class)
    fun readDouble(): Double?

    @Throws(IOException::class)
    fun readString(): String?
}
