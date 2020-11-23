package com.xattacker.gan.data

import com.xattacker.binary.BinaryReadable
import com.xattacker.binary.BinarySerializable2
import com.xattacker.binary.BinaryWritable

class ResponsePack : BinarySerializable2
{
    var result = false
    var id = 0
    var content: ByteArray? = null

    constructor()
    {
    }

    override fun <ByteArray>toBinary(aWritable: BinaryWritable<ByteArray>)
    {
        try
        {
            aWritable.writeShort(if (result) 1.toShort() else 0.toShort())
            aWritable.writeInteger(id)

            if (content != null && content!!.size > 0)
            {
                val size = content!!.size
                aWritable.writeInteger(size)
                aWritable.writeBinary(content!!, 0, size)
            }
            else
            {
                aWritable.writeInteger(0)
            }
        }
        catch (ex: Exception)
        {
        }
    }

    override fun fromBinary(aReadable: BinaryReadable): Boolean
    {
        var result = false

        try
        {
            this.result = aReadable.readShort()?.toInt() == 1
            id = aReadable.readInteger() ?: 0
            val size: Int = aReadable.readInteger() ?: 0
            if (size > 0)
            {
                content = aReadable.readBinary(size)
            }
            else
            {
                content = null
            }
            result = true
        }
        catch (ex: Exception)
        {
            result = false
        }

        return result
    }
}