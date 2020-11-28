//
//  MimeCodec.swift
//  GanClient
//
//  Created by xattacker on 2016/2/12.
//  Copyright © 2016年 xattacker. All rights reserved.
//

import Foundation


public enum Base64Format
{
    case standard
    case urlSafe
}


public final class MimeCodec
{
    private static let BUFFER_UNIT = 1024

    public class func encode(_ source: Data, format: Base64Format = .standard) -> String
    {
        var mime = source.base64EncodedString()
        if format != .standard
        {
            mime = mime.switchBase64(format)
        }
        
        return mime
    }
    
    public class func encode(_ source: String, format: Base64Format = .standard) -> String?
    {
        var str: String?
        
        if let data = source.data(using: String.Encoding.utf8)
        {
            str = self.encode(data, format: format)
        }
        
        return str
    }
    
    public class func encode(_ source: InputStream, output: OutputStream, format: Base64Format = .standard)
    {
        var index = -1
        let length = 3 * BUFFER_UNIT // must be a multiple of 3
        let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: length)
        
        if source.streamStatus == Stream.Status.notOpen
        {
            source.open()
        }
        
        if output.streamStatus == Stream.Status.notOpen
        {
            output.open()
        }
        
        repeat
        {
            index = source.read(buffer, maxLength: length)
            if index > 0
            {
                autoreleasepool
                {
                    let data = Data(bytes: UnsafePointer<UInt8>(buffer), count: index)
                    let mime = self.encode(data, format: format)
                    
                    if let data2 = mime.data(using: String.Encoding.utf8)
                    {
                        let bytes = UnsafeMutablePointer<UInt8>.allocate(capacity: data2.count)
                        data2.copyBytes(to: bytes, count: data2.count)
                        
                        output.write(bytes, maxLength: data2.count)
                        
                        // remember to release it
                        bytes.deallocate()
                    }
                }
            }
            
        } while index > 0
        
        // remember to release it
        buffer.deallocate()
    }
    
    public class func decode(_ mime: String, format: Base64Format = .standard) -> Data?
    {
        var temp = format == .standard ? mime : mime.switchBase64(.standard)
        var options = Data.Base64DecodingOptions(rawValue: 0)
        
        let remainder = temp.count % 4
        if remainder > 0
        {
            temp = temp.padding(toLength: temp.count + 4 - remainder,
                                withPad: "=",
                                startingAt: 0)
            
            options = Data.Base64DecodingOptions.ignoreUnknownCharacters
        }
        
        return Data(base64Encoded: temp, options: options)
    }
    
    public class func decodeToString(_ mime: String, format: Base64Format = .standard) -> String?
    {
        var str: String?
        
        if let data = self.decode(mime, format: format)
        {
            str = String(data: data, encoding: String.Encoding.utf8)
        }
        
        return str
    }
    
    public class func decode(_ source: InputStream, output: OutputStream, format: Base64Format = .standard)
    {
        var index = -1
        let length = 4 * BUFFER_UNIT // must be a multiple of 4
        let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: length)
        
        if source.streamStatus == Stream.Status.notOpen
        {
            source.open()
        }
        
        if output.streamStatus == Stream.Status.notOpen
        {
            output.open()
        }
        
        repeat
        {
            index = source.read(buffer, maxLength: length)
            if index > 0
            {
                autoreleasepool
                {
                    let mime = String(cString: buffer)
                    
                    if let data = self.decode(mime, format: format)
                    {
                        let bytes = UnsafeMutablePointer<UInt8>.allocate(capacity: data.count)
                        data.copyBytes(to: bytes, count: data.count)
                        
                        output.write(bytes, maxLength: data.count)
                        
                        // remember to release it
                        bytes.deallocate()
                    }
                }
            }
            
        } while index > 0
        
        // remember to release it
        buffer.deallocate()
    }
}


public final class Base64FormatSwitcher
{
    public class func switchBase64(_ base64: String, format: Base64Format) -> String
    {
        var temp = base64
        
        switch format
        {
            case .standard:
                temp = temp.replacingOccurrences(of: "-", with: "+")
                           .replacingOccurrences(of: "_", with: "/")
                
                if temp.count % 4 != 0
                {
                    temp.append(String(repeating: "=", count: 4 - base64.count % 4))
                }
                break
                
            case .urlSafe:
                temp = temp.replacingOccurrences(of: "+", with: "-")
                           .replacingOccurrences(of: "/", with: "_")
                           .replacingOccurrences(of: "=", with: "")
                break
        }

        return temp
    }
}


extension String
{
    public func switchBase64(_ format: Base64Format) -> String
    {
        return Base64FormatSwitcher.switchBase64(self, format: format)
    }
}
