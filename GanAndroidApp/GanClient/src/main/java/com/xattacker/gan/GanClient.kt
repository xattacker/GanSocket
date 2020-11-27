package com.xattacker.gan

import android.util.Log

import com.xattacker.binary.BinaryBuffer
import com.xattacker.binary.InputBinaryBuffer
import com.xattacker.gan.data.*
import com.xattacker.gan.data.RequestHeader
import com.xattacker.gan.service.*
import com.xattacker.gan.service.AccountServiceListener
import com.xattacker.gan.service.CallbackService
import com.xattacker.json.JsonUtility

import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.net.Socket

final class GanClient private constructor(private val _address: String, private val _port: Int, aListener: GanClientListener) : GanAgent, AccountServiceListener, CallbackServiceListener
{
    private var listener: WeakReference<GanClientListener>?

    override var account: String? = null
        private set

    override var sessionId: String? = null
        private set

    val isConnected: Boolean
        get() = !this.account.isNullOrEmpty()

    val accountService: AccountService by lazy { AccountService(this, this) }
    val messageService: MessageService by lazy { MessageService(this) }
    val systemService: SystemService by lazy { SystemService(this) }
    private val callbackService: CallbackService by lazy { CallbackService(this, this) }

    companion object
    {
        private const val TIMEOUT = 5000

        var instance: GanClient? = null
            private set

        @Throws(Exception::class)
        fun initial(aAddress: String, aPort: Int, aListener: GanClientListener)
        {
            if (instance == null)
            {
                instance = GanClient(aAddress, aPort, aListener)
            }
        }

        fun release()
        {
            instance?.doRelease()
            instance = null
        }
    }

    init
    {
        listener = WeakReference(aListener)
    }

    @Throws(Exception::class)
    override fun createSocket(): Socket
    {
        val socket = Socket()
        socket.connect(InetSocketAddress(_address, _port), TIMEOUT)

        return socket
    }

    override fun onLoginSucceed(account: String, sessionId: String, socket: Socket)
    {
        try
        {
            this.account = account
            this.sessionId = sessionId

            this.callbackService.handleConnection(socket)
            listener?.get()?.onAccountLoggedIn(account)
        }
        catch (ex: Exception)
        {
        }
    }

    override fun onLoggedOut(account: String)
    {
        this.account = null
        this.sessionId = null

        this.callbackService.close()
    }

    override fun onMessageReceived(message: MessageData)
    {
        this.listener?.get()?.onMessageReceived(message)
    }

    private fun doRelease()
    {
        account = null
        sessionId = null

        this.callbackService.close()
    }

    @Throws(Exception::class)
    private fun wait(aIn: InputStream, aLength: Int, aMaxTry: Int): Boolean
    {
        var try_count = 0
        do
        {
            Thread.sleep(50)
            try_count++
        } while (aIn.available() < aLength && try_count < aMaxTry)

        return aIn.available() >= aLength
    }
}