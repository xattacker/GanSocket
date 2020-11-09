package com.xattacker.gan;

import com.xattacker.gan.service.AccountService;
import com.xattacker.gan.service.MsgService;
import com.xattacker.gan.service.SystemService;

//create all service class that only instantiate in the package
final class ServiceFactory
{
	private ServiceFactory()
	{
	}
}

final class InnerAccountService extends AccountService
{
	InnerAccountService(GanClient aAgent, AccountServiceListener aListener)
	{
		super(aAgent, aListener);
	}
}

final class InnerSmsService extends MsgService
{
	InnerSmsService(GanClient aAgent)
	{
		super(aAgent);
	}
}

final class InnerSystemService extends SystemService
{
	InnerSystemService(GanClient aAgent)
	{
		super(aAgent);
	}
}