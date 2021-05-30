package com.xattacker.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.xattacker.util.DateTimeUtility.DateTimeFormatType;


public final class Logger
{
   public enum LogLevel
   {
       EXCEPTION, // only log out exception message
       ERROR, // log out exception and error messages
       WARNING, // log out exception, error and warning messages 
       INFO, // log out all kinds of messages
       DEBUG // log out messages in DEBUG mode 
   }
   
   private class LogRecord
   {
   	private LogLevel _lv;
   	private String _tag;
   	private String _log;
   }
   
	public interface LogStorage
	{
		void onLogStored(LogRecord aRecord);
		void release();
	}
	
	
	private static Logger _instance;
	
	private LogStorage _storage;

	private Logger(LogStorage aStorage)
	{
		if (aStorage != null)
		{
			_storage = aStorage;
		}
		else
		{
			_storage = new ConsolePrintLogStorage();
		}
	}
	
	public static void initial()
	{
		initial(null);
	}
	
	public static void initial(LogStorage aStorage)
	{
		if (_instance == null)
		{
			_instance = new Logger(aStorage);
		}
	}
	
	public static void release()
	{
		if (_instance != null)
		{
			_instance.doRelease();
			_instance = null;
		}
	}
	
	public static Logger instance()
	{
		return _instance;
	}
	
	public void debug(String aTag, String aLog)
	{
		if (UtilToolkit.isDebug())
		{
			storeLog(LogLevel.DEBUG, aTag, aLog);
		}
	}
	
	public void debug(String aLog)
	{
		debug(null, aLog);
	}
	
	public void log(String aLog)
	{
		log(null, aLog);
	}
	
	public void log(String aTag, String aLog)
	{
		storeLog(LogLevel.INFO, aTag, aLog);
	}
	
	public void warn(String aLog)
	{
		warn(null, aLog);
	}
	
	public void warn(String aTag, String aLog)
	{
		storeLog(LogLevel.WARNING, aTag, aLog);
	}
	
	public void error(String aLog)
	{
		error(null, aLog);
	}
	
	public void error(String aTag, String aLog)
	{
		storeLog(LogLevel.ERROR, aTag, aLog);
	}
	
	public void except(Throwable aTh)
	{
		except(null, aTh);
	}
	
	public void except(String aTag, Throwable aTh)
	{
		String log = convertThowableToString(aTh);
		storeLog(LogLevel.EXCEPTION, aTag, log);
	}

	private String convertThowableToString(Throwable aTh)
	{
		StringBuilder output = new StringBuilder();
		
	   StringWriter swriter = new StringWriter();
	   PrintWriter pwriter = new PrintWriter(swriter);
	   aTh.printStackTrace(pwriter);
	   
	   output.append(swriter.toString());

      try
      {
      	pwriter.close();
      	swriter.close();
      }
      catch (Exception ex)
      {
      }

      return output.toString();
	}
	
	private void storeLog(LogLevel aLv, String aTag, String aLog)
	{
		if (_storage != null)
		{
			LogRecord record = new LogRecord();
			record._lv = aLv;
			record._tag = aTag;
			record._log = aLog;
			_storage.onLogStored(record);
		}
	}
	
	private void doRelease()
	{
		if (_storage != null)
		{
			_storage.release();
			_storage = null;
		}
	}
	
	
	public class ConsolePrintLogStorage implements LogStorage
	{
		@Override
		public void onLogStored(LogRecord aRecord)
		{
			StringBuilder builder = new StringBuilder();
			builder.append(DateTimeUtility.getDateTimeString(DateTimeFormatType.DATETIME_COMPLETE));
			builder.append(" ");
			builder.append(aRecord._lv);
			builder.append(" ");
			
			if (aRecord._tag != null && aRecord._tag.length() > 0)
			{
				builder.append("[");
				builder.append(aRecord._tag);
				builder.append("]");
			}
			
			builder.append(aRecord._log);
			
			System.out.println(builder.toString());
		}

		@Override
		public void release()
		{
		}
	}
}
