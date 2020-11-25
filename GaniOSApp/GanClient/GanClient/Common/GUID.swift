//
//  GUID.swift
//  GanClient
//
//  Created by xattacker on 2015/11/25.
//  Copyright © 2015年 xattacker. All rights reserved.
//

import Foundation


public final class GUID
{
    public class func generateGUID() -> String
    {
        return UUID().uuidString
    }
}
