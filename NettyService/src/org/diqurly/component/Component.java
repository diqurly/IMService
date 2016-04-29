package org.diqurly.component;

import java.util.concurrent.BlockingQueue;

import org.diqurly.packet.Packet;

public interface Component {
	/**
	 * �����������
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * ��ȡת�������������Ϣ
	 * 
	 * @param packet
	 */
	public void packagePacket(Packet packet,BlockingQueue<Packet> queue);
	public void packagePacket(Packet packet);

	/**
	 * ������Ϣ������ģʽ
	 * 
	 * @param packet
	 * @throws InterruptedException 
	 */
	public void sendBlock(Packet packet) throws InterruptedException;

	/**
	 * ������Ϣ��������ģʽ
	 * 
	 * @param packet
	 * @return �ɹ�true ʧ�� false
	 */
	public boolean send(Packet packet);
}
