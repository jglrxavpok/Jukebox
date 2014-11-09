package org.jglrxavpok.android.jukebox.utils;

import java.io.*;

import org.jglrxavpok.jukebox.api.*;
import org.jglrxavpok.jukebox.api.packets.*;

public class Packets
{

    public static void encode(IPacket packet, OutputStream output) throws IOException
    {
        ByteArrayOutputStream output1 = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(output1);
        packet.encodeInto(out);
        out.flush();
        out.close();
        byte[] bytes = output1.toByteArray();
        out = new DataOutputStream(output);
        out.writeInt(PacketRegistry.getPacketId(packet.getClass()));
        out.writeInt(PacketRegistry.getPacketSide(packet.getClass()).ordinal());
        out.writeInt(bytes.length);
        out.write(bytes);
        out.flush();
    }

    public static IPacket decode(InputStream in) throws IOException
    {
        DataInputStream input = new DataInputStream(in);
        int id = input.readInt();
        EnumJukeboxEnd side = EnumJukeboxEnd.values()[input.readInt()];
        int payloadSize = input.readInt();
        byte[] buffer = new byte[payloadSize];
        input.readFully(buffer);
        IPacket packet = PacketRegistry.create(side, id);
        DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(buffer));
        packet.decodeFrom(dataIn);
        dataIn.close();
        return packet;
    }
}
