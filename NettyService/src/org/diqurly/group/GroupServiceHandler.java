package org.diqurly.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import org.diqurly.config.ConfigConst;
import org.diqurly.connect.ConnectManage;
import org.diqurly.handler.DhandlerInterface;
import org.diqurly.service.ServiceSerializable;
/**
 * 群消息交流
 * @author diqurly
 *
 */
public class GroupServiceHandler extends DhandlerInterface {
	

	private ConnectManage<Channel> connectMange;

	public GroupServiceHandler(ConnectManage<Channel> connectMange) {
		this.connectMange = connectMange;
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
				info.getCheckCode();//根据数据库信息进行比对。
				
				//正确
				connectMange.rmCacheCo(ctx.channel());
				
				//错误
				remove(ctx.channel());
			}else
				remove(ctx.channel());
		} else if (connectMange.isCheck(ctx.channel())) {
			//消息接收解析
			
			
		} else {
			remove(ctx.channel());
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
		connectMange.rmConnect(connect);
	}

	@Override
	public DhandlerInterface newHandler() {
		// TODO Auto-generated method stub
		return new GroupServiceHandler(connectMange);
	}
	
}
