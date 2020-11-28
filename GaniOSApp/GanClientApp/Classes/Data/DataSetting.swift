//
//  AccountSetting.swift
//  GanClientApp
//
//  Created by xattacker on 2019/3/26.
//  Copyright © 2019 xattacker. All rights reserved.
//

import Foundation


extension UserDefaults
{
    enum DataSetting: String, UserDefaultSettable
    {
        case ip
        case port
        case account
        case password
    }
    
    func save(_ process: () -> ()) -> Bool
    {
        process()
        return true //synchronize() // this method is unnecessary and shouldn't be used.
    }
}


internal class DataSetting: NSObject
{
    private(set) static var instance: DataSetting?
    
    var ip: String?
    var port: Int = 0
    var account: String?
    var password: String?

    private override init()
    {
        super.init()

        self.ip = UserDefaults.DataSetting.ip.value("")
        self.port = UserDefaults.DataSetting.port.value(0)
        self.account = UserDefaults.DataSetting.account.value("")
        self.password = UserDefaults.DataSetting.password.value("")
    }

    static func initial()
    {
        if instance == nil
        {
            instance = DataSetting()
        }
    }
    
    static func releaseInstance()
    {
        instance = nil
    }

    @discardableResult
    func save() -> Bool
    {
        return UserDefaults.standard.save {
            UserDefaults.DataSetting.ip.set(self.ip ?? "")
            UserDefaults.DataSetting.port.set(self.port)
            UserDefaults.DataSetting.account.set(self.account ?? "")
            UserDefaults.DataSetting.password.set(self.password ?? "")
        }
    }
}
