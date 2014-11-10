package org.jglrxavpok.jukebox.network;

import io.netty.channel.*;

import java.io.*;

import org.jglrxavpok.jukebox.*;
import org.jglrxavpok.jukebox.api.music.*;
import org.jglrxavpok.jukebox.api.packets.*;

public class ServerNetHandler implements INetworkHandler
{

    private DesktopJukebox jukebox;

    public ServerNetHandler(DesktopJukebox desktopJukebox)
    {
        this.jukebox = desktopJukebox;
    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket packet)
    {
        if(packet instanceof C0Ping)
        {
            try
            {
                ChannelHelper.writeAndFlush(new P0Infos(jukebox.getName(), jukebox.getImageData()), ctx);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        else if(packet instanceof C1SendMusic)
        {
            C1SendMusic musicPacket = (C1SendMusic) packet;
            Music music = musicPacket.getMusic();
            jukebox.addToQueue(music);
        }
    }

    @Override
    public void onConnexionEstablished(ChannelHandlerContext ctx)
    {
        ;
    }

}
