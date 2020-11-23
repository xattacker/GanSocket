//
//  TypeConverter.swift
//  UtilToolKit
//
//  Created by tao on 2016/8/15.
//  Copyright © 2016年 Xattacker. All rights reserved.
//

import Foundation


public final class TypeConverter
{
    public class func byteToShort(_ bytes: UnsafePointer<UInt8>) -> Int16
    {
        var value: Int16 = Int16(bytes[0])
        
        value &= 0xff
        value |= (Int16(bytes[1]) << 8)
        
        return value
    }
    
    public class func shortToByte(_ int: Int16) -> UnsafeMutablePointer<UInt8>
    {
        let bytes = UnsafeMutablePointer<UInt8>.allocate(capacity: MemoryLayout<Int16>.size)
        var temp = int
        
        for i in 0 ..< MemoryLayout<Int16>.size
        {
            bytes[i] = UInt8(temp & 0xff)
            temp = temp >> 8
        }
        
        return bytes
    }
    
    public class func byteToInt(_ bytes: UnsafePointer<UInt8>) -> Int32
    {
        var value: Int32 = Int32(bytes[0])
        
        value &= 0xff
        value |= (Int32(bytes[1]) << 8)
        
        value &= 0xffff
        value |= (Int32(bytes[2]) << 16)
        
        value &= 0xffffff
        value |= (Int32(bytes[3]) << 24)

        return value
    }
    
    public class func intToByte(_ int: Int32) -> UnsafeMutablePointer<UInt8>
    {
        let bytes = UnsafeMutablePointer<UInt8>.allocate(capacity: MemoryLayout<Int32>.size)
        var temp = int
        
        for i in 0 ..< MemoryLayout<Int32>.size
        {
            bytes[i] = UInt8(temp & 0xff)
            temp = temp >> 8
        }
        
        return bytes
    }
    
    public class func byteToLongLong(_ bytes: UnsafePointer<UInt8>) -> Int64
    {
        var value: Int64 = Int64(bytes[0])
 
        value &= 0xff
        value |= (Int64(bytes[1]) << 8)
        
        value &= 0xffff
        value |= (Int64(bytes[2]) << 16)
        
        value &= 0xffffff
        value |= (Int64(bytes[3]) << 24)
        
        value &= 0xffffffff
        value |= (Int64(bytes[4]) << 32)
        
        value &= 0xffffffffff
        value |= (Int64(bytes[5]) << 40)
        
        value &= 0xffffffffffff
        value |= (Int64(bytes[6]) << 48)

        value &= 0xffffffffffffff
        value |= (Int64(bytes[7]) << 56)

        return value
    }
    
    public class func longlongToByte(_ long: Int64) -> UnsafeMutablePointer<UInt8>
    {
        let bytes = UnsafeMutablePointer<UInt8>.allocate(capacity: MemoryLayout<Int64>.size)
        var temp = long

        for i in 0 ..< MemoryLayout<Int64>.size
        {
            bytes[i] = UInt8(temp & 0xff)
            temp = temp >> 8
        }
        
        return bytes
    }
    
    public class func doubleToByte(_ d: Double) -> UnsafeMutablePointer<UInt8>
    {
        return self.longlongToByte(self.doubleToRawBits(d))
    }
    
    public class func byteToDouble(_ bytes: UnsafePointer<UInt8>) -> Double
    {
        return rawBitsToDouble(self.byteToLongLong(bytes))
    }
    
    private class func doubleToRawBits(_ d: Double) -> Int64
    {
        var bits: Int64 = 0
        var temp = d
        memcpy(&bits, &temp, MemoryLayout<Int64>.size)
     
        return bits
    }
    
    private class func rawBitsToDouble(_ bits: Int64) -> Double
    {
        var d: Double = 0
        var temp = bits
        memcpy(&d, &temp, MemoryLayout<Double>.size)
        
        return d
    }
}


extension Int16
{
    public func toByte() -> UnsafeMutablePointer<UInt8>
    {
        return TypeConverter.shortToByte(self)
    }
}

extension Int32
{
    public func toByte() -> UnsafeMutablePointer<UInt8>
    {
        return TypeConverter.intToByte(self)
    }
}

extension Int64
{
    public func toByte() -> UnsafeMutablePointer<UInt8>
    {
        return TypeConverter.longlongToByte(self)
    }
}

extension Double
{
    public func toByte() -> UnsafeMutablePointer<UInt8>
    {
        return TypeConverter.doubleToByte(self)
    }
}
