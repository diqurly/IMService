package org.diqurly.component;

import java.util.concurrent.BlockingQueue;

import org.diqurly.packet.Packet;

public  class ComponentService implements Component{
private BlockingQueue<Packet> queue;
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
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
	@Override
	public void packagePacket(Packet packet, BlockingQueue<Packet> queue) {
		// TODO Auto-generated method stub
		this.queue=queue;
		
		//假如组件阻塞后怎么办？？？
	}

	@Override
	public void packagePacket(Packet packet) {
		// TODO Auto-generated method stub
		
	}
}
