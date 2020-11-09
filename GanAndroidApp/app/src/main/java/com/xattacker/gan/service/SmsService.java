package com.xattacker.gan.service;

import com.xattacker.binary.BinaryBuffer;
import com.xattacker.gan.GanAgent;
import com.xattacker.gan.data.FunctionType;
import com.xattacker.gan.data.ResponsePack;

public abstract class SmsService extends ServiceFoundation
{
	protected SmsService(GanAgent aAgent)
	{
		super(aAgent);
	}

	public boolean sendSms(String aReceiver, String aMsg)
	{
		boolean result = false;

		try
		{
			BinaryBuffer buffer = new BinaryBuffer();
			buffer.writeString(aReceiver);
			buffer.writeString(aMsg);
			
			ResponsePack response = send(FunctionType.SEND_SMS, buffer.getData());
			if (response != null)
			{
				result = response.getResult();
				
				response = null;
			}
			
			buffer = null;
		}
		catch (Exception ex)
		{
			android.util.Log.i("aaa", "aere " + ex.toString());
		}
		finally
		{
		}
		
		return result;
	}
}
