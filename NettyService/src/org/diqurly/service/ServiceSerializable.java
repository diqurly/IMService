package org.diqurly.service;

import java.io.Serializable;

/**
 * 服务并发，集群校验类
 * 
 * @author diqurly
 *
 */
public class ServiceSerializable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1922612255311414296L;
	//服务连接角色
	private int role;
	// 校验码，用于对连接建立的重要依据
	private String checkCode;
	private long time;

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
