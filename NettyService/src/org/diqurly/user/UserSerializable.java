package org.diqurly.user;

import java.io.Serializable;

/**
 * 用户校验序列化 类
 * 
 * @author diqurly
 *
 */

public class UserSerializable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 918882446573716384L;

	private String userID;
	private String password;
	// 校验类型 1是密码校验 2是重连校验
	private int connectType;
	private String connectCode;
	//设备号唯一
	private String device;
//所在城市
	private String city;
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getConnectType() {
		return connectType;
	}

	public void setConnectType(int connectType) {
		this.connectType = connectType;
	}

	public String getConnectCode() {
		return connectCode;
	}

	public void setConnectCode(String connectCode) {
		this.connectCode = connectCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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


}
