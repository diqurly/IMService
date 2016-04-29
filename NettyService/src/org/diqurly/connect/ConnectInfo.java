package org.diqurly.connect;
/**
 * ������Ϣ����
 * @author diqurly
 *
 * @param <E>
 */
public class ConnectInfo<E> {
	private E connect;
	/**
	 * �豸���� ���ڽ������͵ķ�ʽ �豸��Ψһ
	 */
	private String device;
	/**
	 * ��ǰ���� ����Գ��з�������
	 */
	private String city;
	/**
	 * ���ӱ�ʶ�룬����ʱ����Ҫ�ٽ�������
	 */
	private int connectCode;
	/**
	 * ��������������У��Ƿ�����
	 */
	private int reConnectNum = 0;
	/**
	 * ��������
	 */
	private int host;

	public ConnectInfo(E connect) {
		this.connect = connect;
		connectCode = this.hashCode();
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getConnectCode() {
		return connectCode;
	}

	public int getReConnectNum() {
		return reConnectNum;
	}

	public int getHost() {
		return host;
	}

	public void setHost(int host) {
		this.host = host;
	}

	public E getConnect() {
		return connect;
	}

	public void setConnect(E connect) {
		this.connect = connect;
		connectCode = this.hashCode();
	}

	/**
	 * ������ʶ��У��
	 * 
	 * @param code
	 * @return true �Ϸ� false �Ƿ�
	 */
	public boolean isCheckCode(String code) {
		try {
			String[] codes = code.split("-");
			int cCode = Integer.parseInt(codes[0]);
			if (cCode == connectCode) {
				int cNum = Integer.parseInt(codes[1]);
				if ((cNum - reConnectNum) == 1) {
					reConnectNum++;
					return true;
				}

			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	/**
	 * ��ȡ������ʶ��
	 * 
	 * @return
	 */
	public String getCode() {
		return connectCode + "-" + (reConnectNum + 1);
	}

}
