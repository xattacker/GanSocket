//
//  OutputBinaryBuffer.swift
//  GanClient
//
//  Created by tao on 2016/4/6.
//  Copyright © 2016年 Xattacker. All rights reserved.
//

import Foundation


public final class OutputBinaryBuffer: BinaryWritable
{
    private var outs: OutputStream!
    
    required public init(outs: OutputStream)
    {
        self.outs = outs
        
        if self.outs.streamStatus == Stream.Status.notOpen
        {
            self.outs.open()
        }
    }
    
    public func close()
    {
        self.outs.close()
    }
       
    // MARK: implement from BinaryWritable
    public typealias Writable = OutputBinaryBuffer
    
    @discardableResult
    public func writeData(_ data: Data) -> OutputBinaryBuffer
    {
        let length = data.count
        if length > 0
        {
            self.writeBinary((data as NSData).bytes, offset: 0, length: UInt(length))
        }
        
        return self
    }
    
    @discardableResult
    public func writeBinary(_ bytes: UnsafeRawPointer, offset: UInt, length: UInt) -> OutputBinaryBuffer
    {
        if offset == 0
        {
            let pointer = bytes.assumingMemoryBound(to: UInt8.self)
            self.outs.write(pointer, maxLength: Int(length))
        }
        else
        {
            let data = NSMutableData()
            data.append(bytes, length: Int(length))
            
            let offset_bytes = UnsafeMutableRawPointer.allocate(byteCount: Int(length), alignment: 0)
            data.getBytes(offset_bytes, range: NSMakeRange(Int(offset), Int(length)))

            let pointer = offset_bytes.assumingMemoryBound(to: UInt8.self)
            self.outs.write(pointer, maxLength: Int(length))
    
            // remember to release it
            offset_bytes.deallocate()
        }
        
        return self
    }
    
    @discardableResult
    public func writeShort(_ value: Int16) -> OutputBinaryBuffer
    {
        let bytes = TypeConverter.shortToByte(value)
        self.writeBinary(bytes, offset: 0, length: UInt(MemoryLayout<Int16>.size))
        
        // remember to release it
        bytes.deallocate()
        
        return self
    }
    
    @discardableResult
    public func writeInteger(_ value: Int32) -> OutputBinaryBuffer
    {
        let bytes = TypeConverter.intToByte(value)
        self.writeBinary(bytes, offset: 0, length: UInt(MemoryLayout<Int32>.size))
        
        // remember to release it
        bytes.deallocate()
        
        return self
    }
    
    @discardableResult
    public func writeLongLong(_ value: Int64) -> OutputBinaryBuffer
    {
        let bytes = TypeConverter.longlongToByte(value)
        self.writeBinary(bytes, offset: 0, length: UInt(MemoryLayout<Int64>.size))
        
        // remember to release it
        bytes.deallocate()
        
        return self
    }
    
    @discardableResult
    public func writeDouble(_ value: Double) -> OutputBinaryBuffer
    {
        let bytes = TypeConverter.doubleToByte(value)
        self.writeBinary(bytes, offset: 0, length: UInt(MemoryLayout<Double>.size))
        
        // remember to release it
        bytes.deallocate()
        
        return self
    }
    
    @discardableResult
    public func writeString(_ value: String) -> OutputBinaryBuffer
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
    
    deinit
    {
        self.outs = nil
    }
}
