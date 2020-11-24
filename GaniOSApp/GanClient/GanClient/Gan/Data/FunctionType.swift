//
//  FunctionType.swift
//  GanClient
//
//  Created by xattacker on 2020/11/23.
//

internal enum FunctionType: Int
{
    case unknown = -1
    
    case login = 1
    case logout = 2
    case register_account = 3
    case connection = 4
    
    case send_sms = 5
    case receive_sms = 6
    
    case get_ip = 7
    case get_system_time = 8
    
    case get_account_list = 9
    case check_account = 10
}
