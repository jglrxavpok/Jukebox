package org.jglrxavpok.jukebox.network;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.handler.codec.*;

public class PacketEncoder extends MessageToByteEncoder<NettyPacket>
{

    @Override
    protected void encode(ChannelHandlerContext arg0, NettyPacket arg1, ByteBuf arg2) throws Exception
    {
        arg2.writeInt(arg1.id);
        arg2.writeInt(arg1.side.ordinal());
        arg2.writeInt(arg1.payload.length);
        arg2.writeBytes(arg1.payload);
    }
}
