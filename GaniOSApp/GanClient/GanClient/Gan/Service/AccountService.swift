//
//  AccountService.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation


internal struct SessionInfo
{
    var account: String
    var sessionId: String
}

internal protocol AccountServiceDelegate
{
    func onLoginSucceed(session: SessionInfo, connection: SocketConnection)
    func onLoggedOut(account: String)
}


public final class AccountService: ServiceFoundation
{
    private let delegate: AccountServiceDelegate
    
    internal init(agent: GanAgent, delegate: AccountServiceDelegate)
    {
        self.delegate = delegate
        
        super.init(agent: agent)
    }
    
    public func login(_ account: String, password: String) -> Bool
    {
        var succeed = false
        
        let buffer = BinaryBuffer()
        buffer.writeString(account)
        buffer.writeString(password)
        
        if let response = self.send(FunctionType.login, closeConnection: false, request: buffer.data)
        {
            if response.result, let session_id = response.responseString
            {
                self.delegate.onLoginSucceed(session: SessionInfo(account: account, sessionId: session_id), connection: response.connection!)
                succeed = true
            }
            else
            {
                response.connection?.close()
            }
        }
        
        return succeed
    }
    
    public func logout() -> Bool
    {
        var succeed = false
        
        if let account = self.agent.account
        {
            let buffer = BinaryBuffer()
            buffer.writeString(account)
            
            if let response = self.send(FunctionType.logout, request: buffer.data), response.result
            {
                self.delegate.onLoggedOut(account: account)
                succeed = true
            }
        }

        return succeed
    }
}
