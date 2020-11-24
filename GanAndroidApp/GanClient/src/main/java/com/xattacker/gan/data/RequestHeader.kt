package com.xattacker.gan.data

import com.xattacker.json.JsonSerializable
import org.json.JSONObject

class RequestHeader : JsonSerializable
{
    var type: FunctionType? = null
    var owner: String? = null
    var sessionId: String? = null
    var deviceType = 0

    override fun toJson(): String
    {
        var json = ""
        try
        {
            val jobj = JSONObject()
            jobj.put("Type", type?.value())
            jobj.put("Owner", owner ?: "")
            jobj.put("SessionId", sessionId ?: "")
            jobj.put("DeviceType", deviceType)
            json = jobj.toString()
        }
        catch (ex: Exception)
        {
        }
        return json
    }

    override fun fromJson(aJson: String): Boolean
    {
        var result = false
        if (aJson.length > 0)
        {
            try
            {
                val jobj = JSONObject(aJson)
                type = FunctionType.parse(jobj.optInt("Type", 0))
                owner = jobj.optString("Owner", "")
                sessionId = jobj.optString("SessionId", "")
                deviceType = jobj.getInt("DeviceType")
                result = true
            }
            catch (ex: Exception)
            {
                result = false
            }
        }
        return result
    }
}