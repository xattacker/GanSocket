package com.xattacker.binary

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class OutputBinaryBuffer: Closeable, BinaryWritable<OutputBinaryBuffer>
{
    private var _out: OutputStream?

    constructor(outs: OutputStream)
    {
        _out = outs
    }

    @Throws(IOException::class)
    fun flush()
    {
        _out?.flush()
    }

    @Throws(IOException::class)
    override fun close()
    {
        _out?.close()
    }

    @Throws(IOException::class)
    override fun writeBinary(aData: ByteArray, aOffset: Int, aLength: Int): OutputBinaryBuffer
    {
        _out?.write(aData, aOffset, aLength)

        return this
    }

    @Throws(IOException::class)
    override fun writeShort(aShort: Short): OutputBinaryBuffer
    {
        val data = TypeConverter.shortToByte(aShort)
        writeBinary(data, 0, data.size)

        return this
    }

    @Throws(IOException::class)
    override fun writeInteger(aInt: Int): OutputBinaryBuffer
    {
        val data = TypeConverter.intToByte(aInt)
        writeBinary(data, 0, data.size)

        return this
    }

    @Throws(IOException::class)
    override fun writeLong(aLong: Long): OutputBinaryBuffer
    {
        val data = TypeConverter.longToByte(aLong)
        writeBinary(data, 0, data.size)

        return this
    }

    @Throws(IOException::class)
    override fun writeDouble(aDouble: Double): OutputBinaryBuffer
    {
        val data = TypeConverter.doubleToByte(aDouble)
        writeBinary(data, 0, data.size)

        return this
    }

    @Throws(IOException::class)
    override fun writeString(aStr: String): OutputBinaryBuffer
    {
        if (aStr.isEmpty())
        {
            writeInteger(0)
        }
        else
        {
            val data = aStr.toByteArray(Charsets.UTF_8)
            val length = data.size

            writeInteger(length)
            writeBinary(data, 0, length)
        }

        return this
    }

    @Throws(IOException::class)
    @JvmOverloads
    fun writeString(aIn: InputStream?, aBufferSize: Int = 5120): OutputBinaryBuffer
    {
        if (aIn == null || aIn.available() == 0)
        {
            writeInteger(0)
        }
        else
        {
            val length = aIn.available()
            writeInteger(length)

            val temp = ByteArray(aBufferSize)
            var index = 0

            while (true)
            {
                index = aIn.read(temp)
                if (index < 0)
                {
                    break
                }

                writeBinary(temp, 0, index)
                flush()
            }
        }

        return this
    }
}
