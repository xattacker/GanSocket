package com.xattacker.util;

import java.lang.management.ManagementFactory;

public final class UtilToolkit
{
	private UtilToolkit()
	{
	}

	public static boolean isDebug()
	{
		return ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}
}
