package com.xattacker.gan.data


enum class FunctionType(private val _value: Int)
{
    UNKNOWN(-1),

    LOGIN(1),
    LOGOUT(2),
    REGISTER_ACCOUNT(3),
    CONNECTION(4),

    SEND_SMS(5),
    RECEIVE_SMS(6),

    GET_IP(7),
    GET_SYSTEM_TIME(8),

    GET_ACCOUNT_LIST(9),
    CHECK_ACCOUNT(10);

    fun value(): Int
    {
        return _value
    }

    companion object
    {
        fun parse(aValue: Int): FunctionType
        {
            for (type in values())
            {
                if (type.value() == aValue)
                {
                    return type
                }
            }

            return UNKNOWN
        }
    }
}