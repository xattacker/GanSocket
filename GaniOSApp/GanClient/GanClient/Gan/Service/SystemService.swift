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
        
        switch self.send(FunctionType.get_ip)
        {
            case .success(let response):
                if response.result
                {
                    ip = response.responseString
                }
                break
                
            case .failure(let error):
                print(error)
                break
        }

        return ip
    }
    
    public func getSystemTime() -> Date?
    {
        var time: Date?
        
        switch self.send(FunctionType.get_system_time)
        {
            case .success(let response):
                if response.result,
                   let data = response.response,
                   let timestamp = BinaryBuffer(data: data).readLongLong()
                {
                    time = Date(timestamp: timestamp)
                }
                break
                
            case .failure(let error):
                print(error)
                break
        }

        return time
    }
}
