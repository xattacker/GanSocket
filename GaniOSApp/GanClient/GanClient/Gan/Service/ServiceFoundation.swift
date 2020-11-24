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
    
    internal func send(_ type: FunctionType) -> Result<ResponsePack, SocketError>
    {
        switch self.createSocket()
        {
            case .success(let client):
                
                
                
                return .failure(SocketError.queryFailed)
                
            case .failure(let error):
                return .failure(error)
        }
    }
    
    internal func createSocket() -> Result<TCPClient, SocketError>
    {
        return self.agent.createSocket()
    }
}
