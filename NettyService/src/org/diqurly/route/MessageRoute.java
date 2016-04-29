package org.diqurly.route;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.diqurly.component.Component;
import org.diqurly.component.ComponentService;
import org.diqurly.config.ConfigConst;
import org.diqurly.connect.ConnectInfo;
import org.diqurly.connect.ConnectManage;
import org.diqurly.packet.Error;
import org.diqurly.packet.Message;
import org.diqurly.packet.Packet;
import org.diqurly.packet.Pressence;
import org.diqurly.user.UserInfo;
import org.diqurly.user.UserManage;

/**
 * 消息路由转发
 * 
 * @author diqurly
 * @param <E>
 *
 */
public class MessageRoute<E extends Channel> extends ComponentService{
	private ConnectManage<E> connectMange;
	private UserManage<E> userManage;
	
	private HashMap<String, Component> components=new HashMap<String, Component>();
	//private BlockingQueue<Packet> packetQueue;
	
	
	private BlockingQueue<Packet> queue=new ArrayBlockingQueue<Packet>(2000);
	
	
	//用线程组来对消息的转发？？数量为CPU/3个为零则默认为一个
	ThreadGroup tg=new ThreadGroup("forward group");
	
	public MessageRoute(ConnectManage<E> connectMange,
			UserManage<E> userManage) {
		this.connectMange = connectMange;
		this.userManage = userManage;
		// this.packetQueue = packetQueue;
		for (int i = 0; i < getThreas(); i++) {
			new Thread(tg, new Runnable() {
				public void run() {
					Packet p;
					while (true) {
						try {
							p = queue.take();
							packetRoute(p);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}, "forward-" + i).start();;
		}

	}
	

	private void msgRoute(Message message) {
		String type = message.getType();
		String msg = message.toJson();
		if ("chat".equals(type)) {
			String to = message.getTo();
			// 接收者比对，是否在缓存中
			if (userManage.uExist(to)) {
				UserInfo<E> userInfo = userManage.getUserConnect(to);
				HashSet<ConnectInfo<E>> connectInfo = userInfo
						.getConnectInfos();
				Iterator<ConnectInfo<E>> iter = connectInfo.iterator();
				while (iter.hasNext()) {
					ConnectInfo<E> i = iter.next();
					i.getConnect().writeAndFlush(msg);
				}
				if (ConfigConst.ISDISTRIBUTED) {
					// 转发给状态服务器
					if (userInfo.getDevices() > 0) {
						routeSend(ConfigConst.DISTRIBUTED_STATUS, msg);
					}
				}
			} else {
				// 不在
				// 判断是否开启集群
				if (ConfigConst.ISDISTRIBUTED) {
					// 转发给状态服务器
					routeSend(ConfigConst.DISTRIBUTED_STATUS, msg);
				} else {
					// 离线保存
				}
			}

		} else if ("groupchat".equals(type)) {
			// 判断是否开启集群
			if (ConfigConst.ISDISTRIBUTED) {
				// 转发给群服务器
				routeSend(ConfigConst.DISTRIBUTED_GROUP, msg);
			} else {
				// 转发给群类
			}
		}
	}

	private void errorRoute(Error error) {
	}

	private void preRoute(Pressence pressence) {

	}

	private void routeSend(int role, String msg) {
		connectMange.getChannel(role).writeAndFlush(msg);
	}

	private Component getComponent(String name)
	{
		return	components.get(name);
	}
	
	public void registerComponent(String name ,Component com)
	{
		components.put(name, com);
	}
	public void removeComponent(String name)
	{
		components.remove(name);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MessageRoute";
	}



	@Override
	public void sendBlock(Packet packet) throws InterruptedException {
		// TODO Auto-generated method stub
		queue.put(packet);
	}

	@Override
	public boolean send(Packet packet) {
		// TODO Auto-generated method stub
		return queue.offer(packet);
	}
	public void packagePacket(Packet packet) {
		// TODO Auto-generated method stub

		String com = packet.getComponent();
		if (com == null || com.isEmpty()) {
			if (packet instanceof Message) {
				send(packet);
			} else if (packet instanceof Error) {
				errorRoute((Error) packet);
			} else if (packet instanceof Pressence) {
				preRoute((Pressence) packet);
			}
		} else {
			// 接收组件
			// 判断该组件是否存在
			// 组件存在消息转发给对应组件
			// 不合法则return;
			Component component;
			try {
				component = getComponent(com);
			} catch (Exception e) {
				// TODO: handle exception
				return;
			}
			if (component != null) {
				component.packagePacket(packet, queue);
			}
		}

	}
	

	
	private int getThreas()
	{
		int threas = Runtime.getRuntime().availableProcessors()/3;
		if(threas<=0)
			threas=1;
		return threas;
		
	}

	

	/**
	 * 消息转发给客户端
	 * @param packet
	 */
	private void packetRoute(Packet packet)
	{
		String msg = packet.toJson();
		String to = packet.getTo();
		// 接收者比对，是否在缓存中
		if (userManage.uExist(to)) {
			UserInfo<E> userInfo = userManage.getUserConnect(to);
			HashSet<ConnectInfo<E>> connectInfo = userInfo.getConnectInfos();
			Iterator<ConnectInfo<E>> iter = connectInfo.iterator();
			while (iter.hasNext()) {
				ConnectInfo<E> i = iter.next();
				i.getConnect().writeAndFlush(msg);
			}
			if (ConfigConst.ISDISTRIBUTED) {
				// 转发给状态服务器
				if (userInfo.getDevices() > 0) {
					routeSend(ConfigConst.DISTRIBUTED_STATUS, msg);
				}
			}
		} else {
			// 不在
			// 判断是否开启集群
			if (ConfigConst.ISDISTRIBUTED) {
				// 转发给状态服务器
				routeSend(ConfigConst.DISTRIBUTED_STATUS, msg);
			} else {
				// 离线保存
			}
		}
		
	}
	
	
}
