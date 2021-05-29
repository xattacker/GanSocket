package com.xattacker.gan.data

import com.google.gson.annotations.SerializedName
import com.xattacker.json.JsonObject

class MessageAck: JsonObject()
{
    @SerializedName("id")
    var id: String? = null
}