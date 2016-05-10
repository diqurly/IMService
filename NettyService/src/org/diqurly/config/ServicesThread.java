package org.diqurly.config;

import org.diqurly.handler.DhandlerInterface;
import org.diqurly.service.ServerBC;

public class ServicesThread implements Runnable{
	private boolean ssl = false;
	private int port;
	private DhandlerInterface handler;
	
	public ServicesThread(boolean ssl,int port,DhandlerInterface handler)
	{
		this.ssl=ssl;
		this.port=port;
		this.handler=handler;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerBC service = new ServerBC(ssl,port, handler);
			service.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
