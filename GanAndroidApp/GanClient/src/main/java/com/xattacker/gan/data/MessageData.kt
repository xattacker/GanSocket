package com.xattacker.gan.data

import com.google.gson.annotations.SerializedName
import com.xattacker.json.JsonObject

class MessageData: JsonObject()
{
    @SerializedName("id")
    var id: String? = null

    @SerializedName("sender")
    var sender: String? = null

    @SerializedName("time")
    var time: Long? = null

    @SerializedName("message")
    var message: String? = null
}