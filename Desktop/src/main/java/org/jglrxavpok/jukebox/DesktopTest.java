package org.jglrxavpok.jukebox;

import java.io.*;
import java.net.*;

import org.jglrxavpok.jukebox.api.*;
import org.jglrxavpok.jukebox.api.packets.*;

public class DesktopTest
{

    public static void main(String[] args) throws UnknownHostException, IOException
    {
        Socket s = new Socket("localhost", IJukebox.SOCKET_PORT);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(output);
        C0Test test = new C0Test();
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
        s.close();
    }
}
