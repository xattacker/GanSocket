package com.xattacker.gan

import android.util.Log
import com.xattacker.binary.BinaryBuffer
import com.xattacker.binary.InputBinaryBuffer
import com.xattacker.binary.OutputBinaryBuffer
import com.xattacker.gan.data.*
import com.xattacker.gan.data.RequestHeader
import com.xattacker.gan.service.AccountService
import com.xattacker.gan.service.AccountService.AccountServiceListener
import com.xattacker.gan.service.MessageService
import com.xattacker.gan.service.SystemService
import com.xattacker.json.JsonUtility
import com.xattacker.util.IOUtility

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.Charset

class GanClient private constructor(private val _address: String, private val _port: Int, aListener: GanClientListener) : Runnable, GanAgent, AccountServiceListener
{
    private var _socket: Socket? = null
    private var _listener: WeakReference<GanClientListener>?

    override var account: String? = null
        private set

    override var sessionId: String? = null
        private set

    val isConnected: Boolean
        get() = !this.account.isNullOrEmpty()

    val accountService: AccountService by lazy { AccountService(this, this) }
    val _messageService: MessageService by lazy { MessageService(this) }
    val systemService: SystemService by lazy { SystemService(this) }

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
        _listener = WeakReference(aListener)
    }

    override fun run()
    {
        try
        {
            val out = _socket?.getOutputStream()
            val ins = _socket?.getInputStream()
            if (out == null || ins == null)
            {
                return
            }


            val buffer = BinaryBuffer()

            val header = RequestHeader()
            header.type = FunctionType.CREATE_CALLBACK_CONNECTION
            header.owner = account
            header.sessionId = sessionId
            buffer.writeString(header.toJson())

            PackChecker.pack(buffer.data, out)
            out.flush()

            var count = 0
            while (account != null && _socket != null)
            {
                Thread.sleep(500)

                if (count >= 20)
                {
                    count = 0
                    _socket?.sendUrgentData(0xFF)
                }

                val valid = PackChecker.isValidPack(ins, false)
                if (valid.valid && valid.length > 0)
                {
                    if (!wait(ins, valid.length, 50))
                    {
                        continue
                    }

                    val binary = InputBinaryBuffer(ins)
                    val response = ResponsePack()
                    response.fromBinary(binary)

                    if (response.result)
                    {
                        when (FunctionType.parse(response.id))
                        {
                            FunctionType.RECEIVE_SMS ->
                            {
                                response.response?.let {
                                    val json = String(it, Charsets.UTF_8)
                                    val msg = JsonUtility.fromJson(json, MessageData::class.java)
                                    _listener?.get()?.onMessageReceived(msg)
                                }
                            }

                            FunctionType.LOGOUT ->
                            {
                                closeConnection()
                                _listener?.get()?.onAccountLoggedOut(account ?: "")

                                account = null
                                sessionId = null
                            }

                            else -> android.util.Log.d("aaa", "unknown callback response id: " + response.id)
                        }
                    }
                }

                count++
            }
        }
        catch (ex: Exception)
        {
            Log.i("aaa", ex.toString())
        }
    }

    @Throws(Exception::class)
    override fun createSocket(): Socket
    {
        val socket = Socket()
        socket.connect(InetSocketAddress(_address, _port), TIMEOUT)

        return socket
    }

    override fun onLoginSucceed(account: String, sessionId: String)
    {
        try
        {
            this. account = account
            this.sessionId = sessionId

            _socket = createSocket()
            _socket?.keepAlive = true
            _socket?.soTimeout = 0
            _socket?.oobInline = true

            _listener?.get()?.onAccountLoggedIn(account)

            start()
        }
        catch (ex: Exception)
        {
        }
    }

    override fun onLoggedOut(account: String)
    {
        this.account = null
        this.sessionId = null
    }

    private fun start()
    {
        Thread(this).start()
    }

    private fun closeConnection()
    {
        try
        {
            _socket?.close()
        }
        catch (ex: Exception)
        {
        }

        _socket = null
    }

    private fun doRelease()
    {
        closeConnection()

        account = null
        sessionId = null
        _listener = null
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