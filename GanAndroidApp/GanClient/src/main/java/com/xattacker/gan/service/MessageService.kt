package com.xattacker.gan.service

import android.util.Log
import com.xattacker.binary.BinaryBuffer
import com.xattacker.gan.GanAgent
import com.xattacker.gan.data.FunctionType


class MessageService internal constructor(agent: GanAgent) : ServiceFoundation(agent)
{
    fun sendMessage(receiver: String, msg: String): Boolean
    {
        var result = false
        try
        {
            val buffer = BinaryBuffer()
            buffer.writeString(receiver)
            buffer.writeString(msg)

            val response = send(FunctionType.SEND_SMS, buffer.data)
            if (response != null)
            {
                result = response.result
            }
        }
        catch (ex: Exception)
        {
            Log.i("aaa", "aere $ex")
        }

        return result
    }
}