package org.diqurly.service;

import io.netty.channel.Channel;

import java.security.cert.CertificateException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLException;

import org.diqurly.connect.ConnectManage;
import org.diqurly.connect.ConnectServerHandler;
import org.diqurly.connect.thread.CacheConnectThread;
import org.diqurly.packet.Packet;
import org.diqurly.route.MessageRouteThread;
import org.diqurly.user.UserManage;

public class DService {

	public static void main(String[] args) {
		ConnectManage<Channel> connectMange = new ConnectManage<Channel>();

		UserManage<Channel> userManage = new UserManage<Channel>();
		BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>(2000);

		CacheConnectThread<Channel> cacheConnectThread = new CacheConnectThread<Channel>(
				connectMange.getCacheConnects());
		MessageRouteThread<Channel> messageRouteThread = new MessageRouteThread<Channel>(
				connectMange, userManage, queue);
		try {
			ServerBC aa = new ServerBC(8007, new ConnectServerHandler(
					connectMange, userManage, queue));
			cacheConnectThread.start();
			messageRouteThread.start();
			aa.start();
		} catch (CertificateException | SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
