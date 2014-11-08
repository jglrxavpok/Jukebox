package org.jglrxavpok.jukebox.api.packets;

import java.io.*;

public class P0Infos implements IPacket
{

    private String playerName;
    private String imageData;

    public P0Infos()
    {
    }

    public P0Infos(String playerName, String imageDataAsBase64)
    {
        this.playerName = playerName;
        this.imageData = imageDataAsBase64;
    }

    @Override
    public void decodeFrom(DataInput input) throws IOException
    {
        playerName = input.readUTF();
        System.out.println("Found player: " + playerName);
        byte[] buffer = new byte[input.readInt()];
        input.readFully(buffer);
        imageData = new String(buffer, "UTF-8");
    }

    @Override
    public void encodeInto(DataOutput output) throws IOException
    {
        output.writeUTF(playerName);
        byte[] bytes = imageData.getBytes();
        output.writeInt(bytes.length);
        output.write(bytes);
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public String getImageDataAsBase64()
    {
        return imageData;
    }

}
