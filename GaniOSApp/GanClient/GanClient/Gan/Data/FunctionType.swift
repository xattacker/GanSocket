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
    case get_account_list = 4
    case check_account = 5
    
    case send_sms = 101
    case receive_sms = 102
    case receive_sms_ack = 103
    
    case get_ip = 901
    case get_system_time = 902
}
