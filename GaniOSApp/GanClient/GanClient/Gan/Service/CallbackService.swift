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
    
    internal func connect() -> Bool
    {
        var succeed = false
        
        switch self.createSocket()
        {
            case .success(let client):
                let request_data = self.createRequestPack(FunctionType.create_callback_connection)
                
                switch client.send(data: request_data)
                {
                    case .success(()):
                        self.task = CallbackReceivingTask(client: client, callback: self.delegate)
                        self.task?.start()
                        
                        succeed = true
                        break
                        
                    case .failure(let error):
                        print(error)
                        break
                }
                break
                
            case .failure(let error):
                print(error)
                break
        }
        
        return succeed
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
    private var client: TCPClient?
    private weak var callback: CallbackServiceDelegate?
    
    init(client: TCPClient, callback: CallbackServiceDelegate)
    {
        self.client = client
        self.callback = callback
    }
    
    override func run() throws
    {
        repeat
        {
            self.sleep(0.3)
            
            guard let data = self.client?.read(1024*10, timeout: 2) else
            {
                continue
            }
            
            
            let buffer = BinaryBuffer(bytes: data, length: UInt(data.count))
            if PackChecker.isValidPack(buffer.data)
            {
                buffer.seekTo(UInt(PackChecker.HEAD_BYTE.count))
                
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
                            
                            self.client?.close()
                            self.client = nil
                            break

                        default:
                            print("unknown response id: \(response.id)")
                            break
                    }
                }
            }
            
        } while self.client != nil && !self.isTerminated
    }
    
    deinit
    {
        self.client?.close()
        self.client = nil
        
        self.delegate = nil
    }
}
