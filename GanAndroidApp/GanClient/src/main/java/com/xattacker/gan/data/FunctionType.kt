package com.xattacker.gan.data


enum class FunctionType(private val _value: Int)
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