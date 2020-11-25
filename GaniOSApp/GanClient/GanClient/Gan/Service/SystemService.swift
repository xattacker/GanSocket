//
//  SystemService.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation


public final class SystemService: ServiceFoundation
{
    public func getIP() -> String?
    {
        var ip: String?
        
        if let response = self.send(FunctionType.get_ip), response.result
        {
            ip = response.responseString
        }

        return ip
    }
    
    public func getSystemTime() -> Date?
    {
        var time: Date?
        
        if let response = self.send(FunctionType.get_system_time), response.result
        {
            if let data = response.response,
               let timestamp = BinaryBuffer(data: data).readLongLong()
            {
                time = Date(timestamp: timestamp)
            }
        }

        return time
    }
}
