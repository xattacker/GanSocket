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
            case .success(let connection):
                return self.send(type, connection: connection, closeConnection: closeConnection, request: request)
                
            case .failure(let error):
                print(error)
                return nil
        }
    }
    
    internal func send(
    _ type: FunctionType,
    connection: SocketConnection,
    closeConnection: Bool = true,
    request: Data? = nil) -> ResponsePack?
    {
        let request_data = self.createRequestPack(type, request: request)
        
        switch connection.send(data: request_data)
        {
            case .success(()):
                let valid = PackChecker.isValidPack(connection)
                guard valid.valid && valid.length > 0 else
                {
                    connection.close()
                    
                    return nil
                }
                
                guard let data = connection.read(valid.length, timeout: 5) else
                {
                    connection.close()
                    
                    return nil
                }
            
                if closeConnection
                {
                    connection.close()
                }
            
                let buffer = BinaryBuffer(bytes: data, length: UInt(data.count))
                let response = ResponsePack()
                if response.fromBinaryReadable(buffer)
                {
                    if !closeConnection
                    {
                        response.connection = connection
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
        
        PackChecker.packData(data_buffer.data, container: buffer)
        
        return buffer.data
    }
    
    internal func createSocket() -> Result<SocketConnection, Error>
    {
        return self.agent.createSocket()
    }
}
