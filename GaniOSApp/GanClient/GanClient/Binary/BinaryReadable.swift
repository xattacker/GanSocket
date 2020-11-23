//
//  BinaryReadable.swift
//  UtilToolKit
//
//  Created by tao on 2016/4/1.
//  Copyright © 2016年 Xattacker. All rights reserved.
//

import Foundation


public protocol BinaryReadable
{
    func hasAvailable() -> Bool
    
    func readBinary(_ length: UInt) -> UnsafeMutableRawPointer?
    func readShort() -> Int16?
    func readInteger() -> Int32?
    func readLongLong() -> Int64?
    func readDouble() -> Double?
    func readString() -> String?
}
