//
//  UserDefaultSettable.swift
//  GanClientApp
//
//  Created by xattacker on 2019/3/26.
//  Copyright © 2019 xattacker. All rights reserved.
//

import Foundation


protocol UserDefaultSettable
{
    var uniqueKey: String { get }
}


extension UserDefaultSettable where Self: RawRepresentable, Self.RawValue == String
{
    var uniqueKey: String
    {
        return "\(Self.self).\(rawValue)"
    }

    func set(_ value: Any?)
    {
        UserDefaults.standard.set(value, forKey: uniqueKey)
    }

    func value<T: Any>(_ defaultValue: T) -> T
    {
        return UserDefaults.standard.value(forKey: uniqueKey) as? T ?? defaultValue
    }
}
