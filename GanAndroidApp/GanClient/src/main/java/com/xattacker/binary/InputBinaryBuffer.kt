package com.xattacker.binary

import java.io.Closeable
import java.io.IOException
import java.io.InputStream

class InputBinaryBuffer: Closeable, BinaryReadable
{
    private var _in: InputStream?

    constructor(ins: InputStream)
    {
        _in = ins
    }

    override fun available(): Long
    {
        var available = 0

        try
        {
            available = _in?.available() ?: 0
        }
        catch (ex: Exception)
        {
            available = 0
        }

        return available.toLong()
    }

    @Throws(IOException::class)
    override fun close()
    {
        _in?.close()
    }

    @Throws(IOException::class)
    fun read(aData: ByteArray): Int?
    {
        return _in?.read(aData)
    }

    @Throws(IOException::class)
    fun read(aData: ByteArray, aOffset: Int, aLength: Int): Int?
    {
        return _in?.read(aData, aOffset, aLength)
    }

    @Throws(IOException::class)
    override fun readBinary(aLength: Int): ByteArray?
    {
        if (_in?.available() ?: 0 < aLength)
        {
            throw IOException("Failed to read binary data from a input stream (Index out of bound)")
        }

        val data = ByteArray(aLength)
        _in?.read(data)

        return data
    }

    @Throws(IOException::class)
    override fun readShort(): Short?
    {
        if (_in?.available() ?: 0 < TypeConverter.SHORT_SIZE)
        {
            throw IOException("Failed to read a short from a input stream (Index out of bound)")
        }
        else
        {
            val data = ByteArray(TypeConverter.SHORT_SIZE)
            _in?.read(data)

            return TypeConverter.byteToShort(data)
        }
    }

    @Throws(IOException::class)
    override fun readInteger(): Int?
    {
        if (_in?.available() ?: 0 < TypeConverter.INTEGER_SIZE)
        {
            throw IOException("Failed to read a integer from a input stream (Index out of bound)")
        }
        else
        {
            val data = ByteArray(TypeConverter.INTEGER_SIZE)
            _in?.read(data)

            return TypeConverter.byteToInt(data)
        }
    }

    @Throws(IOException::class)
    override fun readLong(): Long?
    {
        if (_in?.available() ?: 0 < TypeConverter.LONG_SIZE)
        {
            throw IOException("Failed to read a long from a input stream (Index out of bound)")
        }
        else
        {
            val data = ByteArray(TypeConverter.LONG_SIZE)
            _in?.read(data)

            return TypeConverter.byteToLong(data)
        }
    }

    @Throws(IOException::class)
    override fun readDouble(): Double?
    {
        if (_in?.available() ?: 0 < TypeConverter.DOUBLE_SIZE)
        {
            throw IOException("Failed to read a double from a input stream (Index out of bound)")
        }
        else
        {
            val data = ByteArray(TypeConverter.DOUBLE_SIZE)
            _in?.read(data)

            return TypeConverter.byteToDouble(data)
        }
    }

    @Throws(IOException::class)
    override fun readString(): String?
    {
        var str: String? = null

        val length = readInteger()
        if (length != null)
        {
            val data = readBinary(length)
            if (data != null)
            {
                str = String(data, Charsets.UTF_8)
            }
        }

        return str
    }
}
