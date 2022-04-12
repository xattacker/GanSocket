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
    private var connectionFactory: ConnectionFactory!
    
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
        self.connectionFactory = TCPConnectionFactory()
    }
    
    deinit
    {
        self.sessionInfo = nil
        self.delegate = nil
        self.connectionFactory = nil
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
    
    func createSocket() -> Result<SocketConnection, Error>
    {
        return self.connectionFactory.createConnection(self.address, port: self.port)
    }
}


extension GanClient: AccountServiceDelegate, CallbackServiceDelegate
{
    func onLoginSucceed(session: SessionInfo, connection: SocketConnection)
    {
        self.sessionInfo = session // must be set first
        self.callbackService.handleConnection(connection)
        self.delegate?.onAccountLoggedIn(account: session.account)
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
