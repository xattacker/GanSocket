package com.xattacker.gan

import com.xattacker.gan.service.AccountService
import com.xattacker.gan.service.MsgService
import com.xattacker.gan.service.SystemService


internal class InnerAccountService(aAgent: GanClient?, aListener: AccountServiceListener?) : AccountService(aAgent, aListener)
internal class InnerMsgService(aAgent: GanClient?) : MsgService(aAgent)
internal class InnerSystemService(aAgent: GanClient?) : SystemService(aAgent)