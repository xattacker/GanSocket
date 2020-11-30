package com.xattacker.gan.service

import android.util.Log
import com.xattacker.binary.BinaryBuffer
import com.xattacker.gan.GanAgent
import com.xattacker.gan.data.FunctionType
import java.net.Socket


data class SessionInfo(val account: String, val sessionId: String)


internal interface AccountServiceListener
{
    fun onLoginSucceed(session: SessionInfo, connection: Socket)
    fun onLoggedOut(account: String)
}


class AccountService internal constructor(agent: GanAgent, private val _listener: AccountServiceListener) : ServiceFoundation(agent)
{
    fun login(account: String, password: String): Boolean
    {
        var result = false
        try
        {
            val buffer = BinaryBuffer()
            buffer.writeString(account)
            buffer.writeString(password)

            val response = send(FunctionType.LOGIN, buffer.data, false)
            if (response != null)
            {
                result = response.result
                if (result && response.response != null)
                {
                    val session_id = String(response.response!!)
                    val session = SessionInfo(account, session_id)
                    _listener.onLoginSucceed(session, response.connection!!)
                }
            }
        }
        catch (ex: Exception)
        {
            Log.i("aaa", ex.toString())
        }

        return result
    }

    fun logout(): Boolean
    {
        var result = false

        try
        {
            this.agent.account?.let {
                account ->
                    val buffer = BinaryBuffer()
                    buffer.writeString(account)

                    val response = send(FunctionType.LOGOUT, buffer.data)
                    if (response != null && response.result)
                    {
                        _listener.onLoggedOut(account)
                        result = true
                    }
            }
        }
        catch (ex: Exception)
        {
            Log.i("aaa", ex.toString())
        }

        return result
    }

    fun isAccountExisted(account: String): Boolean
    {
        return false
    }

    fun registerAccount(account: String, password: String, extra: ByteArray? = null): Boolean
    {
        var result = false
        try
        {
            val buffer= BinaryBuffer()
            buffer.writeString(account)
            buffer.writeString(password)

            if (extra != null && extra.size > 0)
            {
                val size = extra.size
                buffer.writeInteger(size)
                buffer.writeBinary(extra, 0, size)
            }
            else
            {
                buffer.writeInteger(0)
            }

            val response = send(FunctionType.REGISTER_ACCOUNT, buffer.data)
            result = response?.result ?: false
        }
        catch (ex: Exception)
        {
            Log.i("aaa", ex.toString())
        }

        return result
    }
}