package org.diqurly.config;

import io.netty.channel.Channel;

import javax.net.ssl.SSLException;

import org.diqurly.handler.DChandlerInterface;
import org.diqurly.service.ClientBC;

/**
 * ���������������߳�
 * @author diqurly
 *
 */
public class ConnectToServicesThread implements Runnable {
	private boolean SSL = false;
	private String host;
	private int port;
	private DChandlerInterface handler;
	private ClientBC client;

	public ConnectToServicesThread(boolean SSL, String host, int port,
			DChandlerInterface handler) throws SSLException {
		this.host = host;
		this.port = port;
		this.SSL = SSL;
		this.handler = handler;
	}

	@Override
	public void run() {

		// TODO Auto-generated method stub
		try {
			client = new ClientBC(SSL, host, port, handler);
			client.connect();
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ���ӹܵ�
	 * 
	 * @return
	 */
	public Channel getChannel() {
		return client.getChannel();
	}
}
