package com.diqurly.test;

import org.diqurly.user.UserSerializable;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HelloWorldServerHandler extends ChannelInboundHandlerAdapter{	
	
	
	
	  @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		  if(msg instanceof UserSerializable)
		  {
			  UserSerializable	info=(UserSerializable) msg;
				System.out.println(info.getCity());
				System.out.println(info.getUserID());
				System.out.println(info.getDevice());
				System.out.println(info.getConnectType());
				System.out.println(info.getSerialversionuid());
		  }
	      ctx.channel().write("server write msg:"+msg); 
		//  ctx.write("server write msg:"+msg);
	      //  System.out.println("channelRead:"+"=");
	     //   System.out.println(ctx.channel().id().asLongText()+"----"+ctx.channel().id().asShortText());
	    }
	 
	    @Override
	    public void channelReadComplete(ChannelHandlerContext ctx) {
	        ctx.flush();
	        System.out.println("channelReadComplete");
	    }
	 
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	        // Close the connection when an exception is raised.
	        cause.printStackTrace();
	        ctx.close();
	        System.out.println("exceptionCaught"+cause.getMessage());
	    }

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			// TODO Auto-generated method stub
			super.channelActive(ctx);
			System.out.println("channelActive");
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			// TODO Auto-generated method stub
			super.channelInactive(ctx);
			System.out.println("channelInactive");
		}

		@Override
		public void channelRegistered(ChannelHandlerContext ctx)
				throws Exception {
			// TODO Auto-generated method stub
			
			super.channelRegistered(ctx);
			System.out.println("channelRegistered");
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx)
				throws Exception {
			// TODO Auto-generated method stub
			super.channelUnregistered(ctx);
			System.out.println("channelUnregistered");
		}

		@Override
		public void channelWritabilityChanged(ChannelHandlerContext ctx)
				throws Exception {
			// TODO Auto-generated method stub
			super.channelWritabilityChanged(ctx);
			System.out.println("channelWritabilityChanged");
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
				throws Exception {
			// TODO Auto-generated method stub
			super.userEventTriggered(ctx, evt);
			System.out.println("userEventTriggered");
		}
	    
}
