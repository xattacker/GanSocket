package com.xattacker.gan.service

import com.xattacker.binary.TypeConverter
import com.xattacker.gan.GanAgent
import com.xattacker.gan.data.FunctionType
import java.util.*


class SystemService internal constructor(agent: GanAgent) : ServiceFoundation(agent)
{
    fun getIP(): String?
    {
        var ip: String? = null
        try
        {
            val response = send(FunctionType.GET_IP, null)
            if (response != null && response.result && response.response != null)
            {
                    ip = String(response.response!!)
            }
        }
        catch (ex: Exception)
        {
            ip = null
        }

        return ip
    }

    fun getSystemTime(): Date?
    {
        var time: Date? = null
        try
        {
            val response = send(FunctionType.GET_SYSTEM_TIME, null)
            if (response != null && response.result && response.response != null)
            {
                val timestamp: Long = TypeConverter.byteToLong(response.response!!)
                time = Date(timestamp)
            }
        }
        catch (ex: Exception)
        {
            time = null
        }

        return time
    }
}