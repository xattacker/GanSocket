//
//  GanAgent.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation

internal protocol GanAgent
{
    func createSocket() -> Result<TCPClient, SocketError>
}
