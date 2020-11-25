package com.xattacker.gan

import com.xattacker.gan.data.MessageData

interface GanClientListener
{
    fun onAccountLoggedIn(account: String)
    fun onAccountLoggedOut(account: String)
    fun onMessageReceived(message: MessageData)
}