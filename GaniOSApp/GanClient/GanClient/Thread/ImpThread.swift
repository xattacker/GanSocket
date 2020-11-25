//
//  ImpThread.swift
//  GanClient
//
//  Created by xattacker on 2015/11/10.
//  Copyright © 2015年 xattacker. All rights reserved.
//

import Foundation


open class ImpThread: ThreadBase
{
    private var thread: Thread?
    
    open func start(_ priority: Double) -> Bool
    {
        let result = self.start()
        
        if result
        {
            self.thread?.threadPriority = priority
        }
        
        return result
    }
    
    // override from ThreadPrototype
    @discardableResult
    open override func start() -> Bool
    {
        var result = false
        
        self.thread = Thread(target: self, selector: #selector(execRun), object: nil)
        self.thread?.start()
        result = true
        
        return result
    }
    
    open override func close()
    {
        self.isTerminated = true
        
        self.thread?.cancel()
        self.thread = nil
    }
    
    // selector function could not be private
    @objc func execRun()
    {
        // If you create a thread that you need to create a auto release pool instance
        autoreleasepool
        {
            self.isStarted = true
            self.isTerminated = false
            
            do
            {
                // template method
                try self.run()
            }
            catch let error as Error?
            {
                self.delegate?.onThreadError(thread: self, error: error)
            }
            

            self.isStarted = false
            self.isTerminated = true
            
            self.thread = nil

            self.delegate?.onThreadEnd(thread: self)
        }
    }
    
    deinit
    {
        self.thread = nil
    }
}
