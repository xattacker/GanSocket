package com.xattacker.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtility
{
	public enum DateTimeFormatType
	{
	   YEAR, // yyyy
	   MONTH_SIMPLE, // yyyyMM
	   MONTH_DATE_SIMPLE, //MMdd
	   DATE_SIMPLE, // yyyyMMdd
	   TIME_SIMPLE, // HHmmss
	   DATETIME_SIMPLE, // yyyyMMddHHmmss
	   MONTH_COMPLETE, // yyyy-MM
	   MONTH_DATE_COMPLETE, // MM-dd
	   DATE_COMPLETE, // yyyy-MM-dd
	   TIME_COMPLETE, // HH:mm:ss
	   DATETIME_COMPLETE // yyyy-MM-dd HH:mm:ss
	}

	// in order to hide constructor
	private DateTimeUtility()
	{
	}
	
   public static String getDateTimeString(DateTimeFormatType aType)
   {
		return getDateTimeString(new Date(), aType);
   }
   
   public static String getDateTimeString(Date aDate, DateTimeFormatType aType)
   {
		return getFormat(aType).format(aDate);
   }
   
   public static Date parseDate(String aDateString, DateTimeFormatType aType) throws ParseException
   {
   	return getFormat(aType).parse(aDateString);
   }
   
   public static long getCurrentTimeStamp()
   {
   	return System.currentTimeMillis();
   }
   
   private static DateFormat getFormat(DateTimeFormatType aType)
   {
   	DateFormat format = null;

       switch (aType)
       {
           case YEAR:
               format = new SimpleDateFormat("yyyy");
               break;

           case MONTH_SIMPLE:
               format = new SimpleDateFormat("yyyyMM");
               break;
               
           case MONTH_DATE_SIMPLE:
               format = new SimpleDateFormat("MMdd");
               break;

           case DATE_SIMPLE:
               format = new SimpleDateFormat("yyyyMMdd");
               break;

           case TIME_SIMPLE:
               format = new SimpleDateFormat("HHmmss");
               break;

           case DATETIME_SIMPLE:
               format = new SimpleDateFormat("yyyyMMddHHmmss");
               break;

           case MONTH_COMPLETE:
               format = new SimpleDateFormat("yyyy-MM");
               break;
               
           case MONTH_DATE_COMPLETE:
               format = new SimpleDateFormat("MM-dd");
               break;
              
           case DATE_COMPLETE:
               format = new SimpleDateFormat("yyyy-MM-dd");
               break;

           case TIME_COMPLETE:
               format = new SimpleDateFormat("HH:mm:ss");
               break;

           case DATETIME_COMPLETE:
               format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               break;
       }

       return format;
   }
}
