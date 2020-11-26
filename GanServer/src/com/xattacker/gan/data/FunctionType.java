package com.xattacker.gan.data;

public enum FunctionType
{
	UNKNOWN(-1),
	
	LOGIN(1),
	LOGOUT(2),
	REGISTER_ACCOUNT(3),
	GET_ACCOUNT_LIST(4),
	CHECK_ACCOUNT(5),
	
	SEND_SMS(101),
	RECEIVE_SMS(102),
	
	GET_IP(901),
	GET_SYSTEM_TIME(902);
	
	
   private int _value;
   
   private FunctionType(int value)
   {
   	_value = value;
   }
   
   public int value()
   {
  	 	return _value;
   }
   
	public static FunctionType parse(int aValue)
	{
		for (FunctionType type : FunctionType.values())
		{
			if (type.value() == aValue)
			{
				return type;
			}
		}

		return UNKNOWN;
	}
}
