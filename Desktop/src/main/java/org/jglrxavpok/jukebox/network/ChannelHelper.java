package org.jglrxavpok.jukebox.network;

import io.netty.channel.*;
import io.netty.util.concurrent.*;

import java.io.*;

import org.jglrxavpok.jukebox.api.*;
import org.jglrxavpok.jukebox.api.packets.*;

public class ChannelHelper
{

    public static void writeAndFlush(IPacket packet, ChannelHandlerContext ctx) throws IOException
    {
        writeAndFlush(packet, ctx.channel());
    }

    public static void writeAndFlush(IPacket packet, Channel channel) throws IOException
    {
        int id = PacketRegistry.getPacketId(packet.getClass());
        EnumJukeboxEnd side = PacketRegistry.getPacketSide(packet.getClass());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);
        packet.encodeInto(dataOut);
        out.flush();
        out.close();
        NettyPacket nettyPacket = new NettyPacket(id, out.toByteArray(), side);
        channel.writeAndFlush(nettyPacket).addListener(new GenericFutureListener<Future<? super Void>>()
        {

            @Override
            public void operationComplete(Future<? super Void> future) throws Exception
            {
                if(!future.isSuccess())
                {
                    future.cause().printStackTrace();
                }
            }
        });
    }
}
