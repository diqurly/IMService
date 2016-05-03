package org.diqurly.user;

import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.diqurly.config.ConfigConst;
import org.diqurly.connect.ConnectInfo;
import org.diqurly.packet.Error;
import org.diqurly.packet.Pressence;
import org.diqurly.util.MemoryUtil;

/**
 * �û������࣬�����û�����
 * {@value
 * ===500һ��===
 * ���ã�3408856--���ڴ棺1.7676 G     0

���ã�23075488--���ڴ棺1.7676 G   500
���ã�35760328--���ڴ棺1.7676 G   1000
���ã�15932264--���ڴ棺1.7676 G   1500
���ã�27179536--���ڴ棺1.7676 G   2000
���ã�40630400--���ڴ棺1.7676 G   2500
���ã�23755456--���ڴ棺1.7676 G   3000



500һ���ڴ�ռ�ò���  500  39333.264
					1000  25369.68
					1500   -
					2000  22494.544
					2500  26901.728
					3000  -
	ƽ��ÿ���û�ռ��6782.2�ֽ�= 6.6232kb=0.006468mb
	һ����û����ռ��64.68mb
 *     ===����===
 * 
 * }
 * @author diqurly
 *
 * @param <E>
 */
public class UserManage<E extends Channel> {
//	ConcurrentSkipListMap<E, String> cacheUsers;
//	ConcurrentSkipListMap<String, UserInfo<E>> userInfos;
	ConcurrentHashMap<E, String> cacheUsers;
	ConcurrentHashMap<String, UserInfo<E>> userInfos;
	private String errorMsg = null;

	private MemoryUtil memoryUtil=new MemoryUtil();
	
	public UserManage()
	{
		init();
		System.out.println(memoryUtil.getNowMemory());
	}
	
	public void init() {
//		cacheUsers = new ConcurrentSkipListMap<E, String>();
//		userInfos = new ConcurrentSkipListMap<String, UserInfo<E>>();
		cacheUsers = new ConcurrentHashMap<E, String>();
		userInfos = new ConcurrentHashMap<String, UserInfo<E>>();	
			errorMsg = new Error(1300).toJson();
	}

	/**
	 * ��½
	 * 
	 * @param connect
	 * @param userSeria
	 *            ��½��Ϣ�����л���Ϣ
	 */
	/**
	 *  ��½
	 * 
	 * @param connect
	 * @param userSeria
	 *            ��½��Ϣ�����л���Ϣ
	 * @return true success
	 * false failed
	 */
	public boolean login(E connect, UserSerializable userSeria) {
		Pressence pressence = null;
			pressence = new Pressence();
		boolean isSuccess = false;
		if (userSeria.getConnectType() == 1) {
			isSuccess = pawCheck(connect, userSeria, pressence);
		} else if (userSeria.getConnectType() == 2) {
			isSuccess = rConCheck(connect, userSeria, pressence);
		} else {
			connect.close();
			return false;
		}
		if (isSuccess) {
			connect.writeAndFlush(pressence.toJson());
			//System.out.println(cacheUsers.size());
			if(cacheUsers.size()%500==0)
			System.out.println(memoryUtil.getNowMemory());
		}
		return isSuccess;
	}

	/**
	 * �˳�
	 * 
	 * @param connect
	 */
	public void loginOut(E connect) {
		removeUser(connect);
		connect.close();
	}

	/**
	 * ��ӻ����û�
	 * 
	 * @param user
	 * @param connect
	 */
	private void addUser(E connect, String user) {
		cacheUsers.put(connect, user);
		if (ConfigConst.DISTRIBUTED_STATUS != ConfigConst.DISTRIBUTED_ROLE
				&& ConfigConst.ISDISTRIBUTED) {
			// ��������״̬��״̬��ʶ������ �û��� �û����� �ص㣬�豸������Ϣ
		}
	}

	/**
	 * �Ƴ������û�
	 * 
	 * @param user
	 */
	private void removeUser(E connect) {

		String userId = cacheUsers.get(connect);
		if (userId == null)
			return;
		if (ConfigConst.DISTRIBUTED_STATUS != ConfigConst.DISTRIBUTED_ROLE
				&& ConfigConst.ISDISTRIBUTED) {
			// ��������״̬��״̬��ʶ������ �û����û���
		}
		cacheUsers.remove(connect);

		HashSet<ConnectInfo<E>> hashSet = userInfos.get(userId)
				.getConnectInfos();
		Iterator<ConnectInfo<E>> iter = hashSet.iterator();
		while (iter.hasNext()) {
			ConnectInfo<E> i = iter.next();
			if (i.getConnect().equals(connect)) {
				iter.remove();
			}
		}
		if (hashSet.isEmpty()) {
			userInfos.remove(userId);
		}

	}

	/**
	 * �����Ƿ�Ϸ�
	 * 
	 * @param connect
	 * @return
	 */
	public boolean isLegal(E connect) {
		return cacheUsers.containsKey(connect);
	}

	/**
	 * �˺�����У��
	 * 
	 * @param connect
	 * @param userSeria
	 * @param pressence
	 * @return
	 */
	private boolean pawCheck(E connect, UserSerializable userSeria,
			Pressence pressence) {
		// ����У��
		String userId = userSeria.getUserID();
		String password = userSeria.getPassword();

		// �������ݿ��������У��

		if (!"123456".equals(password)) {
				connect.writeAndFlush(new Error(1200).toJson());
			connect.close();
			return false;
		}
		addUser(connect, userId);
		UserInfo<E> userInfo = userInfos.get(userId);
		ConnectInfo<E> connectInfo = null;
		if (userInfo == null) {
			userInfo = new UserInfo<E>();
		} else {
			HashSet<ConnectInfo<E>> hashSet = userInfo.getConnectInfos();
			Iterator<ConnectInfo<E>> iter = hashSet.iterator();
			while (iter.hasNext()) {
				ConnectInfo<E> i = iter.next();
				if (userSeria.getDevice().equals(i.getDevice())) {
					// �豸����ȣ��߳�֮ǰ�ĵ�½�û�����֮ǰ��½�û�����һ������
					cacheUsers.remove(i.getConnect());
					i.getConnect().writeAndFlush(errorMsg);
					i.getConnect().close();
					iter.remove();
				}
			}
		}
		connectInfo = new ConnectInfo<E>(connect);
		connectInfo.setDevice(userSeria.getDevice());
		userInfo.addConnectInfo(connectInfo);
		userInfos.put(userId, userInfo);

		pressence.connectSuccess(connectInfo.getCode());
		return true;

	}

	/**
	 * ����У��
	 * 
	 * @param connect
	 * @param userSeria
	 * @param pressence
	 * @return
	 */
	private boolean rConCheck(E connect, UserSerializable userSeria,
			Pressence pressence) {
		// ����У��

		// �����û�ID����ȡconnect.

		UserInfo<E> userInfo = userInfos.get(userSeria.getUserID());
		if (userInfo == null) {
			connect.writeAndFlush(new Error(1301).toJson());
			connect.close();
			return false;
		}
		boolean isCheck = false;
		Iterator<ConnectInfo<E>> iter = userInfo.getConnectInfos().iterator();
		while (iter.hasNext()) {
			ConnectInfo<E> i = iter.next();
			if (userSeria.getDevice().equals(i.getDevice())) {
				isCheck = i.isCheckCode(userSeria.getConnectCode());
				if (isCheck) {
					i.getConnect().close();
					cacheUsers.remove(i.getConnect());
					i.setConnect(connect);
					pressence.connectSuccess(i.getCode());
					break;
				}
			}
		}
		if (!isCheck) {
			connect.writeAndFlush(new Error(1301).toJson());
			connect.close();
			return false;
		}
		return true;
	}

	/**
	 * �ж��û��Ƿ����ߣ������ж��Ƿ����ߣ��ֲ��ж��Ƿ��ڱ���
	 * 
	 * @param user
	 * @return
	 */
	public boolean uExist(String user) {
		return userInfos.containsKey(user);
	}

	/**
	 * ��ȡ�����û�������
	 * 
	 * @param user
	 * @return
	 */
	public UserInfo<E> getUserConnect(String user) {
		return userInfos.get(user);
	}
}
