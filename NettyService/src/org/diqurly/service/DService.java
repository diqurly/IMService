package org.diqurly.service;

import org.diqurly.config.ConfiguratorAbstract;

public class DService {
	private static ConfiguratorAbstract config;

	public static void main(String[] args) {
		
		config=new ConfiguratorAbstract();
		config.init(args);
		config.start();
		
//		ConnectManage<Channel> connectMange = new ConnectManage<Channel>();
//
//		UserManage<Channel> userManage = new UserManage<Channel>();
//		BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>(2000);
//
//		CacheConnectThread<Channel> cacheConnectThread = new CacheConnectThread<Channel>(
//				connectMange.getCacheConnects());
//		MessageRouteThread<Channel> messageRouteThread = new MessageRouteThread<Channel>(
//				connectMange, userManage, queue);
//		try {
//			ServerBC aa = new ServerBC(8007, new ConnectServerHandler(
//					connectMange, userManage, queue));
//			cacheConnectThread.start();
//			messageRouteThread.start();
//			aa.start();
//		} catch (CertificateException | SSLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	public static ConfiguratorAbstract getConfigurator() {
		return config;
	}

}
