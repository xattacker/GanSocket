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
    private static let VALID_LENGTH = HEAD_BYTE.count + MemoryLayout<Int32>.size
    
    static func isValidPack(_ connection: SocketConnection, waitCount: Int = 50) -> ValidResult
    {
        var valid = false
        var length = 0
        
        var wait_count = 0
        while (connection.available < VALID_LENGTH && wait_count < waitCount)
        {
            wait_count += 1
            Thread.sleep(forTimeInterval: 0.05)
        }
        
        if connection.available >= VALID_LENGTH,
           let data = connection.read(VALID_LENGTH, timeout: 1)
        {
            let buffer = BinaryBuffer(bytes: data, length: UInt(data.count))
            
            let compare = buffer.data.subdata(in: 0 ..< HEAD_BYTE.count)
            valid = compare.array.elementsEqual(HEAD_BYTE.array)
            if valid
            {
                buffer.seekTo(UInt(HEAD_BYTE.count))
                length = Int(buffer.readInteger() ?? 0)
                print("request data size: \(length)")
            }
            else
            {
                print("pack invalid !!")
            }
        }
        
        return ValidResult(valid: valid, length: length)
    }
    
    static func packData(_ data: Data, container: BinaryBuffer)
    {
        container.writeData(HEAD_BYTE)
        container.writeInteger(Int32(data.count))
        container.writeData(data)
    }
}
