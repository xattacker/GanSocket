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
    func onLoginSucceed(session: SessionInfo)
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
        
        switch self.send(FunctionType.login, request: buffer.data)
        {
            case .success(let response):
                if response.result,
                   let data = response.response,
                   let session_id = String(data: data, encoding: .utf8)
                {
                    self.delegate.onLoginSucceed(session: SessionInfo(account: account, sessionId: session_id))
                    succeed = true
                }
                break
                
            case .failure(_):
                break
        }
        
        return succeed
    }
}
