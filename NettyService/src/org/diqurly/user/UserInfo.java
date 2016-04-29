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
	 * ���������Ϣ
	 * 
	 * @param connectInfo
	 */
	public void addConnectInfo(ConnectInfo<E> connectInfo) {
		connectInfos.add(connectInfo);
	}

	/**
	 * ��½�豸������һ
	 */
	public void addDevices() {
		devices++;
	}

	/**
	 * ��½�豸������һ
	 */
	public void minusDevices() {
		if (devices > 0)
			devices--;
	}

	/**
	 * ��ȡ��¼�豸����
	 * 
	 * @return
	 */
	public int getDevices() {
		return this.devices;
	}
}
