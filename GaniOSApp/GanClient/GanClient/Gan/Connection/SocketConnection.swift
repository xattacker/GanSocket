//
//  SocketConnection.swift
//  GanClient
//
//  Created by xattacker on 2020/11/29.
//

import Foundation


internal protocol SocketConnection
{
    var available: Int { get }
    
    func send(data: Data) -> Result<Void, Error>
    func read(_ expectlen: Int, timeout: Int) -> [UInt8]?
    func close()
}


internal protocol ConnectionFactory
{
    func createConnection(_ address: String, port: Int) -> Result<SocketConnection, Error>
}
