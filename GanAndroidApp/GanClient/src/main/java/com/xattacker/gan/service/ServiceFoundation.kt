package com.xattacker.gan.service

import android.util.Log

import com.xattacker.binary.BinaryBuffer
import com.xattacker.binary.OutputBinaryBuffer
import com.xattacker.gan.GanAgent
import com.xattacker.gan.data.FunctionType
import com.xattacker.gan.data.PackChecker
import com.xattacker.gan.data.RequestHeader
import com.xattacker.gan.data.ResponsePack
import com.xattacker.util.IOUtility

import java.io.ByteArrayOutputStream
import java.net.Socket

abstract class ServiceFoundation protected constructor(protected var agent: GanAgent)
{
    protected fun send(aType: FunctionType, aRequest: ByteArray? = null): ResponsePack?
    {
        var response: ResponsePack? = null
        var socket: Socket? = null

        try
        {
            socket = createSocket()
            if (socket != null)
            {
                val bout = ByteArrayOutputStream()
                val out = socket.getOutputStream()
                val obb = OutputBinaryBuffer(bout)
                obb.writeBinary(PackChecker.HEAD_BYTE, 0, PackChecker.HEAD_BYTE.size)

                val header = RequestHeader()
                header.type = aType
                header.owner = agent.account
                header.sessionId = agent.sessionId
                obb.writeString(header.toJson())

                if (aRequest != null && aRequest.size > 0)
                {
                    obb.writeBinary(aRequest, 0, aRequest.size)
                }

                obb.flush()
                //obb.close();

                val packed = bout.toByteArray()
                out.write(packed)
                out.flush()

                Thread.sleep(200)

                val bos = ByteArrayOutputStream()
                IOUtility.readResponse(socket.getInputStream(), bos)
                val binary = BinaryBuffer(bos.toByteArray())
                response = ResponsePack()
                if (!response.fromBinary(binary))
                {
                    response = null
                }
                obb.close()
                bos.close()
            }
        }
        catch (ex: Exception)
        {
            response = null
            Log.i("aaa", "ex $ex")
        }
        finally
        {
            try
            {
                socket?.close()
            }
            catch (ex: Exception)
            {
            }
        }

        return response
    }

    @Throws(Exception::class)
    protected fun createSocket(): Socket?
    {
        return agent.createSocket()
    }
}