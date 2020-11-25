//
//  Data.swift
//  GanClient
//
//  Created by xattacker on 2017/9/30.
//  Copyright © 2017年 xattacker. All rights reserved.
//

import Foundation


extension Data
{
    public var hexString: String
    {
        return self.reduce("", {$0 + String(format: "%02x", $1)})
    }
    
    public var array: [UInt8]
    {
        return self.withUnsafeBytes({
                    (bytes: UnsafeRawBufferPointer) -> [UInt8] in
                    [UInt8](bytes)
                })
    }
    
    public var mimeString: String?
    {
        return MimeCodec.encode(self)
    }
}


extension UnsafeMutableRawBufferPointer
{
    public func convertPointer<T>() -> UnsafeMutablePointer<T>?
    {
        return self.baseAddress?.assumingMemoryBound(to: T.self)
    }
}


extension UnsafeRawBufferPointer
{
    public func convertPointer<T>() -> UnsafePointer<T>?
    {
        return self.baseAddress?.assumingMemoryBound(to: T.self)
    }
}
