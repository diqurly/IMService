package org.diqurly.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
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
import org.diqurly.user.UserSerializable;

/**
 * �ֲ� ����Ⱥ������
 * 
 * @author diqurly
 *
 */
public class ClientBC {

	private String host;
	private int port;
	private boolean SSL;
	private SslContext sslCtx;
	private Bootstrap b;
	private ChannelFuture f;
	private EventLoopGroup group;
	private ChannelHandler handler;

	/**
	 * 
	 * @param SSL
	 *            �Ƿ���ssl
	 * @param host
	 *            ������ַ
	 * @param port
	 *            �˿ں�
	 * @param handler
	 *            ������
	 * @throws InterruptedException
	 * @throws SSLException
	 */
	public ClientBC(boolean SSL, String host, int port, ChannelHandler handler)
			throws SSLException {
		this.host = host;
		this.port = port;
		this.SSL = SSL;
		init();

	}

	public ClientBC(String host, int port, ChannelHandler handler)
			throws SSLException {
		this(false, host, port, handler);

	}

	private void init() throws SSLException {
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
								ClassResolvers.cacheDisabled(null)), handler);
					}
				});

	}

	/**
	 * ���� ����ģʽ�赥����һ���߳�
	 * 
	 * @throws Exception
	 * 
	 * @throws InterruptedException
	 */
	public void connect() throws Exception {

		// Start the client.
		try {
			f = b.connect(host, port).sync();
			ServiceSerializable info = new ServiceSerializable();
			info.setRole(ConfigConst.DISTRIBUTED_ROLE);
			info.setTime(System.currentTimeMillis());
			f.channel().writeAndFlush(info);
			f.channel().closeFuture().sync();// ����  //���ӹرպ󲻻������Ͽ�����Ҫ����һ��
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("����ʧ�ܣ�");
		} finally {
			group.shutdownGracefully();
		}

	}

	/**
	 * �رգ��رս�����ʱ����������״̬
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
		}

	}

	/**
	 * ��������
	 * 
	 * @throws Exception
	 */
	public void reConnect() throws Exception {
		try {
			f.channel().close().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Shut down the event loop to terminate all threads.
			group.shutdownGracefully();
		}
		try {
			init();
			connect();
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
