//
//  BinaryBuffer.swift
//  GanClient
//
//  Created by tao on 2016/4/1.
//  Copyright © 2016年 Xattacker. All rights reserved.
//

import Foundation


public final class BinaryBuffer
{
    private var dataPri = NSMutableData()
    private var index: UInt = 0
    
    init()
    {
    }
    
    init(buffer: BinaryBuffer)
    {
        self.writeData(buffer.data)
        self.index = 0
    }
    
    init(data: Data)
    {
        self.writeData(data)
        self.index = 0
    }
    
    init(bytes: UnsafePointer<UInt8>, length: UInt)
    {
        self.writeBinary(bytes, offset: 0, length: length)
        self.index = 0
    }
    
    // operator overloading
    static func +=(lhs: inout BinaryBuffer, rhs: BinaryBuffer)
    {
        lhs.writeData(rhs.data)
    }
    
    static func +(lhs: BinaryBuffer, rhs: BinaryBuffer) -> BinaryBuffer
    {
        let buffer = BinaryBuffer()
        buffer.writeData(lhs.data)
        buffer.writeData(rhs.data)
        
        return buffer
    }

    public var data: Data
    {
        get
        {
            return self.dataPri as Data
        }
    }

    public func isEmpty() -> Bool
    {
        return self.dataPri.length == 0
    }
    
    public var length: UInt
    {
        get
        {
            return UInt(self.dataPri.length)
        }
    }
    
    public func clear()
    {
        self.dataPri = NSMutableData()
        self.index = 0
    }
    
    public func getBytes() -> UnsafeRawPointer
    {
        return self.dataPri.bytes
    }

    public func getCurrentIndex() -> UInt
    {
        return self.index
    }
    
    public func seekToHead()
    {
        self.index = 0
    }
    
    public func seekTo(_ index: UInt)
    {
        if index >= 0 && Int(index) < self.dataPri.length
        {
            self.index = index
        }
    }

    public func seekToEnd()
    {
        self.index = UInt(self.dataPri.length)
    }
    
    @discardableResult
    public func writeBuffer(_ buffer: BinaryBuffer) -> BinaryBuffer
    {
        self.writeData(buffer.data)
        
        return self
    }
}


extension BinaryBuffer: BinaryReadable
{
    // MARK: implement from BinaryReadable
    public func hasAvailable() -> Bool
    {
        return Int(self.index) < self.dataPri.length - 1
    }
    
    public func readBinary(_ length: UInt) -> UnsafeMutableRawPointer?
    {
        if length <= 0 || Int(self.index + length) > self.dataPri.length
        {
            return nil
        }
        

        let bytes = UnsafeMutableRawPointer.allocate(byteCount: Int(length), alignment: 0)
        self.dataPri.getBytes(bytes, range: NSMakeRange(Int(self.index), Int(length)))
       
        self.index += length

        return bytes
    }
    
    public func readShort() -> Int16?
    {
        var value: Int16?
        
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
}


extension BinaryBuffer: BinaryWritable
{
    // MARK: implement from BinaryWritable
    public typealias Writable = BinaryBuffer
    
    @discardableResult
    public func writeData(_ data: Data) -> BinaryBuffer
    {
        let length = data.count
        if length > 0
        {
            if Int(self.index) == self.dataPri.length // append in the end
            {
                self.dataPri.append(data)
                self.index += UInt(length)
            }
            else
            {
                self.writeBinary((data as NSData).bytes, offset: 0, length: UInt(length))
            }
        }
        
        return self
    }
    
    @discardableResult
    public func writeBinary(_ bytes: UnsafeRawPointer, offset: UInt, length: UInt) -> BinaryBuffer
    {
        if Int(self.index) == self.dataPri.length // append in the end
        {
            if offset == 0
            {
                self.dataPri.append(bytes, length: Int(length))
            }
            else
            {
                let data = NSMutableData()
                data.append(bytes, length: Int(length))
                
                let offset_bytes = UnsafeMutableRawPointer.allocate(byteCount: Int(length), alignment: 0)
                data.getBytes(offset_bytes, range: NSMakeRange(Int(offset), Int(length)))
               
                self.dataPri.append(offset_bytes, length: Int(length))
                
                // remember to release it
                offset_bytes.deallocate()
            }
        }
        else
        {
            let range = NSMakeRange(Int(self.index), Int(length))
            self.dataPri.replaceBytes(in: range, withBytes: bytes)
        }
        
        self.index += length
        
        return self
    }

    @discardableResult
    public func writeShort(_ value: Int16) -> BinaryBuffer
    {
        let bytes = TypeConverter.shortToByte(value)
        self.writeBinary(bytes, offset: 0, length: UInt(MemoryLayout<Int16>.size))
      
        // remember to release it
        bytes.deallocate()
        
        return self
    }

    @discardableResult
    public func writeInteger(_ value: Int32) -> BinaryBuffer
    {
        let bytes = TypeConverter.intToByte(value)
        self.writeBinary(bytes, offset: 0, length: UInt(MemoryLayout<Int32>.size))
        
        // remember to release it
        bytes.deallocate()
        
        return self
    }
    
    @discardableResult
    public func writeLongLong(_ value: Int64) -> BinaryBuffer
    {
        let bytes = TypeConverter.longlongToByte(value)
        self.writeBinary(bytes, offset: 0, length: UInt(MemoryLayout<Int64>.size))
        
        // remember to release it
        bytes.deallocate()
        
        return self
    }

    @discardableResult
    public func writeDouble(_ value: Double) -> BinaryBuffer
    {
        let bytes = TypeConverter.doubleToByte(value)
        self.writeBinary(bytes, offset: 0, length: UInt(MemoryLayout<Double>.size))
        
        // remember to release it
        bytes.deallocate()
        
        return self
    }
    
    @discardableResult
    public func writeString(_ value: String) -> BinaryBuffer
    {
        if value.count == 0
        {
            self.writeInteger(0)
        }
        else
        {
            if let data = value.data(using: String.Encoding.utf8)
            {
                self.writeInteger(Int32(data.count))
                self.writeData(data)
            }
        }
        
        return self
    }
}
