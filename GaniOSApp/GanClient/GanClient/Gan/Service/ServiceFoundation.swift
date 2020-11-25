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
    
    internal func send(_ type: FunctionType, request: Data? = nil) -> Result<ResponsePack, SocketError>
    {
        switch self.createSocket()
        {
            case .success(let client):
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
                
                switch client.send(data: buffer.data)
                {
                    case .success(()):
                        Thread.sleep(forTimeInterval: 0.3)
                        
                        guard let data = client.read(1024*10, timeout: 5) else
                        {
                            client.close()
                            
                            return .failure(SocketError.queryFailed)
                        }
                        
                        
                        client.close()
                        
                        let buffer2 = BinaryBuffer(bytes: data, length: UInt(data.count))
                        
                        let response = ResponsePack()
                        if response.fromBinaryReadable(buffer2)
                        {
                            return .success(response)
                        }
                        else
                        {
                            return .failure(SocketError.responseFailed)
                        }
                        
                    case .failure(let error):
                        return .failure(error)
                }
                
            case .failure(let error):
                return .failure(error)
        }
    }
    
    internal func createSocket() -> Result<TCPClient, SocketError>
    {
        return self.agent.createSocket()
    }
}
