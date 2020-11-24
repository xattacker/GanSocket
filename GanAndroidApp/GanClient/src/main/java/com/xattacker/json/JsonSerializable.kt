package com.xattacker.json


interface JsonSerializable
{
    fun toJson(): String
    fun fromJson(aJson: String): Boolean
}