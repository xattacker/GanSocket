package com.xattacker.gan

import java.net.Socket

interface GanAgent
{
    @Throws(Exception::class)
    fun createSocket(): Socket?

    val account: String?
    val sessionId: String?
}