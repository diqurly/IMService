package org.diqurly.handler;

import io.netty.channel.ChannelInboundHandlerAdapter;
/**
 * ����service
 * @author diqurly
 *
 */
public abstract class DhandlerInterface extends ChannelInboundHandlerAdapter{
	public abstract DhandlerInterface newHandler();
}
