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

    
    func toBinaryWriteable<T>(_ writable: T) where T : BinaryWritable
    {
        
    }
    
    func fromBinaryReadable(_ readable: BinaryReadable) -> Bool
    {
        return true
    }
    
    deinit
    {
        self.response = nil
    }
}
