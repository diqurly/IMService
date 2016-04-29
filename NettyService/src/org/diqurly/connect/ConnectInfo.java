package org.diqurly.connect;
/**
 * 连接信息集合
 * @author diqurly
 *
 * @param <E>
 */
public class ConnectInfo<E> {
	private E connect;
	/**
	 * 设备名称 用于进行推送的方式 设备号唯一
	 */
	private String device;
	/**
	 * 当前城市 ，针对城市发送推送
	 */
	private String city;
	/**
	 * 连接标识码，重连时不需要再接收密码
	 */
	private int connectCode;
	/**
	 * 重连次数，用于校验非法重连
	 */
	private int reConnectNum = 0;
	/**
	 * 所在主机
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
	 * 重连标识码校验
	 * 
	 * @param code
	 * @return true 合法 false 非法
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
	 * 获取重连标识码
	 * 
	 * @return
	 */
	public String getCode() {
		return connectCode + "-" + (reConnectNum + 1);
	}

}
