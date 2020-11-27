package com.xattacker.gan.service

import android.util.Log

import com.xattacker.binary.BinaryBuffer
import com.xattacker.gan.GanAgent
import com.xattacker.gan.data.FunctionType
import com.xattacker.gan.data.PackChecker
import com.xattacker.gan.data.RequestHeader
import com.xattacker.gan.data.ResponsePack
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.Socket

abstract class ServiceFoundation protected constructor(protected var agent: GanAgent)
{
    protected fun send(aType: FunctionType, aRequest: ByteArray? = null, closeConnection: Boolean = true): ResponsePack?
    {
        var response: ResponsePack? = null
        var socket: Socket? = null

        try
        {
            socket = createSocket()
            if (socket != null)
            {
                val out = socket.getOutputStream()

                val buffer = BinaryBuffer()
                val header = RequestHeader()
                header.type = aType
                header.owner = agent.account
                header.sessionId = agent.sessionId
                buffer.writeString(header.toJson())

                if (aRequest != null && aRequest.size > 0)
                {
                    buffer.writeBinary(aRequest, 0, aRequest.size)
                }

                PackChecker.pack(buffer.data, out)
                out.flush()

                Thread.sleep(200)

                val bos = ByteArrayOutputStream()
                readResponse(socket.getInputStream(), bos)

                val binary = BinaryBuffer(bos.toByteArray())
                response = ResponsePack()
                if (!response.fromBinary(binary))
                {
                    response = null
                }

                bos.close()

                if (!closeConnection)
                {
                    response?.connection = socket
                }
            }
        }
        catch (ex: Exception)
        {
            response = null
            Log.i("aaa", "ex $ex")
        }
        finally
        {
            if (closeConnection || response == null || response.result == false)
            {
                try
                {
                    socket?.close()
                }
                catch (ex: Exception)
                {
                }
            }
        }

        return response
    }

    @Throws(Exception::class)
    protected fun createSocket(): Socket?
    {
        return agent.createSocket()
    }

    @Throws(Exception::class)
    protected fun readResponse(aIn: InputStream, aBos: ByteArrayOutputStream)
    {
        val temp = ByteArray(256)
        var index = -1
        do
        {
            while (aIn.read(temp).also {index = it} != -1)
            {
                aBos.write(temp, 0, index)
                if (index < temp.size)
                {
                    break
                }
            }
        } while (aBos.size() == 0)
    }

    protected fun wait(aIn: InputStream, aLength: Int, aMaxTry: Int): Boolean
    {
        var try_count = 0
        do
        {
            Thread.sleep(50)
            try_count++
        } while (aIn.available() < aLength && try_count < aMaxTry)

        return aIn.available() >= aLength
    }
}