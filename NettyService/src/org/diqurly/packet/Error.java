package org.diqurly.packet;


/**
 * ������Ϣ
 * ������룺
 * 	�����ࣺ1000
 *  	1100У�鳬ʱ�����Ӻ��뷢��У����Ϣ֮���ʱ�䳬ʱ
 *  	1300�ദ��½��ʾ�����߳�֮ǰ������
 *  		1301����У�鳬ʱ�����������˺������½У��
 *  	1200�˺Ż��������
 *  ��Ϣ��  2000
 *  	2100����������Ϣ���ʧ�ܣ���ͨ���˴��󣬶Գ�����ж�λ�Ŵ�����MessageRouteThread�ǲ��Ǳ���
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
