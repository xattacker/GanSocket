//
//  CallbackService.swift
//  GanClient
//
//  Created by xattacker on 2020/11/25.
//

import Foundation


internal protocol CallbackServiceDelegate
{
    func onMessageReceived(sender: String, time: UInt64, msg: String)
}


internal final class CallbackService: ServiceFoundation
{
    private let delegate: CallbackService
    private var task: CallbackReceivingTask?
    
    internal init(agent: GanAgent, delegate: CallbackService)
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
                        self.task = CallbackReceivingTask(client: client)
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
    
    init(client: TCPClient)
    {
        self.client = client
    }
    
    override func run() throws
    {
        repeat
        {
            self.sleep(0.3)
            
            if let data = self.client?.read(1024*10, timeout: 2)
            {
                let buffer = BinaryBuffer(bytes: data, length: UInt(data.count))
                if PackChecker.isValidPack(buffer.data)
                {
                    buffer.seekTo(UInt(PackChecker.HEAD_BYTE.count))
                    
                    let response = ResponsePack()
                    if response.fromBinaryReadable(buffer)
                    {
//                        switch response.id
//                        {
//                            case <#pattern#>:
//                                break
//                                
//                            default:
//                                print("unknown response id: \(response.id)")
//                                break
//                        }
                    }
                }
            }
            
        } while self.client != nil && !self.isTerminated
    }
    
    deinit
    {
        self.client?.close()
        self.client = nil
    }
}
