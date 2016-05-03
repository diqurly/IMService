package org.diqurly.route;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.diqurly.component.AbstractMessageRoute;
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
 * ��Ϣ·��ת��
 * 
 * @author diqurly
 * @param <E>
 *
 */
public class MessageRoute<E extends Channel> extends AbstractMessageRoute {
	private ConnectManage<E> connectMange;
	private UserManage<E> userManage;

	private HashMap<String, ComponentService> components = new HashMap<String, ComponentService>();
	// private BlockingQueue<Packet> packetQueue;

	private BlockingQueue<Packet> queue = new ArrayBlockingQueue<Packet>(2000);

	// ���߳���������Ϣ��ת����������ΪCPU/3��Ϊ����Ĭ��Ϊһ��
	ThreadGroup tg = new ThreadGroup("forward group");

	public MessageRoute(ConnectManage<E> connectMange, UserManage<E> userManage) {
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
			}, "forward-" + i).start();
			;
		}

	}

	private void msgRoute(Message message) {
		String type = message.getType();
		String msg = message.toJson();
		if ("chat".equals(type)) {
			String to = message.getTo();
			// �����߱ȶԣ��Ƿ��ڻ�����
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
					// ת����״̬������
					if (userInfo.getDevices() > 0) {
						routeSend(ConfigConst.DISTRIBUTED_STATUS, msg);
					}
				}
			} else {
				// ����
				// �ж��Ƿ�����Ⱥ
				if (ConfigConst.ISDISTRIBUTED) {
					// ת����״̬������
					routeSend(ConfigConst.DISTRIBUTED_STATUS, msg);
				} else {
					// ���߱���
				}
			}

		} else if ("groupchat".equals(type)) {
			// �ж��Ƿ�����Ⱥ
			if (ConfigConst.ISDISTRIBUTED) {
				// ת����Ⱥ������
				routeSend(ConfigConst.DISTRIBUTED_GROUP, msg);
			} else {
				// ת����Ⱥ��
			}
		}
	}

	private void errorRoute(Error error) {
	}

	private void preRoute(Pressence pressence) {

	}

	/**
	 * ת������Ӧ��ɫ������
	 * 
	 * @param role
	 * @param msg
	 */
	private void routeSend(int role, String msg) {
		connectMange.getChannel(role).writeAndFlush(msg);
	}

	private ComponentService getComponent(String name) {
		return components.get(name);
	}

	/**
	 * ע�����
	 * 
	 * @param com
	 *            �����
	 */
	public void registerComponent(ComponentService com) {
		components.put(com.getName(), com);
	}

	/**
	 * �Ƴ����
	 * 
	 * @param com
	 *            �����
	 */
	public void removeComponent(ComponentService com) {
		components.remove(com.getName());
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

			// �ж��Ƿ�����Ⱥ
			if (ConfigConst.ISDISTRIBUTED) {
				// ת����Ⱥ������
				
				//�ж���������Ƿ�ע�ᣬ����Ͽ�������ת��
				
				routeSend(ConfigConst.DISTRIBUTED_GROUP, packet.toJson());
			} else {
				// ת����Ⱥ��
				// �������
				// �жϸ�����Ƿ����
				// ���������Ϣת������Ӧ���
				// ���Ϸ���return;
				ComponentService component;
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

	}

	private int getThreas() {
		int threas = Runtime.getRuntime().availableProcessors() / 3;
		if (threas <= 0)
			threas = 1;
		return threas;

	}

	/**
	 * ��Ϣת�����ͻ���
	 * 
	 * @param packet
	 */
	private void packetRoute(Packet packet) {
		String msg = packet.toJson();
		String to = packet.getTo();
		// �����߱ȶԣ��Ƿ��ڻ�����
		if (userManage.uExist(to)) {
			UserInfo<E> userInfo = userManage.getUserConnect(to);
			HashSet<ConnectInfo<E>> connectInfo = userInfo.getConnectInfos();
			Iterator<ConnectInfo<E>> iter = connectInfo.iterator();
			while (iter.hasNext()) {
				ConnectInfo<E> i = iter.next();
				i.getConnect().writeAndFlush(msg);
			}
			if (ConfigConst.ISDISTRIBUTED) {
				// ת����״̬������
				if (userInfo.getDevices() > 0) {
					routeSend(ConfigConst.DISTRIBUTED_STATUS, msg);
				}
			}
		} else {
			// ����
			// �ж��Ƿ�����Ⱥ
			if (ConfigConst.ISDISTRIBUTED) {
				// ת����״̬������
				routeSend(ConfigConst.DISTRIBUTED_STATUS, msg);
			} else {
				// ���߱���
			}
		}

	}

}
