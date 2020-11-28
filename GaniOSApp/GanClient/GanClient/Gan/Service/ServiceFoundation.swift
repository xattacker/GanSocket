//
//  ServiceFoundation.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation


public class ServiceFoundation
{
    internal let agent: GanAgent
    
    init(agent: GanAgent)
    {
        self.agent = agent
    }
    
    internal func send(_ type: FunctionType, closeConnection: Bool = true, request: Data? = nil) -> ResponsePack?
    {
        switch self.createSocket()
        {
            case .success(let client):
                let request_data = self.createRequestPack(type, request: request)
                
                switch client.send(data: request_data)
                {
                    case .success(()):
                        Thread.sleep(forTimeInterval: 0.3)
                        
                        guard let data = client.read(1024*10, timeout: 5) else
                        {
                            client.close()
                            
                            return nil
                        }
                        
                        
                        if closeConnection
                        {
                            client.close()
                        }
                        
                        let buffer = BinaryBuffer(bytes: data, length: UInt(data.count))
                        let response = ResponsePack()
                        if response.fromBinaryReadable(buffer)
                        {
                            if !closeConnection
                            {
                                response.connection = client
                            }
                            
                            return response
                        }
                        else
                        {
                            return nil
                        }
                        
                    case .failure(let error):
                        print(error)
                        return nil
                }
                
            case .failure(let error):
                print(error)
                return nil
        }
    }
    
    internal func createRequestPack(_ type: FunctionType, request: Data? = nil) -> Data
    {
        let buffer = BinaryBuffer()

        let data_buffer = BinaryBuffer()
        let header = RequestHeader()
        header.type = type
        header.owner = self.agent.account
        header.sessionId = self.agent.sessionId
        data_buffer.writeString(header.toJSONString() ?? "")
        
        if let request = request, request.count > 0
        {
            data_buffer.writeData(request)
        }
        
        PackChecker.addHeader(data_buffer.length, buffer: buffer)
        buffer.writeBuffer(data_buffer)
        
        return buffer.data
    }
    
    internal func createSocket() -> Result<SocketConnection, Error>
    {
        return self.agent.createSocket()
    }
}
