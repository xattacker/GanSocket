package com.xattacker.gan.msg;

import com.google.gson.annotations.SerializedName;
import com.xattacker.json.JsonObject;

public final class MsgAck extends JsonObject
{
	@SerializedName("id")
	private String _id;

	public String getId()
	{
		return _id;
	}

	public void setId(String aId)
	{
		_id = aId;
	}
}
