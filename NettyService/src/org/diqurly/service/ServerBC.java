package org.diqurly.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
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

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import org.diqurly.handler.DhandlerInterface;

/**
 * 服务端创建 Socket service create
 * 
 * @author diqurly
 *
 */
public class ServerBC {

	private boolean SSL;
	private SslContext sslCtx;

	// Configure the server.
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private int sThreads;
	private int cThreads;
	private int port;
	private ServerBootstrap b;
	// 用于监听的类
	private DhandlerInterface handler;

	public ServerBC(int port, DhandlerInterface handler)
			throws CertificateException, SSLException {
		this(1, 0, false, port, handler);
	}

	public ServerBC(int sThreads, int cThreads, boolean SSL, int port,
			DhandlerInterface handler) throws CertificateException, SSLException {
		this.sThreads = sThreads;
		this.cThreads = cThreads;
		this.SSL = SSL;
		this.port = port;
		this.handler = handler;
		init();
	}

	private void init() throws CertificateException, SSLException {
		// Configure SSL.
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContext.newServerContext(ssc.certificate(),
					ssc.privateKey());
		} else {
			sslCtx = null;
		}
		// Configure the server.
		bossGroup = new NioEventLoopGroup(sThreads);
		workerGroup = new NioEventLoopGroup(cThreads);

		b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						if (sslCtx != null) {
							p.addLast(sslCtx.newHandler(ch.alloc()));
						}
						p.addLast(new LoggingHandler(LogLevel.INFO));
						p.addLast(new ObjectEncoder(), new ObjectDecoder(
								ClassResolvers.cacheDisabled(null)),handler.newHandler());
					}
				});

	}

	/**
	 * 服务端口开启 开启成功后线程处于阻塞模式
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		try {
			// Start the server.
			ChannelFuture f = b.bind(port).sync();
			// f.channel().close();
			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();// 阻塞
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(port + "端口开启失败");
		} finally {
			// Shut down all event loops to terminate all threads.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
