package com.diqurly.test;

import java.net.InetSocketAddress;
import java.util.Scanner;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class HelloWorldServer {
	 static final boolean SSL = System.getProperty("ssl") != null;
	// static final boolean SSL = true;
	    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
	 
	    public static void main(String[] args) throws Exception {
	        // Configure SSL.
	        final SslContext sslCtx;
	        if (SSL) {
	            SelfSignedCertificate ssc = new SelfSignedCertificate();
	            sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
	        } else {
	            sslCtx = null;
	        }
	 
	        // Configure the server.
	        final EventLoopGroup bossGroup = new NioEventLoopGroup(1); 
	        final EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            ServerBootstrap b = new ServerBootstrap();
	            b.group(bossGroup, workerGroup)
	             .channel(NioServerSocketChannel.class)
	             .option(ChannelOption.SO_BACKLOG, 100)
	             .handler(new LoggingHandler(LogLevel.INFO))
	             .childHandler(new ChannelInitializer<SocketChannel>() {
	                 @Override
	                 public void initChannel(SocketChannel ch) throws Exception {
	                	InetSocketAddress aa = ch.localAddress();
	                	System.out.println(bossGroup.children().size()+"--"+workerGroup.children().size());

	                	 System.out.println("port:"+aa.getPort()+"   name:"+aa.getHostName()+"-"+aa.getHostString());
	                     ChannelPipeline p = ch.pipeline();
	                     if (sslCtx != null) {
	                         p.addLast(sslCtx.newHandler(ch.alloc()));
	                     }
	                     p.addLast(new LoggingHandler(LogLevel.INFO));
	                     p.addLast(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(null)),new HelloWorldServerHandler());
	                 }
	             });
	 
	            // Start the server.
	            ChannelFuture f = b.bind(PORT).sync();
	          //  f.channel().close();
	            // Wait until the server socket is closed.
	            f.channel().closeFuture().sync();    //×èÈû
	      
	            
	      
	            
	            
	        } finally {
	            // Shut down all event loops to terminate all threads.
	            bossGroup.shutdownGracefully();
	            workerGroup.shutdownGracefully();
	        }
	    }
	}

