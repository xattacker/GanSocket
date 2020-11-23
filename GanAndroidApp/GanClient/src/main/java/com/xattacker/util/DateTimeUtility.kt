package com.xattacker.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtility
{
    fun getDateTimeString(aType: DateTimeFormatType?): String
    {
        return getDateTimeString(Date(), aType)
    }

    fun getDateTimeString(aDate: Date?, aType: DateTimeFormatType?): String
    {
        return getFormat(aType)!!.format(aDate)
    }

    @Throws(ParseException::class)
    fun parseDate(aDateString: String?, aType: DateTimeFormatType?): Date
    {
        return getFormat(aType)!!.parse(aDateString)
    }

    val currentTimeStamp: Long
        get() = System.currentTimeMillis()

    private fun getFormat(aType: DateTimeFormatType?): DateFormat?
    {
        var format: DateFormat? = null
        when (aType)
        {
            DateTimeFormatType.YEAR -> format = SimpleDateFormat("yyyy")
            DateTimeFormatType.MONTH_SIMPLE -> format = SimpleDateFormat("yyyyMM")
            DateTimeFormatType.MONTH_DATE_SIMPLE -> format = SimpleDateFormat("MMdd")
            DateTimeFormatType.DATE_SIMPLE -> format = SimpleDateFormat("yyyyMMdd")
            DateTimeFormatType.TIME_SIMPLE -> format = SimpleDateFormat("HHmmss")
            DateTimeFormatType.DATETIME_SIMPEL -> format = SimpleDateFormat("yyyyMMddHHmmss")
            DateTimeFormatType.MONTH_COMPLETE -> format = SimpleDateFormat("yyyy-MM")
            DateTimeFormatType.MONTH_DATE_COMPLETE -> format = SimpleDateFormat("MM-dd")
            DateTimeFormatType.DATE_COMPLETE -> format = SimpleDateFormat("yyyy-MM-dd")
            DateTimeFormatType.TIME_COMPLETE -> format = SimpleDateFormat("HH:mm:ss")
            DateTimeFormatType.DATETIME_COMPLETE -> format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        }
        return format
    }

    enum class DateTimeFormatType
    {
        YEAR,  // yyyy
        MONTH_SIMPLE,  // yyyyMM
        MONTH_DATE_SIMPLE,  //MMdd
        DATE_SIMPLE,  // yyyyMMdd
        TIME_SIMPLE,  // HHmmss
        DATETIME_SIMPEL,  // yyyyMMddHHmmss
        MONTH_COMPLETE,  // yyyy-MM
        MONTH_DATE_COMPLETE,  // MM-dd
        DATE_COMPLETE,  // yyyy-MM-dd
        TIME_COMPLETE,  // HH:mm:ss
        DATETIME_COMPLETE // yyyy-MM-dd HH:mm:ss
    }
}