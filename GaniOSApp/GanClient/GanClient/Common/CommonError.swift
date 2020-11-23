//
//  CommonError.swift
//  UtilToolKit
//
//  Created by xattacker on 2017/1/17.
//  Copyright © 2017年 xattacker. All rights reserved.
//

import Foundation


public enum CommonError: Error
{
    case error(String)
    
    public var description: String
    {
        return "Error: (\(self.details))"
    }
    
    public var details: String
    {
        switch self
        {
            case .error(let details): return details
        }
    }
}
