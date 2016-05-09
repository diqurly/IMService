package org.diqurly.connect.listening;

import io.netty.channel.Channel;

/**
 * ���Ӽ���
 * @author diqurly
 *
 */
public interface ConnectListening<E extends Channel> {
	/**
	 * ����
	 */
	public void connect(E connect);

	/**
	 * �Ͽ�
	 */
	public void disconnect(E connect);
}
