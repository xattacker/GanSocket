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
    
    internal func send(_ type: FunctionType)
    {
        
    }
    
    internal func createSocket() -> Result<TCPClient, SocketError>
    {
        return self.agent.createSocket()
    }
}
