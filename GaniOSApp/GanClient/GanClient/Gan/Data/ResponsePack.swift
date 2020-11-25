//
//  ResponsePack.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation


internal class ResponsePack: BinarySerializable2
{
    var result: Bool = false
    var id: Int = 0
    var response: Data?
    
    var responseString: String?
    {
        if let data = self.response
        {
            return String(data: data, encoding: .utf8)
        }
        
        return nil
    }
    
    func toBinaryWriteable<T>(_ writable: T) where T : BinaryWritable
    {
        _ = writable.writeShort(self.result ? 1 : 0).writeInteger(Int32(self.id))
        
        if let response = self.response, response.count > 0
        {
            _ = writable.writeInteger(Int32(response.count)).writeData(response)
        }
        else
        {
            _ = writable.writeInteger(0)
        }
    }
    
    func fromBinaryReadable(_ readable: BinaryReadable) -> Bool
    {
        var succeed = false
        
        self.result = readable.readShort() == 1
        self.id = Int(readable.readInteger() ?? 0)
        
        if let count = readable.readInteger(), count > 0,
           let bytes = readable.readBinary(UInt(count))
        {
            self.response = Data(bytes: bytes, count: Int(count))
            
            // remember to release it
            bytes.deallocate()
        }
        else
        {
            self.response = nil
        }
        
        succeed = true
        
        return succeed
    }
    
    deinit
    {
        self.response = nil
    }
}
