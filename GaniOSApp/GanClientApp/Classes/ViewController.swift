//
//  HomeMenuViewController.swift
//  GanClientApp
//
//  Created by xattacker on 2015/6/22.
//  Copyright (c) 2015年 xattacker. All rights reserved.
//

import UIKit
import RxSwift
import RxCocoa
import GanClient


class ViewController: BaseViewController
{
    @IBOutlet private weak var ipTextField: UITextField!
    @IBOutlet private weak var portTextField: UITextField!
    
    @IBOutlet private weak var msgTextView: UITextView!
    
    private var ganClient: GanClient?
    
    private let disposeBag = DisposeBag()
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
    }

    override func viewDidAppear(_ animated: Bool)
    {
        super.viewDidAppear(animated)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?)
    {
    }

    @IBAction func onLoginAction(_ sender: AnyObject)
    {
        guard let ip = self.ipTextField.text,
              let port = self.portTextField.text?.intValue() else
        {
            print("ip port error !!")
            
            return
        }
        
        
        self.ganClient?.accountService.logout()
        self.ganClient = nil
        
        self.ganClient = GanClient(address: ip, port: port, delegate: self)
        let succeed = self.ganClient?.accountService.login("test", password: "test")
        print("login: \(succeed)")
    }
    
    @IBAction func onLogoutAction(_ sender: AnyObject)
    {
        guard let client = self.ganClient else
        {
            print("GanClient is not initial !!")
            
            return
        }
        
        
        _ = client.accountService.logout()
    }
    
    @IBAction func onGetIPAction(_ sender: AnyObject)
    {
        guard let client = self.ganClient else
        {
            print("GanClient is not initial !!")
            
            return
        }
        
        
        if let ip = client.systemService.getIP()
        {
            print("got ip: " + ip)
        }
        else
        {
            print("got ip failed")
        }
    }
    
    @IBAction func onGetTimeAction(_ sender: AnyObject)
    {
        guard let client = self.ganClient else
        {
            print("GanClient is not initial !!")
            
            return
        }
        
        
        if let time = client.systemService.getSystemTime()
        {
            print("got time: " + time.getDateTimeString(DateTimeFormatType.datetime_complete))
        }
        else
        {
            print("got time failed")
        }
    }
    
    deinit
    {
        _ = self.ganClient?.accountService.logout()
        self.ganClient = nil
    }
}


extension ViewController: GanClientDelegate
{
    func onAccountLoggedIn(account: String)
    {
        self.title = "\(account)(loggedIn)"
    }
    
    func onAccountLoggedOut(account: String)
    {
        self.title = "(loggedOut)"
    }
    
    func onMessageReceived(sender: String, time: UInt64, msg: String)
    {
        print("onMessageReceived: \(msg)")
    }
}
