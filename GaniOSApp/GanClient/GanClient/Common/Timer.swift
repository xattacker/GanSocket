//
//  Timer.swift
//  UtilToolKit
//
//  Created by xattacker on 2017/12/9.
//  Copyright © 2017年 xattacker. All rights reserved.
//

import Foundation


extension Timer
{
    public func setupWithRunLoop(_ mode: RunLoop.Mode = .common)
    {
        RunLoop.current.add(self, forMode: mode)
    }
}
