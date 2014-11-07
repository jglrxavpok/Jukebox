package org.jglrxavpok.jukebox.network;

import io.netty.channel.*;

import org.jglrxavpok.jukebox.api.packets.*;

public interface INetworkHandler
{

    void handlePacket(ChannelHandlerContext ctx, IPacket packet);

    void onConnexionEstablished(ChannelHandlerContext ctx);
}
