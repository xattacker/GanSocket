package com.xattacker.gan.data

import com.xattacker.binary.BinaryBuffer
import java.io.InputStream
import java.util.*

object PackFormatChecker
{
    const val HEAD = "<GAN_PACK>"
    val HEAD_BYTE = HEAD.toByteArray()

    fun isValidPack(aContent: ByteArray): Boolean
    {
        var valid = false
        try
        {
            if (aContent.size > HEAD.length)
            {
                val buffer = BinaryBuffer(aContent)
                if (buffer.readShort()?.toInt() == HEAD.length)
                {
                    buffer.seekToHead()
                    valid = buffer.readString().equals(HEAD)
                }
            }
        }
        catch (ex: Exception)
        {
            valid = false
        }

        return valid
    }

    fun isValidPack(aIn: InputStream, aMarkable: Boolean): Boolean
    {
        var valid = false
        try
        {
                val size = aIn.available()
                if (size < 0)
                {
                    throw Exception("EOF")
                }
                if (size > HEAD_BYTE.size)
                {
                    if (aIn.markSupported())
                    {
                        aIn.mark(HEAD_BYTE.size)
                    }

                    val temp = ByteArray(HEAD_BYTE.size)
                    aIn.read(temp)
                    valid = Arrays.equals(HEAD_BYTE, temp)
                }
        }
        catch (ex: Exception)
        {
            valid = false
        }
        finally
        {
            try
            {
                if (aIn.markSupported())
                {
                    aIn.reset() // return to marked index
                }
            }
            catch (ex2: Exception)
            {
            }
        }
        return valid
    }
}