//
//  PackChecker.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

import Foundation


internal final class PackChecker
{
    internal static let HEAD = "<GAN_PACK>"
    internal static let HEAD_BYTE = HEAD.data(using: String.Encoding.utf8)!

    static func isValidPack(_ data: Data) -> Bool
    {
        var valid = false
        
        if data.count > HEAD_BYTE.count,
           data.array.elementsEqual(HEAD_BYTE.array)
        {
            valid = true
        }
        
        return valid
    }
}
