package com.xattacker.gan.data;

public enum FunctionType
{
	UNKNOWN_TYPE(-1),
	
	LOGIN(1),
	LOGOUT(2),
	REGISTER_ACCOUNT(3),
	CONNECTION(4),
	SEND_SMS(5),
	RECEIVE_SMS(6),
	GET_IP(7),
	GET_SYSTEM_TIME(8),
	GET_FRIEND_LIST(9),
	ADD_FRIEND(10),
	CHECK_ACCOUNT(11);
	
	
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

		return UNKNOWN_TYPE;
	}
}
