//
//  GanClient.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation


public final class GanClient
{
    public lazy var accountService: AccountService = AccountService(agent: self)
    public lazy var systemService: SystemService = SystemService(agent: self)
    
    private let address: String
    private let port: Int
    private weak var delegate: GanClientDelegate?

    public init(address: String, port: Int, delegate: GanClientDelegate)
    {
        self.address = address
        self.port = port
        self.delegate = delegate
    }
    
    deinit
    {
        self.delegate = nil
    }
}


extension GanClient: GanAgent
{
    var account: String?
    {
        return nil
    }
    
    var sessionId: String?
    {
        return nil
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
