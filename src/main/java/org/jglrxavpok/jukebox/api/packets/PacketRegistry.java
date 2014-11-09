package org.jglrxavpok.jukebox.api.packets;

import java.util.*;

import org.jglrxavpok.jukebox.api.*;

public class PacketRegistry
{

    private static HashMap<EnumJukeboxEnd, HashMap<Integer, Class<? extends IPacket>>> packets;

    private static HashMap<Class<? extends IPacket>, EnumJukeboxEnd>                   sides;
    private static HashMap<Class<? extends IPacket>, Integer>                          ids;

    public static void init()
    {
        sides = new HashMap<Class<? extends IPacket>, EnumJukeboxEnd>();
        ids = new HashMap<Class<? extends IPacket>, Integer>();
        packets = new HashMap<EnumJukeboxEnd, HashMap<Integer, Class<? extends IPacket>>>();
        packets.put(EnumJukeboxEnd.CLIENT, new HashMap<Integer, Class<? extends IPacket>>());
        packets.put(EnumJukeboxEnd.PLAYER, new HashMap<Integer, Class<? extends IPacket>>());

        registerPacket(EnumJukeboxEnd.CLIENT, 0, C0Ping.class);
        registerPacket(EnumJukeboxEnd.CLIENT, 1, C1SendMusic.class);

        registerPacket(EnumJukeboxEnd.PLAYER, 0, P0Infos.class);
    }

    public static int getPacketId(Class<? extends IPacket> packet)
    {
        return ids.get(packet);
    }

    public static EnumJukeboxEnd getPacketSide(Class<? extends IPacket> packet)
    {
        return sides.get(packet);
    }

    public static void registerPacket(EnumJukeboxEnd senderSide, int id, Class<? extends IPacket> packetClass)
    {
        packets.get(senderSide).put(id, packetClass);

        sides.put(packetClass, senderSide);
        ids.put(packetClass, id);
    }

    public static IPacket create(EnumJukeboxEnd side, int id)
    {
        Class<? extends IPacket> packet = packets.get(side).get(id);
        if(packet == null)
        {
            System.err.println("Unknown packet id: " + id);
        }
        try
        {
            return packet.newInstance();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
