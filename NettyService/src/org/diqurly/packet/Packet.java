package org.diqurly.packet;

import org.json.JSONException;
import org.json.JSONObject;

public class Packet {
	// 消息ID
	protected String id;
	// 消息类型
	protected String type;
	/**
	 * 可以为空，为空时默认系统处理
	 * 有值时转到对应的组件进行相应处理
	 */
	protected String component;
	// 时间
	protected long time;	
	
	private String to;

	private String from;
	
	protected JSONObject jsonObject;	
	public Packet() {
		// this.time = System.currentTimeMillis();
		this.time = System.currentTimeMillis();
	}

	public Packet(JSONObject jsonObject) throws JSONException {		
		if (jsonObject != null) {
			this.jsonObject =jsonObject;
			this.type = jsonObject.getString("type");
			this.id = jsonObject.getString("id");
			this.time = jsonObject.getLong("time");
			try {
				this.component=jsonObject.getString("component");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			}
			try {
				this.from = jsonObject.getString("from");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			}
			try {
				this.to = jsonObject.getString("to");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			}			
		}else
		{
			throw new JSONException("jsonObject NULL");
		}
	}

	public String getId() {
		if(id==null)
		{
			id=""+this.hashCode();
		}
		return id;
	}

	public void setId(String id) {
		if (jsonObject != null) {
			try {
				jsonObject.put("id", id);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		if (jsonObject != null) {
			// 不允许修改客户端发送过来的消息组件
			return;
		}
		this.component = component;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		if (jsonObject != null) {
			try {
				jsonObject.put("time", time);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.time = time;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		if (jsonObject != null) {
			try {
				jsonObject.put("to", to);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.to = to;
	}

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		if (jsonObject != null) {
			try {
				jsonObject.put("from", from);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.from = from;
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
					+ "\"component\":\"" + getComponent() +"\","
					+ "\"time\":\"" + getTime() +"\" "
					+ "}";
		}	
	
	}

}
