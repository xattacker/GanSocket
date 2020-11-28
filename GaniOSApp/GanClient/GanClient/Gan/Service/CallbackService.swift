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
        self.task = CallbackReceivingTask(connection: connection, callback: self.delegate)
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
    private weak var callback: CallbackServiceDelegate?
    
    init(connection: SocketConnection, callback: CallbackServiceDelegate)
    {
        self.connection = connection
        self.callback = callback
    }
    
    override func run() throws
    {
        repeat
        {
            self.sleep(0.3)
            
            guard (self.connection?.available() ?? 0) >= PackChecker.headerLength(),
                  let data = self.connection?.read(PackChecker.headerLength(), timeout: 1) else
            {
                continue
            }
            
            
            var buffer = BinaryBuffer(bytes: data, length: UInt(data.count))
            let valid = PackChecker.isValidPack(buffer)
            if valid.valid && valid.length > 0
            {
                guard let data2 = self.connection?.read(valid.length, timeout: 3) else
                {
                    print("read timeout, available: \(self.connection?.available() ?? 0)")
                    continue
                }
                
                
                buffer = BinaryBuffer(bytes: data2, length: UInt(data2.count))
                let response = ResponsePack()
                if response.fromBinaryReadable(buffer)
                {
                    switch FunctionType.init(rawValue: response.id)
                    {
                        case .receive_sms:
                            if let json = response.responseString,
                               let msg = MessageData(JSONString: json)
                            {
                                self.callback?.onMessageReceived(message: msg)
                            }
                            break
                            
                        case .logout: // force logout
                            if let account = response.responseString
                            {
                                self.callback?.onLoggedOut(account: account)
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
