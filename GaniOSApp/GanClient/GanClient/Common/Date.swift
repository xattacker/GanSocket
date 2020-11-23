//
//  Date.swift
//  UtilToolKit
//
//  Created by xattacker on 2018/8/30.
//  Copyright © 2018年 xattacker. All rights reserved.
//

import Foundation


public enum WeekDay: Int, CaseIterable
{
    case sun = 1
    case mon = 2
    case tue = 3
    case wed = 4
    case thu = 5
    case fri = 6
    case sat = 7
}


extension Date
{
    public func dateByTrimSeconds() -> Date
    {
        let calendar = Calendar.current
        let seconds = calendar.component(.second, from: self)
        
        return self.date(withSecond: -seconds)
    }
    
    public func startDateOfMonth() -> Date
    {
        return Calendar.current.date(
               from: Calendar.current.dateComponents([.year, .month],
               from: Calendar.current.startOfDay(for: self))) ?? self
    }
    
    public func endDateOfMonth() -> Date
    {
        return Calendar.current.date(byAdding: DateComponents(month: 1, day: -1), to: self.startDateOfMonth()) ?? self
    }
    
    public func date(withSecond: Int) -> Date
    {
        return Calendar.current.date(byAdding: Calendar.Component.second, value: withSecond, to: self) ?? self
    }
    
    public func date(withMinute: Int) -> Date
    {
        return Calendar.current.date(byAdding: Calendar.Component.minute, value: withMinute, to: self) ?? self
    }
    
    public func date(withHour: Int) -> Date
    {
        return Calendar.current.date(byAdding: Calendar.Component.hour, value: withHour, to: self) ?? self
    }
    
    public func date(withDay: Int) -> Date
    {
        return Calendar.current.date(byAdding: Calendar.Component.day, value: withDay, to: self) ?? self
    }
    
    public func date(withMonth: Int) -> Date
    {
        return Calendar.current.date(byAdding: Calendar.Component.month, value: withMonth, to: self) ?? self
    }
    
    public func date(withYear: Int) -> Date
    {
        return Calendar.current.date(byAdding: Calendar.Component.year, value: withYear, to: self) ?? self
    }
    
    // returns an integer from 1 - 7, with 1 being Sunday and 7 being Saturday
    public var weekDay: WeekDay?
    {
        get
        {
            if let day = Calendar.current.dateComponents([.weekday], from: self).weekday
            {
                return WeekDay(rawValue: day)
            }
            
            return nil
        }
    }
}
