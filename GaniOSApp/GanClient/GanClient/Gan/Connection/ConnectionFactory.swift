//
//  ConnectionFactory.swift
//  GanClient
//
//  Created by xattacker on 2020/11/29.
//

import Foundation


internal class ConnectionFactory
{
    static func createConnection(_ address: String, port: Int) -> Result<SocketConnection, Error>
    {
        let client = TCPClient(address: address, port: Int32(port))

        switch client.connect(timeout: 3)
        {
            case .success(()):
                return .success(client)
                
            case .failure(let error):
                return .failure(error)
        }
    }
}


extension TCPClient: SocketConnection
{
    var available: Int
    {
        return Int(self.bytesAvailable() ?? 0)
    }
}
