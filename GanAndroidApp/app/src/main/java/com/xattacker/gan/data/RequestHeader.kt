package com.xattacker.gan.data

import com.xattacker.json.JsonSerializable
import org.json.JSONObject

class RequestHeader : JsonSerializable
{
    private var _type: FunctionType? = null
    var owner: String? = null
    var sessionId: String? = null
    var deviceType = 0

    var type: FunctionType?
        get() = _type
        set(aType)
        {
            _type = aType
        }

    override fun toJson(): String?
    {
        var json: String? = null
        try
        {
            val jobj = JSONObject()
            jobj.put("Type", _type!!.value())
            jobj.put("Owner", if (owner != null) owner else "")
            jobj.put("SessionId", if (sessionId != null) sessionId else "")
            json = jobj.toString()
        }
        catch (ex: Exception)
        {
        }
        return json
    }

    override fun fromJson(aJson: String?): Boolean
    {
        var result = false
        if (aJson != null && aJson.length > 0)
        {
            try
            {
                val jobj = JSONObject(aJson)
                _type = FunctionType.parse(jobj.optInt("Type", 0))
                owner = jobj.optString("Owner", "")
                sessionId = jobj.optString("SessionId", "")
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