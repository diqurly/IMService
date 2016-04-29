package org.diqurly.component;

import java.util.concurrent.BlockingQueue;

import org.diqurly.packet.Packet;

public interface Component {
	/**
	 * 组件名称设置
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 获取转发到此组件的消息
	 * 
	 * @param packet
	 */
	public void packagePacket(Packet packet,BlockingQueue<Packet> queue);
	public void packagePacket(Packet packet);

	/**
	 * 发送消息，阻塞模式
	 * 
	 * @param packet
	 * @throws InterruptedException 
	 */
	public void sendBlock(Packet packet) throws InterruptedException;

	/**
	 * 发送消息，非阻塞模式
	 * 
	 * @param packet
	 * @return 成功true 失败 false
	 */
	public boolean send(Packet packet);
}
