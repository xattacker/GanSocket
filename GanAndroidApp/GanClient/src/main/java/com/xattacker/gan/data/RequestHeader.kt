package com.xattacker.gan.data

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.xattacker.json.JsonSerializable
import com.xattacker.json.JsonUtility

internal class RequestHeader : JsonSerializable
{
    @SerializedName("Type")
    var type: FunctionType? = null

    @SerializedName("Owner")
    var owner: String? = null

    @SerializedName("SessionId")
    var sessionId: String? = null

    @SerializedName("DeviceType")
    var deviceType = 0

    override fun toJson(): String
    {
        return JsonUtility.createGson {
            builder: GsonBuilder ->
            builder.registerTypeAdapter(FunctionType::class.java, FunctionTypeSerializer())
        }.toJson(this)
    }
}