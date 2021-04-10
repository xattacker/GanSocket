//
//  MessageAck.swift
//  GanClient
//
//  Created by xattacker on 2021/4/10.
//

import Foundation
import ObjectMapper


internal class MessageAck : MappableObj
{
    public var id: String?

    open override func mapping(map: Map)
    {
        super.mapping(map: map)
              
        self.id        <- map["id"]
    }
}
