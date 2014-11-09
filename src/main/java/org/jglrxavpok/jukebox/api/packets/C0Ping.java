package org.jglrxavpok.jukebox.api.packets;

import java.io.*;

public class C0Ping extends IPacket
{

    @Override
    public void decodeFrom(DataInput input) throws IOException
    {
        System.out.println("Found: " + input.readInt());
    }

    @Override
    public void encodeInto(DataOutput output) throws IOException
    {
        output.writeInt(10);
    }

}
