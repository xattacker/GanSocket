//
//  ThreadBase.swift
//  GanClient
//
//  Created by xattacker on 2015/11/10.
//  Copyright © 2015年 xattacker. All rights reserved.
//

import Foundation


public protocol ThreadStatusDelegate: class
{
    func onThreadError(thread: ThreadBase, error: Error?)
    func onThreadEnd(thread: ThreadBase)
}


open class ThreadBase
{
    open var isStarted: Bool = false
    open var isTerminated: Bool = true

    open weak var delegate: ThreadStatusDelegate?
    
    public static func currentThreadId() -> String
    {
        let machTID = pthread_mach_thread_np(pthread_self())
        
        return String(format: "%x", machTID)
    }
    
    open func isRunning() -> Bool
    {
        return self.isStarted && !self.isTerminated
    }

    open func sleep(_ seconds: TimeInterval)
    {
        Thread.sleep(forTimeInterval: seconds)
    }

    // abstract function
    open func start() -> Bool
    {
        preconditionFailure("This method must be overridden")
    }
    
    open func run() throws
    {
        preconditionFailure("This method must be overridden")
    }
    
    open func close()
    {
        preconditionFailure("This method must be overridden")
    }
}
