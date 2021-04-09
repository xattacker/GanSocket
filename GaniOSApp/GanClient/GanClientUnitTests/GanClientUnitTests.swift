//
//  GanClientUnitTests.swift
//  GanClientUnitTests
//
//  Created by xattacker on 2021/4/9.
//

import XCTest
@testable import GanClient
@testable import ObjectMapper


class GanClientUnitTests: XCTestCase
{
    enum AccountStatus: Int
    {
        case none
             
        case logined
        case logouted
    }
    
    private let address = (ip: "192.168.226.46", port: 5999)
    private let account = "test"
    private var accountStatus = AccountStatus.none
    private var ganClient: GanClient?
    private var receivedMsg: MessageData?
    
    override func setUpWithError() throws
    {
        // Put setup code here. This method is called before the invocation of each test method in the class.
        self.ganClient = GanClient(address: address.ip, port: address.port, delegate: self)
        print("call setUpWithError")
    }

    override func tearDownWithError() throws
    {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        
        _ = self.ganClient?.accountService.logout()
        self.ganClient = nil
        
        print("call tearDownWithError")
    }

    func testLogin_out() throws
    {
        let succeed = self.ganClient?.accountService.login(self.account, password: "test123")
        assert(
        succeed == true &&
        self.ganClient?.isConnected == true &&
        self.ganClient?.sessionId != nil,
        "login failed")
        
        wait(1)
        assert(self.accountStatus == .logined, "callback onAccountLoggedIn failed")
        
        
        let result = self.ganClient?.accountService.logout()
        assert(
        result == true &&
        self.ganClient?.isConnected == false &&
        self.ganClient?.sessionId == nil,
        "logout failed")
        
        wait(1)
        assert(self.accountStatus == .logouted, "callback onAccountLoggedOut failed")
    }
    
    func testGetIP() throws
    {
        let ip = self.ganClient?.systemService.getIP()
        assert(ip?.count ?? 0 > 0, "getIP failed")
    }
    
    func testGetTime() throws
    {
        let time = self.ganClient?.systemService.getSystemTime()
        assert(time != nil, "getSystemTime failed")
    }
    
    func testSendMsg() throws
    {
        let msg = "aaafdsafad中文字 " + String(DateTimeUtility.getTimeStamp())
        let succeed = self.ganClient?.messageService.sendMessage(self.account, message: msg)
        assert(succeed == true, "sendMessage failed")

        let result = self.ganClient?.accountService.login(self.account, password: "test123")
        assert(
        result == true &&
        self.ganClient?.isConnected == true &&
        self.ganClient?.sessionId != nil,
        "login failed")
              
        
        wait(1.5)
        assert(self.receivedMsg?.message == msg, "receive Message failed")
        
        self.ganClient?.accountService.logout()
    }
}


extension GanClientUnitTests: GanClientDelegate
{
    func onAccountLoggedIn(account: String)
    {
        assert(self.account == account, "callback onAccountLoggedIn failed, account is not the same")
        self.accountStatus = .logined
    }
    
    func onAccountLoggedOut(account: String)
    {
        assert(self.account == account, "callback onAccountLoggedOut failed, account is not the same")
        self.accountStatus = .logouted
    }
    
    func onMessageReceived(message: MessageData)
    {
        print("onMessageReceived: \(message.message ?? "")")
        self.receivedMsg = message
    }
}


extension XCTestCase
{
    func wait(_ seconds: TimeInterval)
    {
        let exp = expectation(description: "Test wait after \(seconds) seconds")
        _ = XCTWaiter.wait(for: [exp], timeout: seconds)
    }
}
