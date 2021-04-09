package com.xattacker.gan

import com.xattacker.gan.data.MessageData

interface GanClientListener
{
    fun onAccountLoggedIn(account: String)
    fun onAccountLoggedOut(account: String)

    // make the function could be optional
    fun onMessageReceived(message: MessageData)
    {
    }
}