package org.diqurly.handler;

import io.netty.channel.Channel;

import org.diqurly.connect.listening.ConnectListening;
/**
 * ”√”⁄Client
 * @author diqurly
 *
 */
public abstract class DChandlerInterface extends DhandlerInterface{
	protected ConnectListening<Channel> connectListen;
	public void addConnectListening(ConnectListening<Channel> connectListen) {
		this.connectListen = connectListen;
	}
}
