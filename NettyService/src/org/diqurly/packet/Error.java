package org.diqurly.packet;


/**
 * 错误消息
 * 错误代码：
 * 	连接类：1000
 *  	1100校验超时，连接后与发送校验信息之间的时间超时
 *  	1300多处登陆提示，且踢出之前的连接
 *  		1301重连校验超时，重新输入账号密码登陆校验
 *  	1200账号或密码错误
 *  消息类  2000
 *  	2100队列满，消息添加失败，可通过此错误，对程序进行定位排错。例如MessageRouteThread是不是奔溃
 * @author diqurly
 *
 */
public class Error extends Packet{
	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Error() {
		super();
		this.type = "error";
	}

	public Error(int code) {
		super();
		this.type = "error";
		this.code = code;
	}
	
	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return "{" 
		+ "\"id\":\"" + getId()+"\"," 
		+ "\"type\":\"" + getType() +"\","
		+ "\"code\":\"" + getCode() +"\","
		+ "\"time\":\"" + getTime() +"\""
		+ "}";
	}
}
