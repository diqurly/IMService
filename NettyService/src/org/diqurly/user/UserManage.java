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
 * 用户管理类，管理用户在线
 * {@value
 * ===500一组===
 * 已用：3408856--总内存：1.7676 G     0

已用：23075488--总内存：1.7676 G   500
已用：35760328--总内存：1.7676 G   1000
已用：15932264--总内存：1.7676 G   1500
已用：27179536--总内存：1.7676 G   2000
已用：40630400--总内存：1.7676 G   2500
已用：23755456--总内存：1.7676 G   3000



500一组内存占用测试  500  39333.264
					1000  25369.68
					1500   -
					2000  22494.544
					2500  26901.728
					3000  -
	平均每个用户占用6782.2字节= 6.6232kb=0.006468mb
	一万个用户大概占用64.68mb
 *     ===结束===
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
	 * 登陆
	 * 
	 * @param connect
	 * @param userSeria
	 *            登陆信息的序列化消息
	 */
	/**
	 *  登陆
	 * 
	 * @param connect
	 * @param userSeria
	 *            登陆信息的序列化消息
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
	 * 退出
	 * 
	 * @param connect
	 */
	public void loginOut(E connect) {
		removeUser(connect);
		connect.close();
	}

	/**
	 * 添加缓存用户
	 * 
	 * @param user
	 * @param connect
	 */
	private void addUser(E connect, String user) {
		cacheUsers.put(connect, user);
		if (ConfigConst.DISTRIBUTED_STATUS != ConfigConst.DISTRIBUTED_ROLE
				&& ConfigConst.ISDISTRIBUTED) {
			// 发送上线状态给状态标识服务器 用户的 用户名， 地点，设备……信息
		}
	}

	/**
	 * 移除缓存用户
	 * 
	 * @param user
	 */
	private void removeUser(E connect) {

		String userId = cacheUsers.get(connect);
		if (userId == null)
			return;
		if (ConfigConst.DISTRIBUTED_STATUS != ConfigConst.DISTRIBUTED_ROLE
				&& ConfigConst.ISDISTRIBUTED) {
			// 发送下线状态给状态标识服务器 用户的用户名
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
	 * 连接是否合法
	 * 
	 * @param connect
	 * @return
	 */
	public boolean isLegal(E connect) {
		return cacheUsers.containsKey(connect);
	}

	/**
	 * 账号密码校验
	 * 
	 * @param connect
	 * @param userSeria
	 * @param pressence
	 * @return
	 */
	private boolean pawCheck(E connect, UserSerializable userSeria,
			Pressence pressence) {
		// 密码校验
		String userId = userSeria.getUserID();
		String password = userSeria.getPassword();

		// 查找数据库进行密码校验

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
					// 设备号相等，踢出之前的登陆用户，给之前登陆用户发送一条错误
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
	 * 重连校验
	 * 
	 * @param connect
	 * @param userSeria
	 * @param pressence
	 * @return
	 */
	private boolean rConCheck(E connect, UserSerializable userSeria,
			Pressence pressence) {
		// 重连校验

		// 根据用户ID　获取connect.

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
	 * 判读用户是否在线，单机判断是否在线，分布判断是否在本机
	 * 
	 * @param user
	 * @return
	 */
	public boolean uExist(String user) {
		return userInfos.containsKey(user);
	}

	/**
	 * 获取在线用户的连接
	 * 
	 * @param user
	 * @return
	 */
	public UserInfo<E> getUserConnect(String user) {
		return userInfos.get(user);
	}
}
