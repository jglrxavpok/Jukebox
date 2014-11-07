package org.jglrxavpok.jukebox.api.packets;

import java.io.*;

public interface IPacket
{
    void decodeFrom(DataInput input) throws IOException;

    void encodeInto(DataOutput output) throws IOException;
}
