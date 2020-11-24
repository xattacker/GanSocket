package com.xattacker.gan.data

import com.google.gson.*
import java.lang.reflect.Type

internal class FunctionTypeSerializer : JsonSerializer<FunctionType>, JsonDeserializer<FunctionType>
{
    override fun serialize(src: FunctionType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        return JsonPrimitive(src?.value())
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): FunctionType
    {
        return FunctionType.parse(json?.asInt ?: FunctionType.UNKNOWN.value())
    }
}