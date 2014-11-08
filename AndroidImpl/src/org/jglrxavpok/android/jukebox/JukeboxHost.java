package org.jglrxavpok.android.jukebox;

import java.net.*;

public class JukeboxHost
{

    private String      name;
    private InetAddress address;

    public JukeboxHost(String name, InetAddress address)
    {
        this.name = name;
        this.address = address;
    }

    public String getName()
    {
        return name;
    }

    public InetAddress getAddress()
    {
        return address;
    }
}
