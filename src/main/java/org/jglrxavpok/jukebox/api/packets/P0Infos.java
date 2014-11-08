package org.jglrxavpok.jukebox.api.packets;

import java.io.*;

public class P0Infos implements IPacket
{

    private String playerName;

    public P0Infos()
    {
    }

    public P0Infos(String playerName)
    {
        this.playerName = playerName;
    }

    @Override
    public void decodeFrom(DataInput input) throws IOException
    {
        playerName = input.readUTF();
        System.out.println("Found player: " + playerName);
    }

    @Override
    public void encodeInto(DataOutput output) throws IOException
    {
        output.writeUTF(playerName);
    }

}
