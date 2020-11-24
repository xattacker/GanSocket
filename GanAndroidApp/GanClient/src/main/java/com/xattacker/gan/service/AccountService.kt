package com.xattacker.gan.service

import android.util.Log
import com.xattacker.binary.BinaryBuffer
import com.xattacker.gan.GanAgent
import com.xattacker.gan.data.FunctionType

abstract class AccountService protected constructor(aAgent: GanAgent?, private val _listener: AccountServiceListener?) : ServiceFoundation(aAgent)
{
    interface AccountServiceListener
    {
        fun onLoginSucceed(aAccount: String, aSessionId: String)
        fun onLogout(aAccount: String)
    }

    private var _account: String? = null
    fun login(aAccount: String, aPassword: String): Boolean
    {
        var result = false
        try
        {
            val buffer = BinaryBuffer()
            buffer.writeString(aAccount)
            buffer.writeString(aPassword)

            val response = send(FunctionType.LOGIN, buffer.data)
            if (response != null)
            {
                result = response.result
                if (result && response.response != null && _listener != null)
                {
                    val session_id = String(response.response!!)
                    _account = aAccount
                    _listener.onLoginSucceed(aAccount, session_id)
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
            _account?.let {
                account ->
                    val buffer = BinaryBuffer()
                    buffer.writeString(account)

                    val response = send(FunctionType.LOGOUT, buffer.data)
                    if (response != null && response.result)
                    {
                        _listener?.onLogout(account)
                        result = true
                        _account = null
                    }
            }
        }
        catch (ex: Exception)
        {
            Log.i("aaa", ex.toString())
        }

        return result
    }

    fun isAccountExisted(aAccount: String?): Boolean
    {
        return false
    }

    fun registerAccount(aAccount: String, aPassword: String, aExtra: ByteArray?): Boolean
    {
        var result = false
        try
        {
            val buffer= BinaryBuffer()
            buffer.writeString(aAccount)
            buffer.writeString(aPassword)

            if (aExtra != null && aExtra.size > 0)
            {
                val size = aExtra.size
                buffer.writeInteger(size)
                buffer.writeBinary(aExtra, 0, size)
            }
            else
            {
                buffer.writeInteger(0)
            }

            val response = send(FunctionType.REGISTER_ACCOUNT, buffer.data)
            if (response != null)
            {
                result = response.result
            }
        }
        catch (ex: Exception)
        {
            Log.i("aaa", ex.toString())
        }
        return result
    }
}