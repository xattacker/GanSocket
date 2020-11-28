//
//  GanClientDelegate.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation


public protocol GanClientDelegate : class
{
    func onAccountLoggedIn(account: String)
    func onAccountLoggedOut(account: String)
    func onMessageReceived(message: MessageData)
}

