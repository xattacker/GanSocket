package com.xattacker.gan.service

import android.util.Log
import com.xattacker.binary.InputBinaryBuffer
import com.xattacker.gan.GanAgent
import com.xattacker.gan.data.*
import com.xattacker.json.JsonUtility
import java.lang.ref.WeakReference
import java.net.Socket

internal interface CallbackServiceListener
{
    fun onLoggedOut(account: String)
    fun onMessageReceived(message: MessageData)
}


internal class CallbackService internal constructor(agent: GanAgent, listener: CallbackServiceListener) : ServiceFoundation(agent), Runnable
{
    private var socket: Socket? = null
    private var thread: Thread? = null
    private var listener: WeakReference<CallbackServiceListener>? = null

    private val ENABLE_MSG_ACK = false

    init
    {
        this.listener = WeakReference(listener)
    }

    fun handleConnection(socket: Socket)
    {
        this.socket = socket
        this.socket?.keepAlive = true
        this.socket?.soTimeout = 0
        this.socket?.oobInline = true

        thread = Thread(this)
        thread?.start()
    }

    fun close()
    {
        thread = null

        socket?.close()
        socket = null
    }

    override fun run()
    {
        try
        {
            val out = socket?.getOutputStream()
            val ins = socket?.getInputStream()
            if (out == null || ins == null)
            {
                return
            }


            while (socket != null)
            {
                Thread.sleep(500)

                val valid = PackChecker.isValidPack(ins, 50)
                if (valid.valid && valid.length > 0)
                {
                    wait(ins, valid.length, 50, socket?.receiveBufferSize ?: 0)


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
                                    listener?.get()?.onMessageReceived(msg)

                                    if (ENABLE_MSG_ACK)
                                    {
                                        val ack = MessageAck()
                                        ack.id = msg.id

                                        PackChecker.pack(ack.toJson().toByteArray(), out)
                                        Thread.sleep(200)
                                    }
                                }
                            }

                            FunctionType.LOGOUT ->
                            {
                                close()
                                listener?.get()?.onLoggedOut(this.agent.account ?: "")
                            }

                            else -> android.util.Log.d("aaa", "unknown callback response id: " + response.id)
                        }
                    }
                }
            }
        }
        catch (ex: Exception)
        {
            Log.i("aaa", ex.toString())
        }
    }
}