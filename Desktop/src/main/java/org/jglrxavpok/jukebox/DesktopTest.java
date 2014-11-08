package org.jglrxavpok.jukebox;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jglrxavpok.jukebox.api.*;
import org.jglrxavpok.jukebox.api.packets.*;

public class DesktopTest
{

    public static void main(String[] args) throws UnknownHostException, IOException
    {
        PacketRegistry.init();
        for(InetAddress addr : ipAdresses())
        {
            try
            {
                Socket s = new Socket(addr, IJukebox.SOCKET_PORT);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(output);
                C0Ping test = new C0Ping();
                test.encodeInto(out);
                out.flush();
                out.close();
                byte[] bytes = output.toByteArray();
                out = new DataOutputStream(s.getOutputStream());
                out.writeInt(0);
                out.writeInt(EnumJukeboxEnd.CLIENT.ordinal());
                out.writeInt(bytes.length);
                out.write(bytes);
                out.flush();
                DataInputStream in = new DataInputStream(s.getInputStream());
                int id = in.readInt();
                int type = in.readInt();
                int payloadSize = in.readInt();
                byte[] payload = new byte[payloadSize];
                in.readFully(payload);
                IPacket packet = PacketRegistry.create(EnumJukeboxEnd.values()[type], id);
                DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload));
                packet.decodeFrom(input);
                input.close();
                out.close();
                s.close();
                System.out.println("YEAH!");
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<InetAddress> ipAdresses() throws SocketException
    {
        ArrayList<InetAddress> list = new ArrayList<InetAddress>();
        for(final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();)
        {
            final NetworkInterface cur = interfaces.nextElement();

            if(cur.isLoopback())
            {
                continue;
            }

            for(final InterfaceAddress addr : cur.getInterfaceAddresses())
            {
                final InetAddress inetAddr = addr.getAddress();

                if(!(inetAddr instanceof Inet4Address))
                {
                    continue;
                }

                if(inetAddr.isLoopbackAddress())
                    continue;

                System.out.println(
                        "  address: " + inetAddr.getHostAddress() +
                                "/" + addr.getNetworkPrefixLength()
                        );

                System.out.println(
                        "  broadcast address: " +
                                addr.getBroadcast().getHostAddress()
                        );
                list.add(inetAddr);
            }
        }
        return list;
    }
}
