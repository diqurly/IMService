package org.diqurly.packet;

import org.json.JSONException;
import org.json.JSONObject;

public class Ping extends Packet {
	public Ping(JSONObject jsonObject) throws JSONException {
		super(jsonObject);
		// TODO Auto-generated constructor stub
		if(!"D-PING".equals(jsonObject.getString("msg")))
		{
			throw new JSONException("ping format error!");
		}
	}
	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		if(jsonObject!=null)
		{
			return jsonObject.toString();
		}else
		{
			return "{" 
					+ "\"id\":\"" + getId()+"\"," 
					+ "\"type\":\"" + getType() +"\","
					+ "\"msg\":\"D-PING\","
					+ "\"time\":\"" + getTime() +"\" "
					+ "}";
		}
	}
	
}
