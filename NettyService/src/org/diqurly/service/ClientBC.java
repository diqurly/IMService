package org.diqurly.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;

import org.diqurly.config.ConfigConst;
import org.diqurly.connect.listening.ConnectListening;
import org.diqurly.handler.DChandlerInterface;

/**
 * 分布 ，集群连接类
 * 
 * @author diqurly
 *
 */
public class ClientBC implements ConnectListening<Channel>{

	private String host;
	private int port;
	private boolean SSL;
	private SslContext sslCtx;
	private Bootstrap b;
	private ChannelFuture f;
	private EventLoopGroup group;
	private DChandlerInterface handler;

	/**
	 * 
	 * @param SSL
	 *            是否开启ssl
	 * @param host
	 *            主机地址
	 * @param port
	 *            端口号
	 * @param handler
	 *            监听类
	 * @throws InterruptedException
	 * @throws SSLException
	 */
	public ClientBC(boolean SSL, String host, int port,
			DChandlerInterface handler) throws SSLException {
		this.host = host;
		this.port = port;
		this.SSL = SSL;
		this.handler=handler;
		init();

	}

	public ClientBC(String host, int port, DChandlerInterface handler)
			throws SSLException {
		this(false, host, port, handler);

	}

	private void init() throws SSLException {
		handler.addConnectListening(this);
		if (SSL) {
			sslCtx = SslContext
					.newClientContext(InsecureTrustManagerFactory.INSTANCE);
		} else {
			sslCtx = null;
		}
		group = new NioEventLoopGroup();

		b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						if (sslCtx != null) {
							p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
						}
						p.addLast(new ObjectEncoder(), new ObjectDecoder(
								ClassResolvers.cacheDisabled(null)), handler
								.newHandler());
					}
				});

	}

	/**
	 * 连接 阻塞模式需单独放一个线程
	 * 
	 * @throws Exception
	 * 
	 * @throws InterruptedException
	 */
	public void connect() throws Exception {

		// Start the client.
		try {
			f = b.connect(host, port).sync();
//			ServiceSerializable info = new ServiceSerializable();
//			info.setRole(ConfigConst.DISTRIBUTED_ROLE);
//			info.setTime(System.currentTimeMillis());
//			f.channel().writeAndFlush(info);
			f.channel().closeFuture().sync();// 阻塞 //连接关闭后不会立即断开，需要等上一会
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("连接失败！");
		} finally {
			group.shutdownGracefully();
		}

	}

	/**
	 * 获取管道
	 * 
	 * @return
	 */
	public Channel getChannel() {
		return f.channel();
	}
	/**
	 * 关闭，关闭进行中时将处于阻塞状态
	 * 
	 * @throws InterruptedException
	 */
	public void disConnect() {
		try {
			f.channel().close().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Shut down the event loop to terminate all threads.
			group.shutdownGracefully();
			group = null;
			sslCtx = null;
			b = null;
			f = null;
		}

	}

	/**
	 * 断线重连
	 * 
	 * @throws Exception
	 */
	public void reConnect() {
		disConnect();
		try {
			init();
			connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void connect(Channel connect) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect(Channel connect) {
		// TODO Auto-generated method stub
			reConnect();
	}

}
