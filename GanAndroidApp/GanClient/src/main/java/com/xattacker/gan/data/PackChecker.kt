package com.xattacker.gan.data

import com.xattacker.binary.BinaryBuffer
import com.xattacker.binary.InputBinaryBuffer
import com.xattacker.binary.TypeConverter
import com.xattacker.gan.exception.ResponseTimeoutException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class PackChecker
{
    companion object
    {
        private const val HEAD = "<GAN_PACK>"
        private val HEAD_BYTE = HEAD.toByteArray()
        private const val INT_LENGTH = 4
        private val VALID_LENGTH = HEAD_BYTE.size + INT_LENGTH

        @Throws(Exception::class)
        fun isValidPack(aIn: InputStream, waitCount: Int, aMarkable: Boolean = false): ValidResult
        {
            val result = PackChecker().ValidResult()

            try
            {
                var wait_count = 0
                while (aIn.available() < VALID_LENGTH && wait_count < waitCount)
                {
                    wait_count++
                    Thread.sleep(50)
                }

                if (aIn.available() < VALID_LENGTH)
                {
                    throw ResponseTimeoutException();
                }

                if (aIn.markSupported() && aMarkable)
                {
                    aIn.mark(HEAD_BYTE.size)
                }

                val temp = ByteArray(HEAD_BYTE.size)
                aIn.read(temp)

                result.valid = Arrays.equals(HEAD_BYTE, temp)
                if (result.valid)
                {
                    val buffer = InputBinaryBuffer(aIn)
                    result.length = buffer.readInteger() ?: 0
                }
            }
            catch (ex: Exception)
            {
                result.valid = false
            }
            finally
            {
                try
                {
                    if (aIn.markSupported() && aMarkable)
                    {
                        aIn.reset() // return to marked index
                    }
                }
                catch (ex2: Exception)
                {
                }
            }

            return result
        }

        fun pack(data: ByteArray, out: OutputStream)
        {
            out.write(HEAD_BYTE)
            out.write(TypeConverter.intToByte(data.size))
            out.write(data)
        }
    }

    private constructor()

    inner class ValidResult
    {
        var valid = false
        var length = 0
    }
}