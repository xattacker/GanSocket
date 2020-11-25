//
//  WeakDelegateBox.swift
//  GanClient
//
//  Created by xattacker on 2015/11/27.
//  Copyright © 2015年 xattacker. All rights reserved.
//

import Foundation


public final class WeakDelegateBox <T: AnyObject>
{
    public weak var delegate: T?
    
    public func isEqual(_ delegate: T?) -> Bool
    {
        return self.delegate === delegate
    }
    
    deinit
    {
        self.delegate = nil
    }
}


public final class WeakDelegateList <T: AnyObject>
{
    private var delegates: Array<WeakDelegateBox<T>>!
    
    public init()
    {
        self.delegates = Array<WeakDelegateBox<T>>()
    }
    
    public var isEmpty: Bool
    {
        return self.delegates.isEmpty
    }
        
    public var count: Int
    {
        return self.delegates.count
    }
    
    public func clear()
    {
        self.delegates.removeAll()
    }
    
    public subscript(index: Int) -> T?
    {
        return self.delegates[index].delegate
    }
    
    public func addDelegate(_ delegate: T)
    {
        let found = self.delegates.first(where:
                        {
                            (box) -> Bool in
                            return box.isEqual(delegate)
                        })
        
        if found == nil
        {
            let box = WeakDelegateBox<T>()
            box.delegate = delegate
            self.delegates.append(box)
        }
    }
    
    public func removeDelegate(_ delegate: T)
    {
        if let found = self.delegates.firstIndex(where:
                        {
                            (box) -> Bool in
                            return box.isEqual(delegate)
                        })
        {
            self.delegates.remove(at: found)
        }
    }
    
    public func fetch(_ fetch: (_ delegate: T) -> Void)
    {
        for var i in 0 ..< self.delegates.count
        {
            let box = self.delegates[i]
            if let delegate = box.delegate
            {
                fetch(delegate)
            }
            else
            {
                self.delegates.remove(at: i)
                i -= 1
            }
        }
    }
    
    deinit
    {
        self.delegates.removeAll()
        self.delegates = nil
    }
}
