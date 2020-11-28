//
//  AppProperties.swift
//  GanClient
//
//  Created by xattacker on 2018/8/22.
//  Copyright © 2018年 xattacker. All rights reserved.
//

import Foundation


extension Array
{
    public var isEmpty: Bool
    {
        return self.count <= 0
    }
    
    @discardableResult
    public mutating func remove<U: Equatable>(_ object: U) -> Bool
    {
        for (idx, objectToCompare) in self.enumerated()
        {
            if let to = objectToCompare as? U
            {
                if object == to
                {
                    self.remove(at: idx)
                    return true
                }
            }
        }
        
        return false
    }
}


extension Array where Element: Numeric
{
    public func sum() -> Element
    {
        return self.reduce(0, {$0 + $1})
    }
}


extension Array where Element == String
{
    public func combine(_ separator: String = ",") -> String
    {
        return self.reduce("", {$0 + ($0.length > 0 ? separator : "") + $1})
    }
}
