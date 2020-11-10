package com.xattacker.gan.service

import android.util.Log
import com.xattacker.binary.BinaryBuffer
import com.xattacker.gan.GanAgent
import com.xattacker.gan.data.FunctionType

abstract class MsgService protected constructor(aAgent: GanAgent?) : ServiceFoundation(aAgent)
{
    fun sendMsg(aReceiver: String, aMsg: String): Boolean
    {
        var result = false
        try
        {
            val buffer = BinaryBuffer()
            buffer.writeString(aReceiver)
            buffer.writeString(aMsg)

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
        finally
        {
        }

        return result
    }
}