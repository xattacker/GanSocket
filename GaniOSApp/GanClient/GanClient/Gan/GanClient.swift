//
//  GanClient.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation


public final class GanClient
{
    private let address: String
    private let port: Int
    private weak var delegate: GanClientDelegate?
    private var sessionInfo: SessionInfo?
    
    public var isConnected: Bool
    {
        return self.sessionInfo != nil
    }
    
    public lazy var accountService: AccountService = AccountService(agent: self, delegate: self)
    public lazy var messageService: MessageService = MessageService(agent: self)
    public lazy var systemService: SystemService = SystemService(agent: self)
    private lazy var callbackService = CallbackService(agent: self, delegate: self)
    
    public init(address: String, port: Int, delegate: GanClientDelegate)
    {
        self.address = address
        self.port = port
        self.delegate = delegate
    }
    
    deinit
    {
        self.sessionInfo = nil
        self.delegate = nil
    }
}


extension GanClient: GanAgent
{
    var account: String?
    {
        return self.sessionInfo?.account
    }
    
    var sessionId: String?
    {
        return self.sessionInfo?.sessionId
    }
    
    func createSocket() -> Result<TCPClient, SocketError>
    {
        let client = TCPClient(address: self.address, port: Int32(self.port))

        switch client.connect(timeout: 3)
        {
            case .success(()):
                return .success(client)
                
            case .failure(let error):
                return .failure(error)
        }
    }
}


extension GanClient: AccountServiceDelegate, CallbackServiceDelegate
{
    func onLoginSucceed(session: SessionInfo)
    {
        self.sessionInfo = session // must be set first
        
        if self.callbackService.connect()
        {
            self.delegate?.onAccountLoggedIn(account: session.account)
        }
        else
        {
            self.sessionInfo = nil
        }
    }
    
    func onLoggedOut(account: String)
    {
        self.delegate?.onAccountLoggedOut(account: account)
        self.callbackService.disconnect()
        self.sessionInfo = nil
    }
    
    func onMessageReceived(message: MessageData)
    {
        // bypass to another delegate
        self.delegate?.onMessageReceived(message: message)
    }
}
