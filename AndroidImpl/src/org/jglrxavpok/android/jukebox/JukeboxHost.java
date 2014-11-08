package org.jglrxavpok.android.jukebox;

import java.net.*;

import android.graphics.*;

public class JukeboxHost
{

    private String      name;
    private InetAddress address;
    private Bitmap      icon;

    public JukeboxHost(String name, Bitmap icon, InetAddress address)
    {
        this.icon = icon;
        this.name = name;
        this.address = address;
    }

    public Bitmap getIcon()
    {
        return icon;
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
