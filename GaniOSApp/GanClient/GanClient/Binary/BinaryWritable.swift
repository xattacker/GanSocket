//
//  BinaryWritable.swift
//  GanClient
//
//  Created by tao on 2016/4/1.
//  Copyright © 2016年 Xattacker. All rights reserved.
//

import Foundation


// fluent interface
public protocol BinaryWritable
{
    associatedtype Writable: BinaryWritable
    
    func writeData(_ data: Data) -> Writable
    func writeBinary(_ bytes: UnsafeRawPointer, offset: UInt, length: UInt) -> Writable
    func writeShort(_ value: Int16) -> Writable
    func writeInteger(_ value: Int32) -> Writable
    func writeLongLong(_ value: Int64) -> Writable
    func writeDouble(_ value: Double) -> Writable
    func writeString(_ value: String) -> Writable
}
