package org.diqurly.route;

import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;

import org.diqurly.connect.ConnectManage;
import org.diqurly.packet.Packet;
import org.diqurly.user.UserManage;
/**
 * 消息转发线程
 * @author diqurly
 *
 * @param <E>
 */
public class MessageRouteThread<E extends Channel> extends Thread {
	private boolean isStop = false;
	private BlockingQueue<Packet> queue;
	private MessageRoute<E> msgRoute;

	public MessageRouteThread(ConnectManage<E> connectMange,
			UserManage<E> userManage, BlockingQueue<Packet> queue) {
		this.queue = queue;
		msgRoute = new MessageRoute<E>(connectMange, userManage);
		this.setName("MessageRoutThread");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!isStop) {
			try {
				msgRoute.packagePacket(queue.take());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// 错误中断
				//this.interrupt();
			}
		}
	}

	public void close() {
		isStop = true;
		this.interrupt();
	}

}
