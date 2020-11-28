//
//  SocketConnection.swift
//  GanClient
//
//  Created by xattacker on 2020/11/29.
//

import Foundation


internal protocol SocketConnection
{
    func send(data: Data) -> Result<Void, Error>
    func read(_ expectlen: Int, timeout: Int) -> [UInt8]?
    func available() -> Int
    func close()
}
