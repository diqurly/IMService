package org.diqurly.component;

import org.diqurly.packet.Packet;

public abstract class AbstractMessageRoute extends ComponentService{
	public abstract void packagePacket(Packet packet);
}
