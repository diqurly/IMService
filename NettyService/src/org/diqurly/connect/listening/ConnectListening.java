package org.diqurly.connect.listening;

import io.netty.channel.Channel;

/**
 * 连接监听
 * @author diqurly
 *
 */
public interface ConnectListening<E extends Channel> {
	/**
	 * 连接
	 */
	public void connect(E connect);

	/**
	 * 断开
	 */
	public void disconnect(E connect);
}
