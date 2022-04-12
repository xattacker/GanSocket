package com.xattacker.gan.data

import com.xattacker.binary.BinaryReadable
import com.xattacker.binary.BinarySerializable2
import com.xattacker.binary.BinaryWritable
import java.net.Socket

class ResponsePack() : BinarySerializable2
{
    var result = false
    var id = 0
    var response: ByteArray? = null
    var connection: Socket? = null

    override fun <ByteArray>toBinary(writable: BinaryWritable<ByteArray>)
    {
        try
        {
            writable.writeShort(if (result) 1.toShort() else 0.toShort())
            writable.writeInteger(id)

            if (response?.size ?: 0 > 0)
            {
                val size = response!!.size
                writable.writeInteger(size)
                writable.writeBinary(response!!, 0, size)
            }
            else
            {
                writable.writeInteger(0)
            }
        }
        catch (ex: Exception)
        {
        }
    }

    override fun fromBinary(readable: BinaryReadable): Boolean
    {
        var succeed = false

        try
        {
            this.result = readable.readShort()?.toInt() == 1
            this.id = readable.readInteger() ?: 0

            val size = readable.readInteger() ?: 0
            if (size > 0)
            {
                response = readable.readBinary(size)
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