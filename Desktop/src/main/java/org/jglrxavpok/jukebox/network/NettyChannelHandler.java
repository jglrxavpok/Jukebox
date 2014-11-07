package org.jglrxavpok.jukebox.network;

import io.netty.channel.*;

public class NettyChannelHandler extends ChannelHandler
{

    public NettyChannelHandler()
    {
        super(new ServerNetHandler());
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx)
    {
        System.out.println("new client");
        // TODO ChannelHelper.writeAndFlush(new S0ConnectionAccepted(), ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }
}
