package org.jglrxavpok.jukebox.network;

import org.jglrxavpok.jukebox.api.*;

public class NettyPacket
{

    byte[]         payload;
    int            id;
    EnumJukeboxEnd side;

    NettyPacket()
    {

    }

    public NettyPacket(int id, byte[] payload, EnumJukeboxEnd side)
    {
        this.side = side;
        this.id = id;
        this.payload = payload;
    }

    public int getID()
    {
        return id;
    }

    public byte[] getPayload()
    {
        return payload;
    }

    public EnumJukeboxEnd getSide()
    {
        return side;
    }
}
