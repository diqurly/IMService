package com.diqurly.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIOServer {
	static final int PORT = 30000;
	static List<AsynchronousSocketChannel> channelList = new ArrayList<AsynchronousSocketChannel>();
final static String UTF_8="utf-8";
	public void startListen() throws IOException {
		ExecutorService excutor = Executors.newFixedThreadPool(20);
		AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup
				.withThreadPool(excutor);
		AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel
				.open(channelGroup).bind(new InetSocketAddress(PORT));
		
		serverChannel.accept(null, new Accepthandler(serverChannel));

	}
	
	public static void main(String[] args) throws IOException {
		AIOServer server=new AIOServer();
		server.startListen();
		
		while(true)
		{
			
		}
		
	}
	
	
	
}
