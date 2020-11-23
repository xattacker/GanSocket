//
//  InputBinaryBuffer.swift
//  UtilToolKit
//
//  Created by tao on 2016/4/6.
//  Copyright © 2016年 Xattacker. All rights reserved.
//

import Foundation


public final class InputBinaryBuffer: BinaryReadable
{
    private var ins: InputStream!
    
    required public init(ins: InputStream)
    {
        self.ins = ins
        
        if self.ins.streamStatus == Stream.Status.notOpen
        {
            self.ins.open()
        }
    }
    
    public func close()
    {
        self.ins.close()
    }
     
    // MARK: implement from BinaryReadable
    public func hasAvailable() -> Bool
    {
        return self.ins.hasBytesAvailable
    }
    
    public func readBinary(_ length: UInt) -> UnsafeMutableRawPointer?
    {
        if length <= 0 || !self.ins.hasBytesAvailable
        {
            return nil
        }
        
        
        let temp = UnsafeMutablePointer<UInt8>.allocate(capacity: Int(length))
        self.ins.read(temp, maxLength: Int(length))

        let bytes = UnsafeMutableRawPointer(temp)
        
        // remember to release it
        temp.deallocate()
        
        return bytes
    }
    
    public func readShort() -> Int16?
    {
        var value: Int16? = 0
        
        if let bytes = self.readBinary(UInt(MemoryLayout<Int16>.size))
        {
            let pointer = bytes.assumingMemoryBound(to: UInt8.self)
            value = TypeConverter.byteToShort(pointer)
            
            // remember to release it
            bytes.deallocate()
        }
        
        return value
    }
    
    public func readInteger() -> Int32?
    {
        var value: Int32?
        
        if let bytes = self.readBinary(UInt(MemoryLayout<Int32>.size))
        {
            let pointer = bytes.assumingMemoryBound(to: UInt8.self)
            value = TypeConverter.byteToInt(pointer)
            
            // remember to release it
            bytes.deallocate()
        }
        
        return value
    }
    
    public func readLongLong() -> Int64?
    {
        var value: Int64?
        
        if let bytes = self.readBinary(UInt(MemoryLayout<Int64>.size))
        {
            let pointer = bytes.assumingMemoryBound(to: UInt8.self)
            value = TypeConverter.byteToLongLong(pointer)
            
            // remember to release it
            bytes.deallocate()
        }
        
        return value
    }
    
    public func readDouble() -> Double?
    {
        var value: Double?
        
        if let bytes = self.readBinary(UInt(MemoryLayout<Double>.size))
        {
            let pointer = bytes.assumingMemoryBound(to: UInt8.self)
            value = TypeConverter.byteToDouble(pointer)
            
            // remember to release it
            bytes.deallocate()
        }
        
        return value
    }
    
    public func readString() -> String?
    {
        var value: String?
        
        if let length = self.readInteger(), length > 0
        {
            if let bytes = self.readBinary(UInt(length))
            {
                value = String(
                        bytesNoCopy: bytes,
                        length: Int(length),
                        encoding: String.Encoding.utf8,
                        freeWhenDone: true)

                //bytes.deallocate()
            }
        }
        
        return value
    }
    
    deinit
    {
        self.ins = nil
    }
}
