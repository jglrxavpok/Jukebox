package org.jglrxavpok.jukebox.api.packets;

import java.io.*;

public abstract class IPacket
{
    public abstract void decodeFrom(DataInput input) throws IOException;

    public abstract void encodeInto(DataOutput output) throws IOException;

    public void writeBytes(DataOutput output, byte[] bytes) throws IOException
    {
        output.writeInt(bytes.length);
        output.write(bytes);
    }

    public byte[] readBytes(DataInput input) throws IOException
    {
        byte[] buffer = new byte[input.readInt()];
        input.readFully(buffer);
        return buffer;
    }
}
