package com.xattacker.gan

interface GanClientListener
{
    fun onAccountLogined(aAccount: String)
    fun onAccountClosed(aAccount: String)
    fun onSMSReceived(aSender: String, aTime: Long, aMsg: String)
}