package com.xattacker.gan

interface GanClientListener
{
    fun onAccountLoggedIn(account: String)
    fun onAccountLoggedOut(account: String)
    fun onMessageReceived(sender: String, time: Long, msg: String)
}