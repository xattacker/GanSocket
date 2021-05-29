//
//  CallbackService.swift
//  GanClient
//
//  Created by xattacker on 2020/11/25.
//

import Foundation


internal protocol CallbackServiceDelegate: class
{
    func onLoggedOut(account: String)
    func onMessageReceived(message: MessageData)
}


internal final class CallbackService: ServiceFoundation
{
    private let delegate: CallbackServiceDelegate
    private var task: CallbackReceivingTask?
    
    internal init(agent: GanAgent, delegate: CallbackServiceDelegate)
    {
        self.delegate = delegate
        
        super.init(agent: agent)
    }
    
    internal func handleConnection(_ connection: SocketConnection)
    {
        self.task = CallbackReceivingTask(connection: connection, callbackDelegate: self.delegate)
        self.task?.start()
    }
    
    internal func disconnect()
    {
        self.task?.close()
        self.task = nil
    }
    
    deinit
    {
        self.disconnect()
    }
}


internal final class CallbackReceivingTask: ImpThread
{
    private var connection: SocketConnection?
    private weak var callbackDelegate: CallbackServiceDelegate?

    init(connection: SocketConnection, callbackDelegate: CallbackServiceDelegate)
    {
        self.connection = connection
        self.callbackDelegate = callbackDelegate
    }
    
    override func run() throws
    {
        repeat
        {
            self.sleep(0.3)
            
            guard let connection = self.connection else
            {
                continue
            }
            
            let valid = PackChecker.isValidPack(connection)
            if valid.valid && valid.length > 0
            {
                guard let data = connection.read(valid.length, timeout: 3) else
                {
                    print("read timeout, available: \(connection.available)")
                    continue
                }
                
                
                let buffer = BinaryBuffer(bytes: data, length: UInt(data.count))
                let response = ResponsePack()
                if response.fromBinaryReadable(buffer)
                {
                    switch FunctionType.init(rawValue: response.id)
                    {
                        case .receive_sms:
                            guard let json = response.responseString,
                                  let msg = MessageData(JSONString: json) else
                            {
                                break
                            }
                            
                            // send Ack response, but server side could not receive it ??!!
                            let ack = MessageAck()
                            ack.id = msg.id
                            
                            guard let ack_data = ack.toJSONString()?.data(using: String.Encoding.utf8) else
                            {
                                break
                            }
                            
                            let buffer_ack = BinaryBuffer()
                            PackChecker.packData(ack_data, container: buffer_ack)
                            
                            switch connection.send(data: buffer_ack.data)
                            {
                                case .success(()):
//                                    Thread.sleep(forTimeInterval: 0.5)
//
//                                    let valid = PackChecker.isValidPack(connection)
//                                    guard valid.valid && valid.length > 0 else
//                                    {
//                                        continue
//                                    }
//
//                                    guard let data2 = connection.read(valid.length, timeout: 5) else
//                                    {
//                                        continue
//                                    }
//
//                                    let buffer2 = BinaryBuffer(bytes: data2, length: UInt(data2.count))
//                                    let response2 = ResponsePack()
//                                    if response2.fromBinaryReadable(buffer2), response2.result
//                                    {
//                                        self.callbackDelegate?.onMessageReceived(message: msg)
//                                    }
                                    
                                    self.callbackDelegate?.onMessageReceived(message: msg)
                                    Thread.sleep(forTimeInterval: 0.2)
                                    
                                case .failure(let error):
                                    print(error)
                                    continue
                            }
                            break
                            
                        case .logout: // force logout
                            if let account = response.responseString
                            {
                                self.callbackDelegate?.onLoggedOut(account: account)
                            }
                            
                            self.connection?.close()
                            self.connection = nil
                            break

                        default:
                            print("unknown response id: \(response.id)")
                            break
                    }
                }
            }
            
        } while self.connection != nil && !self.isTerminated
    }
    
    deinit
    {
        self.connection?.close()
        self.connection = nil
        
        self.delegate = nil
    }
}
