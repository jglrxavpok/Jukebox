package org.jglrxavpok.jukebox.api.packets;

import java.io.*;

public class P0Infos extends IPacket
{

    private String playerName;
    private byte[] imageData;

    public P0Infos()
    {
    }

    public P0Infos(String playerName, byte[] imageData)
    {
        this.playerName = playerName;
        this.imageData = imageData;
    }

    @Override
    public void decodeFrom(DataInput input) throws IOException
    {
        playerName = input.readUTF();
        System.out.println("Found player: " + playerName);
        imageData = new byte[input.readInt()];
        input.readFully(imageData);
    }

    @Override
    public void encodeInto(DataOutput output) throws IOException
    {
        output.writeUTF(playerName);
        output.writeInt(imageData.length);
        output.write(imageData);
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public byte[] getImageData()
    {
        return imageData;
    }

}
