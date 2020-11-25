package com.xattacker.json

import com.google.gson.GsonBuilder
import com.xattacker.gan.data.FunctionType
import com.xattacker.gan.data.FunctionTypeSerializer

abstract class JsonObject : JsonSerializable
{
    open fun onBuilderPrepared(builder: GsonBuilder)
    {
    }

    override fun toJson(): String
    {
        return JsonUtility.createGson {
            builder: GsonBuilder ->
            onBuilderPrepared(builder)
        }.toJson(this)
    }
}