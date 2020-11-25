//
//  BinarySerializable.swift
//  GanClient
//
//  Created by tao on 2016/4/1.
//  Copyright © 2016年 Xattacker. All rights reserved.
//

import Foundation


public protocol BinarySerializable
{
    func toBinary() -> Data
    func fromBinary(_ content: Data) -> Bool
}


public protocol BinarySerializable2
{
    func toBinaryWriteable<T: BinaryWritable>(_ writable: T)
    func fromBinaryReadable(_ readable: BinaryReadable) -> Bool
}
