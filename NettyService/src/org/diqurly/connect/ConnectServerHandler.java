package org.diqurly.connect;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.BlockingQueue;

import org.diqurly.handler.DhandlerInterface;
import org.diqurly.packet.Error;
import org.diqurly.packet.Packet;
import org.diqurly.packet.PacketPackage;
import org.diqurly.user.UserManage;
import org.diqurly.user.UserSerializable;

public class ConnectServerHandler extends DhandlerInterface {

	private ConnectManage<Channel> connectMange;

	private UserManage<Channel> userManage;
	private BlockingQueue<Packet> queue;

	public ConnectServerHandler(ConnectManage<Channel> connectMange,
			UserManage<Channel> userManage, BlockingQueue<Packet> queue) {
		this.connectMange = connectMange;
		this.userManage = userManage;
		this.queue = queue;
	}
/**
 * new
 * @return
 */
	public DhandlerInterface newHandler() {
		return new ConnectServerHandler(connectMange, userManage, queue);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		if (msg instanceof UserSerializable) {
			if (!userManage.login(ctx.channel(), (UserSerializable) msg))
				ctx.close();
				remove(ctx.channel());
		} else if (userManage.isLegal(ctx.channel())) {
			Packet packet = PacketPackage.packageing(msg.toString(),
					ctx.channel());
			if (packet != null) {
				if(!queue.offer(packet))
				{
					//���ʧ��
					ctx.writeAndFlush(new Error(2100).toJson());
					//��֪�ͻ��˴�����Ϣ����ʧ�ܣ���ȴ���
				}
			}
		} else {
			remove(ctx.channel());
			ctx.close();
			userManage.loginOut(ctx.channel());
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		ctx.flush();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		connectMange.addCacheCo(ctx.channel());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		remove(ctx.channel());
		ctx.close();
		userManage.loginOut(ctx.channel());
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)
			throws Exception {
		// TODO Auto-generated method stub
		super.channelWritabilityChanged(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		cause.printStackTrace();
		ctx.close();
		remove(ctx.channel());
		userManage.loginOut(ctx.channel());
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		// TODO Auto-generated method stub
		super.userEventTriggered(ctx, evt);
	}

	private void remove(Channel connect) {
		connectMange.rmCacheCo(connect);
	}

}
