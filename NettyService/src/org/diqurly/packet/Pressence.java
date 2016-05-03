package org.diqurly.packet;


/**
 * ����״̬
 * @author diqurly
 *
 */
public class Pressence extends Packet{

	private String msg;
	
	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}


	public Pressence()
	{
		super();
		this.type="pressence";
	}
	
	
	
	/**
	 * ���ӳɹ��󷵻����ӵı�ʶ�룬��������
	 * @param code
	 */
	public void connectSuccess(String code)
	{
		msg="{"
				+ "\"type\":\"connect\","
				+ "\"code\":\""+code+"\" "
				+ "}";
	}
	
	
	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return "{" 
		+ "\"id\":\"" + getId()+"\"," 
		+ "\"type\":\"" + getType() +"\","
		+ "\"msg\":" + getMsg() +","
		+ "\"time\":\"" + getTime() +"\" "
		+ "}";
	}
	
}
