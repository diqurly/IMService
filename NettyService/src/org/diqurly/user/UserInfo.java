package org.diqurly.user;

import java.util.HashSet;

import org.diqurly.connect.ConnectInfo;

public class UserInfo<E> {
	private HashSet<ConnectInfo<E>> connectInfos;

	private int devices = 0;

	public UserInfo() {
		connectInfos=new HashSet<ConnectInfo<E>>();
	}

	public HashSet<ConnectInfo<E>> getConnectInfos() {
		return connectInfos;
	}

	public void setConnectInfos(HashSet<ConnectInfo<E>> connectInfos) {
		this.connectInfos = connectInfos;
	}

	/**
	 * 添加连接信息
	 * 
	 * @param connectInfo
	 */
	public void addConnectInfo(ConnectInfo<E> connectInfo) {
		connectInfos.add(connectInfo);
	}

	/**
	 * 登陆设备数量加一
	 */
	public void addDevices() {
		devices++;
	}

	/**
	 * 登陆设备数量减一
	 */
	public void minusDevices() {
		if (devices > 0)
			devices--;
	}

	/**
	 * 获取登录设备数量
	 * 
	 * @return
	 */
	public int getDevices() {
		return this.devices;
	}
}
