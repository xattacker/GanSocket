//
//  Dictionary.swift
//  GanClient
//
//  Created by xattacker on 2020/5/7.
//  Copyright © 2018年 xattacker. All rights reserved.
//

import Foundation


extension Dictionary where Key : Hashable
{
    public var keys: [Key]
    {
        return self.map { $0.key }
    }
    
    public var values: [Value]
    {
        return self.map { $0.value }
    }
}
