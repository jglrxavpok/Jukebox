package org.jglrxavpok.android.jukebox;

import java.io.*;
import java.net.*;
import java.util.*;

import android.graphics.*;

import org.jglrxavpok.android.jukebox.utils.*;
import org.jglrxavpok.jukebox.api.*;
import org.jglrxavpok.jukebox.api.music.*;
import org.jglrxavpok.jukebox.api.packets.*;

public class AndroidJukeboxClient implements IJukebox
{

    private ArrayList<JukeboxHost> hosts;
    private JukeboxHost            currentHost;
    private Socket                 socket;

    public AndroidJukeboxClient()
    {
        PacketRegistry.init();
        hosts = new ArrayList<JukeboxHost>();
        findAllHosts();
    }

    @Override
    public EnumJukeboxEnd getType()
    {
        return EnumJukeboxEnd.CLIENT;
    }

    public ArrayList<JukeboxHost> getHosts()
    {
        return hosts;
    }

    public void setJukeboxHost(JukeboxHost host)
    {
        this.currentHost = host;
    }

    public void addJukeboxHost(JukeboxHost host)
    {
        hosts.add(host);
        JukeboxActivity.instance.getMainPanel().addJukeboxHost(host);
    }

    public JukeboxHost getCurrentHost()
    {
        return currentHost;
    }

    public void findAllHosts()
    {
        new Thread()
        {
            public void run()
            {
                for(int i = 1; i < 254; i++ )
                {
                    final String n = "192.168.1." + i;//192.168.1.1,192.168.1.2 n so on.........to 192.168.1.255
                    new Thread()
                    {
                        public void run()
                        {
                            try
                            {
                                Socket s = new Socket();
                                s.setSoTimeout(1000);
                                InetAddress address = InetAddress.getByName(n);
                                s.connect(new InetSocketAddress(address, IJukebox.SOCKET_PORT));
                                Packets.encode(new C0Ping(), s.getOutputStream());
                                IPacket packet = Packets.decode(s.getInputStream());
                                if(!(packet instanceof P0Infos))
                                {
                                    throw new IllegalArgumentException("Received packet at launch was not a packet containing informations about the jukebox host");
                                }
                                s.close();
                                addJukeboxHost(new JukeboxHost(((P0Infos) packet).getPlayerName(), convertToBitmap(((P0Infos) packet).getImageData()), address));
                            }
                            catch(Exception e)
                            {
                                if(!(e instanceof IOException))
                                    e.printStackTrace();
                            }
                        }

                        private Bitmap convertToBitmap(byte[] imageData)
                        {
                            Bitmap bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
                            for(int x = 0; x < bitmap.getWidth(); x++ )
                            {
                                for(int y = 0; y < bitmap.getHeight(); y++ )
                                {
                                    int index = (x + y * bitmap.getWidth()) * 4;
                                    int alpha = imageData[index];
                                    int red = imageData[index + 1];
                                    int green = imageData[index + 2];
                                    int blue = imageData[index + 3];
                                    int color = (alpha << 24) | (red << 16) | (green << 8) | blue;
                                    bitmap.setPixel(x, y, color);
                                }
                            }
                            return bitmap;
                        }
                    }.start();
                }
            }
        }.start();
    }

    public void sendMusic(Music music)
    {
        if(currentHost == null)
            throw new IllegalStateException("Tried to send music file while not connected");
        try
        {
            if(socket == null)
            {
                socket = new Socket(currentHost.getAddress(), SOCKET_PORT);
            }
            Packets.encode(new C1SendMusic(music), socket.getOutputStream());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
