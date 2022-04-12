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
    fun getDateTimeString(type: DateTimeFormatType, timeZone: TimeZone? = null): String
    {
        return getDateTimeString(Date(), type)
    }

    fun getDateTimeString(aDate: Date, type: DateTimeFormatType, timeZone: TimeZone? = null): String
    {
        return getDateTimeString(aDate, type._value, timeZone)
    }

    fun getDateTimeString(aDate: Date, format: String, timeZone: TimeZone? = null): String
    {
        val format = SimpleDateFormat(format)

        if (timeZone != null)
        {
            format.timeZone = timeZone
        }

        return format.format(aDate)
    }

    @Throws(ParseException::class)
    fun parseDate(dateString: String, type: DateTimeFormatType): Date?
    {
        return parseDate(type._value, dateString)
    }

    fun parseDate(dateString: String, format: String, timeZone: TimeZone? = null): Date?
    {
        var date: Date? = null

        try
        {
            val format = SimpleDateFormat(format)

            if (timeZone != null)
            {
                format.timeZone = timeZone
            }

            date = format.parse(dateString)
        }
        catch(ex: Exception)
        {
        }

        return date
    }

    val currentTimeStamp: Long
        get() = System.currentTimeMillis()
}