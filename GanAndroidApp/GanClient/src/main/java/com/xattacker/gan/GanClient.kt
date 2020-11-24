package com.xattacker.gan

import android.util.Log
import com.xattacker.binary.BinaryBuffer
import com.xattacker.binary.OutputBinaryBuffer
import com.xattacker.gan.data.FunctionType
import com.xattacker.gan.data.PackChecker
import com.xattacker.gan.data.RequestHeader
import com.xattacker.gan.data.ResponsePack
import com.xattacker.gan.service.AccountService
import com.xattacker.gan.service.AccountService.AccountServiceListener
import com.xattacker.gan.service.MsgService
import com.xattacker.gan.service.SystemService
import com.xattacker.util.IOUtility

import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.net.Socket

class GanClient private constructor(private val _address: String, private val _port: Int, aListener: GanClientListener) : Runnable, GanAgent, AccountServiceListener
{
    private var _socket: Socket? = null

    private var _listener: WeakReference<GanClientListener>?

    override var account: String? = null
        private set

    override var sessionId: String? = null
        private set

    val accountService: AccountService by lazy { InnerAccountService(this, this) }
    val smsService: MsgService by lazy { InnerSmsService(this) }
    val systemService: SystemService by lazy { InnerSystemService(this) }

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
            val out = _socket!!.getOutputStream()
            out.write(PackChecker.HEAD_BYTE)
            val obb = OutputBinaryBuffer(out)

            val header = RequestHeader()
            header.type = (FunctionType.CONNECTION)
            header.owner = (account)
            header.sessionId = (sessionId)
            obb.writeString(header.toJson() ?: "")
            obb.flush()

            Thread.sleep(500)

            var count = 0
            while (account != null && _socket != null)
            {
                if (count >= 20)
                {
                    count = 0
                    _socket?.sendUrgentData(0xFF)
                }

                if (PackChecker.isValidPack(_socket!!.getInputStream(), false))
                {
                    val bos = ByteArrayOutputStream()
                    IOUtility.readResponse(_socket!!.getInputStream(), bos)

                    var binary = BinaryBuffer(bos.toByteArray())
                    val response = ResponsePack()
                    response.fromBinary(binary)
                    if (response.result)
                    {
                        when (response.id)
                        {
                            1 ->
                            {
                                binary = BinaryBuffer(response.response!!)
                                val sender: String = binary.readString() ?: ""
                                val time: Long = binary.readLong() ?: 0
                                val msg: String = binary.readString() ?: ""
                                _listener?.get()?.onSMSReceived(sender, time, msg)
                            }

                            2 ->
                            {
                                closeConnection()
                                _listener?.get()?.onAccountClosed(account ?: "")
                                account = null
                                sessionId = null
                            }
                        }
                    }

                    bos.close()
                }

                Thread.sleep(500)
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

    override fun onLoginSucceed(aAccount: String, aSessionId: String)
    {
        try
        {
            account = aAccount
            sessionId = aSessionId

            _socket = createSocket()
            _socket?.keepAlive = true
            _socket?.soTimeout = 0
            _socket?.oobInline = true

            _listener?.get()?.onAccountLogined(account ?: "")

            start()
        }
        catch (ex: Exception)
        {
        }
    }

    override fun onLogout(aAccount: String)
    {
        account = null
        sessionId = null
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
}