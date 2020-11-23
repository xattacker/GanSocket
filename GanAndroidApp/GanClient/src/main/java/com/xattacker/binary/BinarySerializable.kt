package com.xattacker.binary

interface BinarySerializable {
    fun toBinary(): ByteArray

    fun fromBinary(aContent: ByteArray): Boolean
}
