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
    
    internal func send(_ type: FunctionType, request: Data? = nil) -> ResponsePack?
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
                        
                        
                        client.close()
                        
                        let buffer2 = BinaryBuffer(bytes: data, length: UInt(data.count))
                        
                        let response = ResponsePack()
                        if response.fromBinaryReadable(buffer2)
                        {
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
        buffer.writeData(PackChecker.HEAD_BYTE)
        
        let header = RequestHeader()
        header.type = type
        header.owner = self.agent.account
        header.sessionId = self.agent.sessionId
        buffer.writeString(header.toJSONString() ?? "")
        
        if let request = request, request.count > 0
        {
            buffer.writeData(request)
        }
        
        return buffer.data
    }
    
    internal func createSocket() -> Result<TCPClient, SocketError>
    {
        return self.agent.createSocket()
    }
}
