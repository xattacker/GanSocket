package com.xattacker.gan.data

import com.xattacker.binary.BinaryReadable
import com.xattacker.binary.BinarySerializable2
import com.xattacker.binary.BinaryWritable

class ResponsePack : BinarySerializable2
{
    var result = false
    var id = 0
    var response: ByteArray? = null

    constructor()
    {
    }

    override fun <ByteArray>toBinary(aWritable: BinaryWritable<ByteArray>)
    {
        try
        {
            aWritable.writeShort(if (result) 1.toShort() else 0.toShort())
            aWritable.writeInteger(id)

            if (response?.size ?: 0 > 0)
            {
                val size = response!!.size
                aWritable.writeInteger(size)
                aWritable.writeBinary(response!!, 0, size)
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
        var succeed = false

        try
        {
            this.result = aReadable.readShort()?.toInt() == 1
            id = aReadable.readInteger() ?: 0

            val size = aReadable.readInteger() ?: 0
            if (size > 0)
            {
                response = aReadable.readBinary(size)
            }
            else
            {
                response = null
            }

            succeed = true
        }
        catch (ex: Exception)
        {
            succeed = false
        }

        return succeed
    }
}