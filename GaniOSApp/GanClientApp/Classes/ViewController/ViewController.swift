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
        
        DataSetting.initial()
        
        if let ip = DataSetting.instance?.ip,
           let port = DataSetting.instance?.port
        {
            self.ipTextField.text = ip
            self.portTextField.text = port.toString()
        }
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
        
        
        _ = self.ganClient?.accountService.logout()
        self.ganClient = nil
        
        self.ganClient = GanClient(address: ip, port: port, delegate: self)
        let succeed = self.ganClient?.accountService.login("test", password: "test")
        print("login: \(succeed)")
        
        if succeed == true
        {
            DataSetting.instance?.ip = ip
            DataSetting.instance?.port = port
            DataSetting.instance?.save()
        }
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
    
    @IBAction func onSendMsgAction(_ sender: AnyObject)
    {
        guard let client = self.ganClient else
        {
            print("GanClient is not initial !!")
            
            return
        }
        
        
        let msg = "aaafdsafad中文字 " + DateTimeUtility.getTimeStamp().toString()
        if client.messageService.sendMessage("test", message: msg)
        {
            print("sendMessage succeed: \(msg)")
        }
        else
        {
            print("sendMessage failed")
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
    
    func onMessageReceived(message: MessageData)
    {
        print("onMessageReceived: \(message.message ?? "")")
    }
}
