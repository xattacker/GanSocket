package com.xattacker.gan.service

import com.xattacker.binary.TypeConverter
import com.xattacker.gan.GanAgent
import com.xattacker.gan.data.FunctionType
import com.xattacker.gan.data.ResponsePack
import java.util.*


abstract class SystemService protected constructor(aAgent: GanAgent?) : ServiceFoundation(aAgent)
{
    fun getIPAddress(): String?
    {
        var ip: String? = null
        try
        {
            val response = send(FunctionType.GET_IP, null)
            if (response != null && response.result && response.content != null)
            {
                    ip = String(response.content!!)
            }
        }
        catch (ex: Exception)
        {
            ip = null
        }
        finally
        {
        }
        return ip
    }

    fun getSystemTime(): Date?
    {
        var time: Date? = null
        try
        {
            val response = send(FunctionType.GET_SYSTEM_TIME, null)
            if (response != null && response.result && response.content != null)
            {
                val timestamp: Long = TypeConverter.byteToLong(response.content!!)
                time = Date(timestamp)
            }
        }
        catch (ex: Exception)
        {
            time = null
        }
        finally
        {
        }
        return time
    }
}