//
//  RequestHeader.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation
import ObjectMapper


internal class RequestHeader : MappableObj
{
    var type: FunctionType = .unknown
    var owner: String?
    var sessionId: String?
    var deviceType: Int = 0
    
    open override func mapping(map: Map)
    {
        super.mapping(map: map)
              
        self.type         <- map["Type"]
        self.owner        <- map["Owner"]
        self.sessionId    <- map["SessionId"]
        self.deviceType   <- map["DeviceType"]
    }
}
