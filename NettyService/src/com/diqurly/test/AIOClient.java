package com.diqurly.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class AIOClient {
	final static String UTF_8 = "utf-8";
	final static int PORT = 30000;
	// 与服务器端通讯的异步Channel
	AsynchronousSocketChannel clientChannel;
	JFrame mainWin = new JFrame("多人聊天");
	JTextArea jta = new JTextArea(16, 48);
	JTextField jtf = new JTextField(40);
	JButton sendBn = new JButton("发送");

	public void init() {
		mainWin.setLayout(new BorderLayout());
		jta.setEditable(false);
		mainWin.add(new JScrollPane(jta), BorderLayout.CENTER);
		JPanel jp = new JPanel();
		jp.add(jtf);
		jp.add(sendBn);

		// 发送消息的action,Action是ActionListener的子接口
		Action sendAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String content = jtf.getText();
				if (content.trim().length() > 0) {
					try {
						clientChannel
								.write(ByteBuffer.wrap(content.trim().getBytes(
										UTF_8))).get();
					} catch (UnsupportedEncodingException
							| InterruptedException | ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				// 清空输出框
				jtf.setText("");
			}
		};

		sendBn.addActionListener(sendAction);
		// 将“Ctrl+Enter”键和“Send”关联
		jtf.getInputMap().put(
				KeyStroke.getKeyStroke('\n',
						java.awt.event.InputEvent.CTRL_MASK), "send");
		// 将“send”和sendAction关联
		jtf.getActionMap().put("send", sendAction);
		mainWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWin.add(jp, BorderLayout.SOUTH);
		mainWin.pack();
		mainWin.setVisible(true);
	}

	public void connect() throws IOException, InterruptedException,
			ExecutionException {
		// 定义一个ByteBuffer准备读取数据
		final ByteBuffer buff = ByteBuffer.allocate(1024);
		// 创建一个线IP程池
		ExecutorService executor = Executors.newFixedThreadPool(80);
		// 以指定线程池来创建一个asynchronousChannelGroup
		AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup
				.withThreadPool(executor);
		// 以channelGroup作为管理器来创建AsynchronousSocketChannel
		clientChannel = AsynchronousSocketChannel.open(channelGroup);
		// 让AsynchronousSocketChannel连接到指定IP地址，指定端口
		clientChannel.connect(new InetSocketAddress("127.0.0.1", PORT)).get();
		jta.append("----与服务器连接成功---\n");
		buff.clear();
		clientChannel.read(buff, null,
				new CompletionHandler<Integer, Object>() {

					@Override
					public void completed(Integer result, Object attachment) {
						// TODO Auto-generated method stub
						buff.flip();
						// 将buff中的内容转换为字符串
						String content = StandardCharsets.UTF_8.decode(buff)
								.toString();
						// 显示从服务器端读取的数据
						jta.append("某人说：" + content + "\n");
						buff.clear();
						clientChannel.read(buff, null, this);
					}

					@Override
					public void failed(Throwable exc, Object attachment) {
						// TODO Auto-generated method stub
						System.out.println("读取数据失败：" + exc);
					}
				});
	}

	public static void main(String[] args) throws IOException,
			InterruptedException, ExecutionException {
		AIOClient client = new AIOClient();
		client.init();
		client.connect();
	}

}
