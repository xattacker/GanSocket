package com.xattacker.gan.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.xattacker.binary.BinaryBuffer;
import com.xattacker.binary.OutputBinaryBuffer;
import com.xattacker.gan.GanAgent;
import com.xattacker.gan.data.PackFormatChecker;
import com.xattacker.gan.data.FunctionType;
import com.xattacker.gan.data.RequestHeader;
import com.xattacker.gan.data.ResponsePack;
import com.xattacker.util.IOUtility;

abstract class ServiceFoundation
{
	protected GanAgent _agent;
	
	protected ServiceFoundation(GanAgent aAgent)
	{
		_agent = aAgent;
	}
	
	protected ResponsePack send(FunctionType aType, byte[] aRequest)
	{
		ResponsePack response = null;
		Socket socket= null;
		
		try
		{
			socket = createSocket();
			
			ByteArrayOutputStream baout = new ByteArrayOutputStream();
			OutputStream out = socket.getOutputStream();

			OutputBinaryBuffer obb = new OutputBinaryBuffer(baout);
			obb.writeBinary(PackFormatChecker.HEAD_BYTE, 0, PackFormatChecker.HEAD_BYTE.length);
			
			RequestHeader header = new RequestHeader();
			header.setType(aType);
			
			String account = _agent.getAccount();
			if (account != null)
			{
				header.setOwner(account);
			}
			
			String session_id = _agent.getSessionId();
			if (session_id != null)
			{
				header.setSessionId(session_id);
			}
			
			obb.writeString(header.toJson());
			
			if (aRequest != null && aRequest.length > 0)
			{
				obb.writeBinary(aRequest, 0, aRequest.length);
			}
			
			obb.flush();
			//obb.close();
			
			byte[] packed = baout.toByteArray();
			out.write(packed);
			out.flush();
			packed = null;
			
			Thread.sleep(200);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IOUtility.readResponse(socket.getInputStream(), bos);

			BinaryBuffer binary = new BinaryBuffer(bos.toByteArray());
			
			response = new ResponsePack();
			if (!response.fromBinary(binary))
			{
				response = null;
			}
		   
			obb.close();
			bos.close();
		}
		catch (Exception ex)
		{
			response = null;
			android.util.Log.i("aaa", "ex " + ex.toString());
		}
		finally
		{
			try
			{
				socket.close();
			}
			catch (Exception ex)
			{
			}
			
			socket = null;
			
			System.gc();
		}
		
		return response;
	}
	
	protected Socket createSocket() throws Exception
	{
		return _agent != null ? _agent.createSocket() : null;
	}
}
