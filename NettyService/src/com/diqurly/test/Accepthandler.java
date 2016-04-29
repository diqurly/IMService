package com.diqurly.test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class Accepthandler implements
		CompletionHandler<AsynchronousSocketChannel, Object> {
	private AsynchronousServerSocketChannel serverChannel;
	ByteBuffer buff = ByteBuffer.allocate(1024);

	public Accepthandler(AsynchronousServerSocketChannel serverChannel) {
		// TODO Auto-generated constructor stub
		this.serverChannel = serverChannel;
	}

	@Override
	public void completed(final AsynchronousSocketChannel sc, Object attachment) {
		// TODO Auto-generated method stub
		// 记录新进来的Channel
		AIOServer.channelList.add(sc);
		// 准备接收客户端的下一次连接
		serverChannel.accept(null, this);
		sc.read(buff, null, new CompletionHandler<Integer, Object>() {

			@Override
			public void completed(Integer result, Object attachment) {
				// TODO Auto-generated method stub
				buff.flip();
				// 将buff中的内容转化为字符串
				String content = StandardCharsets.UTF_8.decode(buff).toString();
				// 遍历每个Channel，将收到的信息写入各Channel中
				for (AsynchronousSocketChannel c : AIOServer.channelList) {
					try {
						c.write(ByteBuffer.wrap(content
								.getBytes(AIOServer.UTF_8))).get();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				buff.clear();
				sc.read(buff, null, this);
			}

			@Override
			public void failed(Throwable exc, Object attachment) {
				// TODO Auto-generated method stub
				System.out.println("读取数据失败：" + exc);
				AIOServer.channelList.remove(sc);
			}
		});
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		// TODO Auto-generated method stub
		System.out.println("连接失败："+exc);
	}

}
