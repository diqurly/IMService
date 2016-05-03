package org.diqurly.packet;

import org.json.JSONException;
import org.json.JSONObject;

public class Message extends Packet {

	public Message() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Message(JSONObject jsonObject) throws JSONException {
		super(jsonObject);
		
	}
	private String msg;
	

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	
	public String toJson() {
		if(jsonObject!=null)
		{
			return jsonObject.toString();
		}else
		{
			return "{" 
					+ "\"id\":\"" + getId()+"\","
					+ "\"type\":\"" + getType() +"\","
					+ "\"to\":\"" + getTo()+"\"," 
					+ "\"from\":\"" + getFrom()+"\"," 
					+ "\"msg\":\"" + getMsg()+"\"," 
					+ "\"time\":\"" + getTime() +"\" "
					+ "}";
		}	
	
	}
	
}
