package org.jglrxavpok.jukebox.network;

import io.netty.channel.*;

import java.io.*;

import org.jglrxavpok.jukebox.api.packets.*;

public abstract class ChannelHandler extends ChannelInboundHandlerAdapter
{

    protected INetworkHandler netHandler;

    public ChannelHandler(INetworkHandler netHandler)
    {
        this.netHandler = netHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        NettyPacket m = (NettyPacket) msg;
        try
        {
            IPacket packet = PacketRegistry.create(m.getSide(), m.getID());
            packet.decodeFrom(new DataInputStream(new ByteArrayInputStream(m.payload)));
            netHandler.handlePacket(ctx, packet);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void channelActive(ChannelHandlerContext ctx)
    {
        netHandler.onConnexionEstablished(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }

}
