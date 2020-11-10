package com.xattacker.binary

import android.app.Activity
import kotlin.experimental.and
import kotlin.experimental.or

object TypeConverter
{
    const val CHAR_SIZE = 2
    const val SHORT_SIZE = 2
    const val INTEGER_SIZE = 4
    const val LONG_SIZE = 8
    const val DOUBLE_SIZE = 8

    fun charToByte(aChar: Char): ByteArray
    {
        val bytes = ByteArray(CHAR_SIZE)
        bytes[0] = (aChar.toInt() and 0xff00 shr 8).toByte()
        bytes[1] = (aChar.toInt() and 0x00ff).toByte()

        return bytes
    }

    fun charArrayToByte(aChars: CharArray): ByteArray
    {
        val bytes = ByteArray(aChars.size * 2)

        var i = 0
        val length = aChars.size
        while (i < length)
        {
            bytes[2 * i] = (aChars[i].toInt() and 0xff00 shr 8).toByte()
            bytes[2 * i + 1] = (aChars[i].toInt() and 0x00ff).toByte()
            i++
        }

        return bytes
    }

    fun byteToChar(aBytes: ByteArray): Char
    {
        var value = aBytes[0].toInt()

        value = value and 0xff
        value = value or (aBytes[1].toInt() shl 8)

        return value.toChar()
    }

    fun shortToByte(aShort: Short): ByteArray
    {
        var temp = aShort
        val bytes = ByteArray(SHORT_SIZE)

        for (i in bytes.indices)
        {
            val b = Integer.valueOf(temp.toInt() and 0xff)
            bytes[i] = b.toByte()
            temp = (temp.toInt() shr 8).toShort()
        }

        return bytes
    }

    fun byteToShort(aBytes: ByteArray): Short
    {
        var value = aBytes[0].toShort()

        value = value and 0xff
        value = value or (aBytes[1].toInt() shl 8).toShort()

        return value
    }

    fun intToByte(aInt: Int): ByteArray
    {
        var temp = aInt
        val bytes = ByteArray(INTEGER_SIZE)

        for (i in bytes.indices)
        {
            val b = Integer.valueOf(temp and 0xff)
            bytes[i] = b.toByte()

            temp = temp shr 8
        }

        return bytes
    }

    fun byteToInt(aBytes: ByteArray): Int
    {
        var value = aBytes[0].toInt()

        value = value and 0xff
        value = value or (aBytes[1].toInt() shl 8)

        value = value and 0xffff
        value = value or (aBytes[2].toInt() shl 16)

        value = value and 0xffffff
        value = value or (aBytes[3].toInt() shl 24)

        return value
    }

    fun longToByte(aLong: Long): ByteArray
    {
        var temp = aLong
        val bytes = ByteArray(LONG_SIZE)

        for (i in bytes.indices)
        {
            val b = Integer.valueOf(temp.toInt() and 0xff)
            bytes[i] = b.toByte()

            temp = temp shr 8
        }

        return bytes
    }

    fun byteToLong(aBytes: ByteArray): Long
    {
        var value = aBytes[0].toLong()

        value = value and 0xff
        value = value or (aBytes[1].toLong() shl 8)

        value = value and 0xffff
        value = value or (aBytes[2].toLong() shl 16)

        value = value and 0xffffff
        value = value or (aBytes[3].toLong() shl 24)

        value = value and 0xffffffffL
        value = value or (aBytes[4].toLong() shl 32)

        value = value and 0xffffffffffL
        value = value or (aBytes[5].toLong() shl 40)

        value = value and 0xffffffffffffL
        value = value or (aBytes[6].toLong() shl 48)

        value = value and 0xffffffffffffffL
        value = value or (aBytes[7].toLong() shl 56)

        return value
    }

    fun doubleToByte(aDouble: Double): ByteArray
    {
        return longToByte(java.lang.Double.doubleToLongBits(aDouble))
    }

    fun byteToDouble(aBytes: ByteArray): Double
    {
        return java.lang.Double.longBitsToDouble(byteToLong(aBytes))
    }
}
