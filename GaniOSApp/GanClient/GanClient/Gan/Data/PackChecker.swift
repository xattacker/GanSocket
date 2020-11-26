//
//  PackChecker.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation


internal struct ValidResult
{
    var valid: Bool
    var length: Int
}

internal final class PackChecker
{
    private static let HEAD = "<GAN_PACK>"
    private static let HEAD_BYTE = HEAD.data(using: String.Encoding.utf8)!
    
    static func isValidPack(_ buffer: BinaryBuffer) -> ValidResult
    {
        var valid = false
        var length = 0
        
        print("pack size: \(buffer.length)")
        if buffer.length >= headerLength()
        {
            let compare = buffer.data.subdata(in: 0 ..< HEAD_BYTE.count)
            valid = compare.array.elementsEqual(HEAD_BYTE.array)
            
            if valid
            {
                buffer.seekTo(UInt(HEAD_BYTE.count))
                length = Int(buffer.readInteger() ?? 0)
                print("data size: \(length)")
            }
            else
            {
                print("pack invalid !!")
            }
        }
        
        return ValidResult(valid: valid, length: length)
    }
    
    static func addHeader(_ dataLength: UInt, buffer: BinaryBuffer)
    {
        buffer.writeData(HEAD_BYTE)
        buffer.writeInteger(Int32(dataLength))
    }
    
    static func headerLength() -> Int
    {
        return HEAD_BYTE.count + MemoryLayout<Int32>.size
    }
}
