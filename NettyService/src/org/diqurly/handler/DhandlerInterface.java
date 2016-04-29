package org.diqurly.handler;

import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class DhandlerInterface extends ChannelInboundHandlerAdapter{
public abstract DhandlerInterface newHandler();
}
