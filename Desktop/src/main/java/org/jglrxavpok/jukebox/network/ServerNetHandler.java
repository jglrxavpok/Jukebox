package org.jglrxavpok.jukebox.network;

import io.netty.channel.*;

import java.io.*;

import org.jglrxavpok.jukebox.api.packets.*;

public class ServerNetHandler implements INetworkHandler
{

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket packet)
    {
        if(packet instanceof C0Ping)
        {
            try
            {
                ChannelHelper.writeAndFlush(new P0Infos("TEST-PLAYER"), ctx);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnexionEstablished(ChannelHandlerContext ctx)
    {
        ;
    }

}
