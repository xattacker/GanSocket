//
//  DateTimeUtility.swift
//  UtilToolKit
//
//  Created by xattacker on 2015/7/12.
//  Copyright (c) 2015年 xattacker. All rights reserved.
//

import Foundation


public enum DateTimeFormatType: String, CaseIterable
{
    case year = "yyyy"
    case month_simple = "yyyyMM"
    case month_date_simple = "MMdd"
    case date_simple = "yyyyMMdd"
    case time_simple = "HHmmss"
    case datetime_simple = "yyyyMMddHHmmss"
    case month_complete = "yyyy-MM"
    case month_date_complete = "MM-dd"
    case date_complete = "yyyy-MM-dd"
    case time_complete = "HH:mm:ss"
    case datetime_complete = "yyyy-MM-dd HH:mm:ss"
    case datetime_complete_without_second = "yyyy-MM-dd HH:mm"
}


public final class DateTimeUtility
{
    public static func getDateTimeString(_ type: DateTimeFormatType, date: Date = Date(), timeZone: TimeZone? = nil) -> String
    {
        return self.getDateTimeString(type.rawValue, date: date, timeZone: timeZone)
    }
    
    public static func getDateTimeString(_ format: String, date: Date = Date(), timeZone: TimeZone? = nil) -> String
    {
        let formatter = DateFormatter()
        formatter.dateFormat = format
        
        let locale = Locale(identifier: "en_US")
        formatter.locale = locale
        
        if let timeZone = timeZone
        {
            formatter.timeZone = timeZone
        }
        
        
        return formatter.string(from: date)
    }
    
    public static func parseDate(_ str: String, type: DateTimeFormatType, timeZone: TimeZone? = nil) -> Date?
    {
        return self.parseDate(str, format: type.rawValue, timeZone: timeZone)
    }
    
    public static func parseDate(_ str: String, format: String, timeZone: TimeZone? = nil) -> Date?
    {
        let formatter = DateFormatter()
        formatter.dateFormat = format
        
        let locale = Locale(identifier: "en_US")
        formatter.locale = locale
        
        if let timeZone = timeZone
        {
            formatter.timeZone = timeZone
        }
        
        return formatter.date(from: str)
    }
    
    public static func getTimeStamp(_ date: Date = Date(), millsecond: Bool = true) -> UInt64
    {
        let time = date.timeIntervalSince1970
        return millsecond ? UInt64(time * 1000) : UInt64(time)
    }
}


extension Date
{
    public func getDateTimeString(_ type: DateTimeFormatType, timeZone: TimeZone? = nil) -> String
    {
        return DateTimeUtility.getDateTimeString(type.rawValue, date: self, timeZone: timeZone)
    }
    
    public func getDateTimeString(_ format: String, timeZone: TimeZone? = nil) -> String
    {
        return DateTimeUtility.getDateTimeString(format, date: self, timeZone: timeZone)
    }
    
    public var timestamp: UInt64
    {
        return DateTimeUtility.getTimeStamp(self)
    }
    
    public func isSameYear(_ other: Date) -> Bool
    {
        return Calendar.current.isDate(self, equalTo: other, toGranularity: .year)
    }
        
    public func isSameMonth(_ other: Date) -> Bool
    {
        return Calendar.current.isDate(self, equalTo: other, toGranularity: .month)
    }
    
    public func isSameDay(_ other: Date) -> Bool
    {
        return Calendar.current.isDate(self, inSameDayAs: other)
    }
            
    public func isSameHour(_ other: Date) -> Bool
    {
        return Calendar.current.isDate(self, equalTo: other, toGranularity: .hour)
    }
    
    public var isToday: Bool
    {
        return Calendar.current.isDateInToday(self)
    }
    
    public static func -(lhs: Date, rhs: Date) -> UInt64
    {
        return lhs.timestamp - rhs.timestamp
    }
    
    public static func +(lhs: Date, rhs: Date) -> UInt64
    {
        return lhs.timestamp + rhs.timestamp
    }
}
