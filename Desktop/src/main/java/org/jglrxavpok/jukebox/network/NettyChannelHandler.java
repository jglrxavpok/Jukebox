package org.jglrxavpok.jukebox.network;

import io.netty.channel.*;

import org.jglrxavpok.jukebox.*;

public class NettyChannelHandler extends ChannelHandler
{

    private DesktopJukebox jukebox;

    public NettyChannelHandler(DesktopJukebox desktopJukebox)
    {
        super(new ServerNetHandler(desktopJukebox));
        this.jukebox = desktopJukebox;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx)
    {
        System.out.println("new client");
        jukebox.onConnection(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }
}
