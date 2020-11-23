package com.xattacker.util

import java.io.ByteArrayOutputStream
import java.io.InputStream

object IOUtility
{
    @Throws(Exception::class)
    fun readResponse(aIn: InputStream?, aBos: ByteArrayOutputStream?)
    {
        if (aIn != null && aBos != null)
        {
            var temp = ByteArray(256)
            var index = -1
            do
            {
                while (aIn.read(temp).also {index = it} != -1)
                {
                    aBos.write(temp, 0, index)
                    if (index < temp.size)
                    {
                        break
                    }
                }
            } while (aBos.size() == 0)
        }
    }
}