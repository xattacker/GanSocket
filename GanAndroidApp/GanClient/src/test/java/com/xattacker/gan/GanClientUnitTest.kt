package com.xattacker.gan

import com.xattacker.gan.data.MessageData
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GanClientUnitTest: GanClientListener
{
    companion object
    {
        private val account = "test"
    }

    enum class AccountStatus
    {
        none,

        logined,
        logout
    }

    private var accountStatus = AccountStatus.none
    private var gotMsg: MessageData? = null

    constructor()
    {
        System.out.println(this::class.simpleName + " call constructor")
    }

    @Before
    fun setUp()
    {
        System.out.println(this::class.simpleName + " call setUp")

        GanClient.initial("192.168.226.46", 5999, this)
    }

    @After
    fun tearDown()
    {
        System.out.println(this::class.simpleName + " call tearDown")

        GanClient.release()
        gotMsg = null
    }

    @Test
    fun testLogin_Out()
    {
        val result = GanClient.instance?.accountService?.login(account, "test123")
        assertTrue("login failed", result == true && GanClient.instance?.isConnected == true && GanClient.instance?.sessionId != null)
        assertTrue("callback onAccountLoggedIn failed", this.accountStatus == AccountStatus.logined)


       // Thread.sleep(2000)

        val result2 = GanClient.instance?.accountService?.logout()
        assertTrue("logout failed", result2 == true && GanClient.instance?.isConnected == false && GanClient.instance?.sessionId == null)

        //Thread.sleep(2000)
        assertTrue("callback onAccountLoggedOut failed", this.accountStatus == AccountStatus.logout)
    }

    @Test
    fun testGetIP()
    {
        val ip = GanClient.instance?.systemService?.getIP()
        assertTrue("getIPAddress failed", ip.isNullOrEmpty() == false)
    }

    @Test
    fun testGetTime()
    {
        val time = GanClient.instance?.systemService?.getSystemTime()
        assertTrue("getSystemTime failed", time != null)
    }

    @Test
    fun testSendMsg()
    {
        val result = GanClient.instance?.accountService?.login(account, "test123")
        assertTrue("login failed", result == true && GanClient.instance?.isConnected == true && GanClient.instance?.sessionId != null)
        assertTrue("callback onAccountLoggedIn failed", this.accountStatus == AccountStatus.logined)



        val msg = "message 中文: " + System.currentTimeMillis()

        val result2 = GanClient.instance?.messageService?.sendMessage(account, msg)
        assertTrue("sendMessage failed", result2 == true)

        Thread.sleep(1500)
        assertTrue("callback onMessageReceived failed", this.gotMsg?.message?.equals(msg) == true)
    }

    override fun onAccountLoggedIn(account: String)
    {
        assertTrue("login account is not the same", GanClientUnitTest.account.equals(account))
        accountStatus = AccountStatus.logined
    }

    override fun onAccountLoggedOut(account: String)
    {
        assertTrue("logout account is not the same", GanClientUnitTest.account.equals(account))
        accountStatus = AccountStatus.logout
    }

    override fun onMessageReceived(message: MessageData)
    {
        this.gotMsg = message
    }
}