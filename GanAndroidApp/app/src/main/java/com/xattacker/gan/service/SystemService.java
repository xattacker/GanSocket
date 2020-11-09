package com.xattacker.gan.service;

import java.util.Date;

import com.xattacker.binary.TypeConverter;
import com.xattacker.gan.GanAgent;
import com.xattacker.gan.data.FunctionType;
import com.xattacker.gan.data.ResponsePack;

public abstract class SystemService extends ServiceFoundation
{
	protected SystemService(GanAgent aAgent)
	{
		super(aAgent);
	}

	public String getIPAddress()
	{
		String ip = null;
		
		try
		{
			ResponsePack response = send(FunctionType.GET_IP, null);
			if (response != null)
			{
				if (response.getResult())
				{
					ip = new String(response.getContent());
				}
				
				response = null;
			}
		}
		catch (Exception ex)
		{
			ip = null;
		}
		finally
		{
		}
		
		return ip;
	}
	
	public Date getSystemTime()
	{
		Date time = null;
		
		try
		{
			ResponsePack response = send(FunctionType.GET_SYSTEM_TIME, null);
			if (response != null)
			{
				if (response.getResult())
				{
					long timestamp = TypeConverter.byteToLong(response.getContent());
					time = new Date(timestamp);
				}
				
				response = null;
			}
		}
		catch (Exception ex)
		{
			time = null;
		}
		finally
		{
		}
		
		return time;
	}
}
