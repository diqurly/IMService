package org.diqurly.connect;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 连接管理 管理集群、分布式连接
 * 
 * @author diqurly
 * @param <E>
 *
 */
public class ConnectManage<E extends Channel> {
	// private BlockingQueue<ChannelHandlerContext> cacheConnects;
	// private ConcurrentSkipListSet<ConnectInfo<E>> cacheConnects;
	private ConcurrentHashMap<E, Long> cacheConnects;

	// 只有在开启发布，集群的情况下才生效。
	// 用于判断该连接发送的消息是否合法
	private ConcurrentSkipListSet<E> connects;
	// 查找对应角色的服务器，用于不同服务器之间消息转发
	private ConcurrentHashMap<Integer, E> services;

	
	
	public ConnectManage()
	{
		init();
	}
	
	public void init() {
		// cacheConnects = new
		// ArrayBlockingQueue<ChannelHandlerContext>(capacity);
		// cacheConnects =new ConcurrentSkipListSet<ConnectInfo<E>>();
		cacheConnects = new ConcurrentHashMap<E, Long>();
		connects = new ConcurrentSkipListSet<E>();
		services = new ConcurrentHashMap<Integer, E>();
	}

	/**
	 * 获取缓存连接
	 * 
	 * @return
	 */
	public ConcurrentHashMap<E, Long> getCacheConnects() {
		return this.cacheConnects;
	}

	/**
	 * 判断该连接是否合法
	 * 
	 * @param connect
	 * @return
	 */
	public boolean isCheck(E connect) {
		return connects.contains(connect);
	}

	/**
	 * 获取相关角色的通道
	 * 
	 * @param role
	 * @return
	 */
	public E getChannel(int role) {
		return services.get(role);
	}

	/**
	 * 添加缓存连接，用于对连接进行超时验证
	 * 
	 * @param connect
	 */
	public void addCacheCo(E connect) {
		cacheConnects.put(connect, System.currentTimeMillis());
	}

	/**
	 * 移除缓存连接
	 * 
	 * @param connect
	 */
	public void rmCacheCo(E connect) {
		cacheConnects.remove(connect);
	}

	/**
	 * 添加通过校验的服务连接
	 * 
	 * @param connect
	 */
	public void addConnect(E connect) {
		connects.add(connect);
	}

	/**
	 * 移除服务连接
	 * 
	 * @param connect
	 */
	public void rmConnect(E connect) {
		connects.remove(connect);
		connect.close();
	}

	/**
	 * 添加角色 连接映射关系
	 * 
	 * @param role
	 * @param connect
	 */
	public void addChannel(int role, E connect) {
		services.put(role, connect);
	}

	/**
	 * 移除角色 连接映射关系
	 * 
	 * @param role
	 */
	public void rmChannel(int role) {
		services.get(role).close();
		services.remove(role);
	}

	/**
	 * 角色服务器是否存在
	 * 
	 * @param role
	 * @return 存在true 不存在false
	 */
	public boolean existRole(int role) {
		return services.contains(role);
	}
}
