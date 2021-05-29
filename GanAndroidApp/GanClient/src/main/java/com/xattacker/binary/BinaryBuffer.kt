package com.xattacker.binary

import java.io.IOException
import java.io.UnsupportedEncodingException

class BinaryBuffer : BinaryReadable, BinaryWritable<BinaryBuffer>
{
    lateinit var data: ByteArray
        private set

    var currentIndex: Long = 0
        private set

    var length: Long = 0
        private set

    val isEmpty: Boolean
        get() = length == 0L

    override val available: Long
        get() = length - currentIndex

    constructor()
    {
        clear()
    }

    constructor(aData: ByteArray)
    {
        data = aData
        currentIndex = 0
        length = (aData.size).toLong()
    }

    constructor(aBuffer: BinaryBuffer) : this(aBuffer.data)

    fun clear()
    {
        currentIndex = 0
        data = ByteArray(0)
        length = 0
    }

    fun seekToHead()
    {
        currentIndex = 0
    }

    @Throws(Exception::class)
    fun seekTo(aIndex: Long)
    {
        val index = if (aIndex > 0) aIndex else 0

        if (index > length)
        {
            throw Exception("Failed to seek to index [$aIndex] in buffer.")
        }
        else
        {
            currentIndex = aIndex
        }
    }

    fun seekToEnd()
    {
        currentIndex = length
    }

    fun writeBuffer(aBuffer: BinaryBuffer): BinaryBuffer
    {
        val data = aBuffer.data
        writeBinary(data, 0, data.size)

        return this
    }

    override fun writeBinary(aData: ByteArray, aOffset: Int, aLength: Int): BinaryBuffer
    {
        append(aLength)
        System.arraycopy(aData, aOffset, data, currentIndex.toInt(), aLength)

        currentIndex += aLength.toLong()
        length = if (currentIndex <= length) length else currentIndex

        return this
    }

    @Throws(IOException::class)
    override fun readBinary(aLength: Int): ByteArray?
    {
        if (currentIndex + aLength > length)
        {
            throw IOException("Failed to read binary data from a binary array (Index out of bound)")
        }

        val data = ByteArray(aLength)
        System.arraycopy(this.data, currentIndex.toInt(), data, 0, aLength)
        currentIndex += aLength.toLong()

        return data
    }

    override fun writeShort(aShort: Short): BinaryBuffer
    {
        val data = TypeConverter.shortToByte(aShort)
        writeBinary(data, 0, data.size)

        return this
    }

    @Throws(IOException::class)
    override fun readShort(): Short?
    {
        if (currentIndex + TypeConverter.SHORT_SIZE > length)
        {
            throw IOException("Failed to read a short from a binary array (Index out of bound)")
        }
        else
        {
            val data = ByteArray(TypeConverter.SHORT_SIZE)
            System.arraycopy(this.data, currentIndex.toInt(), data, 0, TypeConverter.SHORT_SIZE)
            currentIndex += TypeConverter.SHORT_SIZE

            return TypeConverter.byteToShort(data)
        }
    }

    override fun writeInteger(aInt: Int): BinaryBuffer
    {
        val data = TypeConverter.intToByte(aInt)
        writeBinary(data, 0, data.size)

        return this
    }

    @Throws(IOException::class)
    override fun readInteger(): Int?
    {
        if (currentIndex + TypeConverter.INTEGER_SIZE > length)
        {
            throw IOException("Failed to read a integer from a binary array (Index out of bound)")
        }
        else
        {
            val data = ByteArray(TypeConverter.INTEGER_SIZE)
            System.arraycopy(this.data, currentIndex.toInt(), data, 0, TypeConverter.INTEGER_SIZE)
            currentIndex += TypeConverter.INTEGER_SIZE

            return TypeConverter.byteToInt(data)
        }
    }

    override fun writeLong(aLong: Long): BinaryBuffer
    {
        val data = TypeConverter.longToByte(aLong)
        writeBinary(data, 0, data.size)

        return this
    }

    @Throws(IOException::class)
    override fun readLong(): Long?
    {
        if (currentIndex + TypeConverter.LONG_SIZE > length)
        {
            throw IOException("Failed to read a long from a binary array (Index out of bound)")
        }
        else
        {
            val data = ByteArray(TypeConverter.LONG_SIZE)
            System.arraycopy(this.data, currentIndex.toInt(), data, 0, TypeConverter.LONG_SIZE)
            currentIndex += TypeConverter.LONG_SIZE

            return TypeConverter.byteToLong(data)
        }
    }

    override fun writeDouble(aDouble: Double): BinaryBuffer
    {
        val data = TypeConverter.doubleToByte(aDouble)
        writeBinary(data, 0, data.size)

        return this
    }

    @Throws(IOException::class)
    override fun readDouble(): Double?
    {
        if (currentIndex + TypeConverter.DOUBLE_SIZE > length)
        {
            throw IOException("Failed to read a double from a binary array (Index out of bound)")
        }
        else
        {
            val data = ByteArray(TypeConverter.DOUBLE_SIZE)
            System.arraycopy(this.data, currentIndex.toInt(), data, 0, TypeConverter.DOUBLE_SIZE)
            currentIndex += TypeConverter.DOUBLE_SIZE

            return TypeConverter.byteToDouble(data)
        }
    }

    @Throws(UnsupportedEncodingException::class)
    override fun writeString(aStr: String): BinaryBuffer
    {
        if (aStr.isEmpty())
        {
            writeInteger(0)
        }
        else
        {
            val data = aStr.toByteArray(charset("UTF8"))
            val length = data.size

            writeInteger(length)
            writeBinary(data, 0, length)
        }

        return this
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

    private fun append(aLength: Int)
    {
        if (length < currentIndex + aLength)
        {
            val new_data = ByteArray(currentIndex.toInt() + aLength)

            // copy original data to new data
            System.arraycopy(data, 0, new_data, 0, data.size)

            data = new_data
            length = new_data.size.toLong()
        }
    }
}
