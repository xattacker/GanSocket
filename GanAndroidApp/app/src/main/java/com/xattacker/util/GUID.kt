package com.xattacker.util

import java.util.*

object GUID
{
    // generate a 36 bytes guid string
    fun generateGUID(): String
    {
        return UUID.randomUUID().toString()
    }
}