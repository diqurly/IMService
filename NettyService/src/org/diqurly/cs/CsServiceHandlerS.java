package org.diqurly.cs;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.BlockingQueue;

import org.diqurly.config.ConfigConst;
import org.diqurly.connect.ConnectManage;
import org.diqurly.handler.DhandlerInterface;
import org.diqurly.packet.Error;
import org.diqurly.packet.Packet;
import org.diqurly.packet.PacketPackage;
import org.diqurly.service.ServiceSerializable;
/**
 * CS���������ӡ�״̬������
 * @author diqurly
 *
 */
public class CsServiceHandlerS extends DhandlerInterface{

	private ConnectManage<Channel> connectMange;
	private BlockingQueue<Packet> queue;

	public CsServiceHandlerS(ConnectManage<Channel> connectMange, BlockingQueue<Packet> queue) {
		this.connectMange = connectMange;
		this.queue=queue;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		if (msg instanceof ServiceSerializable) {
			ServiceSerializable info=(ServiceSerializable)msg;
			if(info.getRole()==ConfigConst.DISTRIBUTED_CS)
			{
				//У��
				if(ConfigConst.CS_CHECK_CODE.equals(info.getCheckCode()))//�������ݿ���Ϣ���бȶԡ�
				{
					//��ȷ
					connectMange.addGroupConnect(ctx.channel());
					connectMange.rmCacheCo(ctx.channel());
				}else
				{
					//����
					remove(ctx.channel());
				}				
			}else
				remove(ctx.channel());
		} else if (connectMange.existGroupConnect(ctx.channel())) {
			//��Ϣ���ս���
			
			//1Ⱥ��Ϣ��ת��
			
			//2������ִ
			

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
		connectMange.removeGroupConnect(connect);
	}

	@Override
	public DhandlerInterface newHandler() {
		// TODO Auto-generated method stub
		return new CsServiceHandlerS(connectMange,queue);
	}

}
