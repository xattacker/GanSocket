package com.xattacker.gan

import com.xattacker.gan.service.AccountService
import com.xattacker.gan.service.MsgService
import com.xattacker.gan.service.SystemService


//create all service class that only instantiate in the package
internal class ServiceFactory private constructor()

internal class InnerAccountService(aAgent: GanClient?, aListener: AccountServiceListener?) :
    AccountService(aAgent, aListener)

internal class InnerSmsService(aAgent: GanClient?) : MsgService(aAgent)
internal class InnerSystemService(aAgent: GanClient?) : SystemService(aAgent)