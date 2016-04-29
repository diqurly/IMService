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
	// ���������ͨѶ���첽Channel
	AsynchronousSocketChannel clientChannel;
	JFrame mainWin = new JFrame("��������");
	JTextArea jta = new JTextArea(16, 48);
	JTextField jtf = new JTextField(40);
	JButton sendBn = new JButton("����");

	public void init() {
		mainWin.setLayout(new BorderLayout());
		jta.setEditable(false);
		mainWin.add(new JScrollPane(jta), BorderLayout.CENTER);
		JPanel jp = new JPanel();
		jp.add(jtf);
		jp.add(sendBn);

		// ������Ϣ��action,Action��ActionListener���ӽӿ�
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
				// ��������
				jtf.setText("");
			}
		};

		sendBn.addActionListener(sendAction);
		// ����Ctrl+Enter�����͡�Send������
		jtf.getInputMap().put(
				KeyStroke.getKeyStroke('\n',
						java.awt.event.InputEvent.CTRL_MASK), "send");
		// ����send����sendAction����
		jtf.getActionMap().put("send", sendAction);
		mainWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWin.add(jp, BorderLayout.SOUTH);
		mainWin.pack();
		mainWin.setVisible(true);
	}

	public void connect() throws IOException, InterruptedException,
			ExecutionException {
		// ����һ��ByteBuffer׼����ȡ����
		final ByteBuffer buff = ByteBuffer.allocate(1024);
		// ����һ����IP�̳�
		ExecutorService executor = Executors.newFixedThreadPool(80);
		// ��ָ���̳߳�������һ��asynchronousChannelGroup
		AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup
				.withThreadPool(executor);
		// ��channelGroup��Ϊ������������AsynchronousSocketChannel
		clientChannel = AsynchronousSocketChannel.open(channelGroup);
		// ��AsynchronousSocketChannel���ӵ�ָ��IP��ַ��ָ���˿�
		clientChannel.connect(new InetSocketAddress("127.0.0.1", PORT)).get();
		jta.append("----����������ӳɹ�---\n");
		buff.clear();
		clientChannel.read(buff, null,
				new CompletionHandler<Integer, Object>() {

					@Override
					public void completed(Integer result, Object attachment) {
						// TODO Auto-generated method stub
						buff.flip();
						// ��buff�е�����ת��Ϊ�ַ���
						String content = StandardCharsets.UTF_8.decode(buff)
								.toString();
						// ��ʾ�ӷ������˶�ȡ������
						jta.append("ĳ��˵��" + content + "\n");
						buff.clear();
						clientChannel.read(buff, null, this);
					}

					@Override
					public void failed(Throwable exc, Object attachment) {
						// TODO Auto-generated method stub
						System.out.println("��ȡ����ʧ�ܣ�" + exc);
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
