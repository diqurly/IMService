package org.diqurly.service;

import java.io.Serializable;

/**
 * ���񲢷�����ȺУ����
 * 
 * @author diqurly
 *
 */
public class ServiceSerializable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1922612255311414296L;
	//�������ӽ�ɫ
	private int role;
	// У���룬���ڶ����ӽ�������Ҫ����
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
