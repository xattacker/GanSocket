package com.xattacker.gan

import com.xattacker.gan.data.MessageData
import com.xattacker.gan.service.*

import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.net.Socket


final class GanClient private constructor(private val _address: String, private val _port: Int, aListener: GanClientListener) : GanAgent, AccountServiceListener, CallbackServiceListener
{
    private var listener: WeakReference<GanClientListener>?

    override val account: String?
        get() = this.session?.account

    override val sessionId: String?
        get() = this.session?.sessionId

    val isConnected: Boolean
        get() = !this.account.isNullOrEmpty()

    val accountService: AccountService by lazy { AccountService(this, this) }
    val messageService: MessageService by lazy { MessageService(this) }
    val systemService: SystemService by lazy { SystemService(this) }

    private val callbackService: CallbackService by lazy { CallbackService(this, this) }
    private var session: SessionInfo? = null

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

    override fun onLoginSucceed(session: SessionInfo, socket: Socket)
    {
        try
        {
            this.session = session
            this.callbackService.handleConnection(socket)
            this.listener?.get()?.onAccountLoggedIn(session.account)
        }
        catch (ex: Exception)
        {
        }
    }

    override fun onLoggedOut(account: String)
    {
        this.session = null
        this.callbackService.close()
        this.listener?.get()?.onAccountLoggedOut(account)
    }

    override fun onMessageReceived(message: MessageData)
    {
        this.listener?.get()?.onMessageReceived(message)
    }

    private fun doRelease()
    {
        this.session = null
        this.listener = null
        this.callbackService.close()
    }
}