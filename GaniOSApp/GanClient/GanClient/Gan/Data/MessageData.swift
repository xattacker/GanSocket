//
//  MessageData.swift
//  GanClient
//
//  Created by xattacker on 2020/11/25.
//

import Foundation
import ObjectMapper


public class MessageData : MappableObj
{
    public var id: String?
    public var sender: String?
    public var time: UInt64?
    public var message: String?

    open override func mapping(map: Map)
    {
        super.mapping(map: map)
              
        self.id        <- map["id"]
        self.sender    <- map["sender"]
        self.time      <- map["time"]
        self.message   <- map["message"]
    }
}
