package org.diqurly.group;

import java.util.concurrent.BlockingQueue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import org.diqurly.config.ConfigConst;
import org.diqurly.connect.ConnectManage;
import org.diqurly.handler.DhandlerInterface;
import org.diqurly.packet.Error;
import org.diqurly.packet.Packet;
import org.diqurly.packet.PacketPackage;
import org.diqurly.service.ServiceSerializable;
/**
 * 与群服务器数据交流
 * 
 * {@value
 * 作用：只需要被动的接收群服务器传过来的数据即可，不需要主动的与群服务器进行交流（发送心跳？判断是否断开）。
 * 只需要一个群连接管理即可（HashSet<?>）
 * }
 * @author diqurly
 *
 */
public class GroupServiceHandler extends DhandlerInterface {
	

	private ConnectManage<Channel> connectMange;
	private BlockingQueue<Packet> queue;

	public GroupServiceHandler(ConnectManage<Channel> connectMange, BlockingQueue<Packet> queue) {
		this.connectMange = connectMange;
		this.queue=queue;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		if (msg instanceof ServiceSerializable) {
			ServiceSerializable info=(ServiceSerializable)msg;
			if(info.getRole()==ConfigConst.DISTRIBUTED_GROUP)
			{
				//校验
				if(ConfigConst.GROUP_CHECK_CODE.equals(info.getCheckCode()))//根据数据库信息进行比对。
				{
					//正确
					connectMange.addGroupConnect(ctx.channel());
					connectMange.rmCacheCo(ctx.channel());
				}else
				{
					//错误
					remove(ctx.channel());
				}				
			}else
				remove(ctx.channel());
		} else if (connectMange.existGroupConnect(ctx.channel())) {
			//消息接收解析
			
			//1群消息的转发
			
			//2心跳回执
			

			Packet packet = PacketPackage.packageing(msg.toString(),
					ctx.channel());
			if (packet != null) {
				if(!queue.offer(packet))
				{
					//添加失败
					ctx.writeAndFlush(new Error(2100).toJson());
					//告知客户端此条消息发送失败，请等待。
				}
			}
		} else {
			remove(ctx.channel());
		}
		//为什么要使用这句，网上说法是释放msg资源，面对内存爆满。
				ReferenceCountUtil.release(msg);
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
	//	ctx.close();
		remove(ctx.channel());
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		// TODO Auto-generated method stub
		super.userEventTriggered(ctx, evt);
	}

	
	private void remove(Channel connect)
	{
		connectMange.rmCacheCo(connect);
		connectMange.removeGroupConnect(connect);
	}

	@Override
	public DhandlerInterface newHandler() {
		// TODO Auto-generated method stub
		return new GroupServiceHandler(connectMange,queue);
	}
	
}
