package com.xattacker.util

import java.io.InputStream
import java.io.OutputStream
import android.util.Base64

fun String.mimeString(flags: Int = Base64.NO_WRAP): String
{
    return MimeCodec.encode(this, flags)
}

fun String.decodeMime(flags: Int = Base64.NO_WRAP): ByteArray
{
    return MimeCodec.decode(this, flags)
}


object MimeCodec
{
    private const val BUFFER_SIZE = 5120 // bytes

    fun encode(aSrc: ByteArray, aLength: Int, flags: Int = Base64.NO_WRAP): ByteArray
    {
        return Base64.encode(aSrc, 0, aLength, flags)
    }

    fun encode(aSrc: ByteArray, flags: Int = Base64.NO_WRAP): String
    {
        return String(encode(aSrc, aSrc.size, flags))
    }

    @Throws(Exception::class)
    fun encode(aIn: InputStream?, aOut: OutputStream?, flags: Int = Base64.NO_WRAP)
    {
        if (aIn == null || aOut == null)
        {
            return
        }


        val buffer = ByteArray(3 * BUFFER_SIZE) // must be a multiple of 3
        var encoded: ByteArray?
        var index: Int

        while (true)
        {
            index = aIn.read(buffer)
            if (index < 0)
            {
                break
            }

            encoded = encode(buffer, index, flags)

            aOut.write(encoded, 0, encoded.size)
            aOut.flush()
        }

        aOut.close()
        aIn.close()
    }

    fun encode(aSrc: String, flags: Int = Base64.NO_WRAP): String
    {
        return encode(aSrc.toByteArray(), flags)
    }

    fun decode(aSrc: ByteArray, aLength: Int, flags: Int = Base64.NO_WRAP): ByteArray
    {
        return Base64.decode(aSrc, 0, aLength, flags)
    }

    fun decode(aSrc: String?, flags: Int = Base64.NO_WRAP): ByteArray
    {
        if (aSrc == null || aSrc.isEmpty())
        {
            return ByteArray(0)
        }


        val src = aSrc.toByteArray()
        val length = src.size

        return decode(src, length, flags)
    }

    @Throws(Exception::class)
    fun decode(aIn: InputStream?, aOut: OutputStream?, flags: Int = Base64.NO_WRAP)
    {
        if (aIn == null || aOut == null)
        {
            return
        }


        val buffer = ByteArray(4 * BUFFER_SIZE) // must be a multiple of 4
        var decoded: ByteArray?
        var index: Int

        while (true)
        {
            index = aIn.read(buffer)
            if (index < 0)
            {
                break
            }

            decoded = decode(buffer, index, flags)

            aOut.write(decoded, 0, decoded.size)
            aOut.flush()
        }

        aOut.close()
        aIn.close()
    }

    fun decodeToString(aSrc: String, flags: Int = Base64.NO_WRAP): String
    {
        return String(decode(aSrc, flags))
    }
}