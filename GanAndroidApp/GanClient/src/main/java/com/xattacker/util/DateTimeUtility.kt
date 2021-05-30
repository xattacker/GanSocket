package com.xattacker.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


enum class DateTimeFormatType(internal var _value: String)
{
    YEAR("yyyy"),
    MONTH_SIMPLE("yyyyMM"),
    MONTH_DATE_SIMPLE("MMdd"),
    DATE_SIMPLE("yyyyMMdd"),
    TIME_SIMPLE("HHmmss"),
    DATETIME_SIMPLE("yyyyMMddHHmmss"),
    MONTH_COMPLETE("yyyy-MM"),
    MONTH_DATE_COMPLETE("MM-dd"),
    DATE_COMPLETE("yyyy-MM-dd"),
    TIME_COMPLETE("HH:mm:ss"),
    DATETIME_COMPLETE("yyyy-MM-dd HH:mm:ss");

    companion object
    {
        fun parse(value: String): DateTimeFormatType
        {
            for (type in DateTimeFormatType.values())
            {
                if (type._value == value)
                {
                    return type
                }
            }

            return DATETIME_COMPLETE
        }
    }
}


object DateTimeUtility
{
    fun getDateTimeString(aType: DateTimeFormatType, aTimeZone: TimeZone? = null): String
    {
        return getDateTimeString(Date(), aType)
    }

    fun getDateTimeString(aDate: Date, aType: DateTimeFormatType, aTimeZone: TimeZone? = null): String
    {
        return getDateTimeString(aDate, aType._value, aTimeZone)
    }

    fun getDateTimeString(aDate: Date, aFormat: String, aTimeZone: TimeZone? = null): String
    {
        val format = SimpleDateFormat(aFormat)

        if (aTimeZone != null)
        {
            format.timeZone = aTimeZone
        }

        return format.format(aDate)
    }

    @Throws(ParseException::class)
    fun parseDate(aDateString: String, aType: DateTimeFormatType): Date?
    {
        return parseDate(aType._value, aDateString)
    }

    fun parseDate(aDateString: String, aFormat: String, aTimeZone: TimeZone? = null): Date?
    {
        var date: Date? = null

        try
        {
            val format = SimpleDateFormat(aFormat)

            if (aTimeZone != null)
            {
                format.timeZone = aTimeZone
            }

            date = format.parse(aDateString)
        }
        catch(ex: Exception)
        {
        }

        return date
    }

    val currentTimeStamp: Long
        get() = System.currentTimeMillis()
}