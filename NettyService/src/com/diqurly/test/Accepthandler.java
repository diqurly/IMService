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
		// ��¼�½�����Channel
		AIOServer.channelList.add(sc);
		// ׼�����տͻ��˵���һ������
		serverChannel.accept(null, this);
		sc.read(buff, null, new CompletionHandler<Integer, Object>() {

			@Override
			public void completed(Integer result, Object attachment) {
				// TODO Auto-generated method stub
				buff.flip();
				// ��buff�е�����ת��Ϊ�ַ���
				String content = StandardCharsets.UTF_8.decode(buff).toString();
				// ����ÿ��Channel�����յ�����Ϣд���Channel��
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
				System.out.println("��ȡ����ʧ�ܣ�" + exc);
				AIOServer.channelList.remove(sc);
			}
		});
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		// TODO Auto-generated method stub
		System.out.println("����ʧ�ܣ�"+exc);
	}

}
