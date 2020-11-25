//
//  MessageService.swift
//  GanClient
//
//  Created by xattacker on 2020/11/25.
//

import Foundation


public final class MessageService: ServiceFoundation
{
    public func sendMessage(_ receiver: String, message: String) -> Bool
    {
        var succeed = false
        
        let buffer = BinaryBuffer()
        buffer.writeString(receiver)
        buffer.writeString(message)
        
        if let response = self.send(FunctionType.send_sms, request: buffer.data), response.result
        {
            succeed = true
        }
        
        return succeed
    }
}
