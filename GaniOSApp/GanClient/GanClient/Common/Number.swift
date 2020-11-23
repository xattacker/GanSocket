//
//  Number.swift
//  UtilToolKit_Swift
//
//  Created by tao on 2020/7/21.
//  Copyright © 2020 xattacker. All rights reserved.
//

import Foundation


infix operator ^^

public func ^^ (left: Int, right: Int) -> Int
{
    return Int(pow(Double(left), Double(right)))
}
