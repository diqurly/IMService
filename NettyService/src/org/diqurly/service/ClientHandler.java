package org.diqurly.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.BlockingQueue;

import org.diqurly.config.ConfigConst;
import org.diqurly.connect.ConnectManage;
import org.diqurly.connect.listening.ConnectListening;
import org.diqurly.handler.DChandlerInterface;
import org.diqurly.handler.DhandlerInterface;
import org.diqurly.packet.Packet;
import org.diqurly.packet.PacketPackage;
import org.diqurly.service.ServiceSerializable;

/**
 * C
 * 
 * @author diqurly
 *
 */
public class ClientHandler extends DChandlerInterface {

	private ConnectManage<Channel> connectMange;
	private BlockingQueue<Packet> queue;
	private int role;
	private String code;

	public ClientHandler(ConnectManage<Channel> connectMange,
			BlockingQueue<Packet> queue,int role,String code) {
		this.connectMange = connectMange;
		this.queue = queue;
		this.role=role;
		this.code=code;
	}

	public ClientHandler(ConnectManage<Channel> connectMange,
			BlockingQueue<Packet> queue, ConnectListening<Channel> connectListen,int role,String code) {
		this.connectMange = connectMange;
		this.queue = queue;
		this.connectListen = connectListen;
		this.role=role;
		this.code=code;
		
	}

	public void channelActive(ChannelHandlerContext ctx) {
		ServiceSerializable info = new ServiceSerializable();
		info.setTime(System.currentTimeMillis());
		info.setRole(role);
		info.setCheckCode(code);
		ctx.writeAndFlush(info);
		// System.out.println("channelActive");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.handlerRemoved(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub

		Packet packet = PacketPackage.packageing(msg.toString(), ctx.channel());
		if (packet != null) {
			queue.put(packet);
			// if(!queue.offer(packet))
			// {
			// //添加失败
			// ctx.writeAndFlush(new Error(2100).toJson());
			// //告知客户端此条消息发送失败，请等待。
			// }
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
		connectMange.addChannel(role, ctx.channel());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		connectMange.rmChannel(role);
		connectListen.disconnect(ctx.channel());
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
		// ctx.close();
		connectMange.rmChannel(ConfigConst.DISTRIBUTED_GROUP);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		// TODO Auto-generated method stub
		super.userEventTriggered(ctx, evt);
	}

	@Override
	public DhandlerInterface newHandler() {
		// TODO Auto-generated method stub
		return new ClientHandler(connectMange, queue, connectListen,role,code);
	}

}
