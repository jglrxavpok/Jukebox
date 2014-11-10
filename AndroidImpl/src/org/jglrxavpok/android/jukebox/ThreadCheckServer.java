package org.jglrxavpok.android.jukebox;

import java.io.*;
import java.net.*;

import android.util.*;
import android.widget.*;

import org.jglrxavpok.android.jukebox.utils.*;
import org.jglrxavpok.jukebox.api.music.*;
import org.jglrxavpok.jukebox.api.packets.*;

public class ThreadCheckServer extends Thread
{

    private Socket socket;

    public ThreadCheckServer(Socket socket)
    {
        super("Server input thred");
        this.socket = socket;
    }

    public void run()
    {
        while(!socket.isClosed())
        {
            IPacket packet;
            try
            {
                packet = Packets.decode(socket.getInputStream());
                if(packet instanceof P1UpdateList)
                {
                    final P1UpdateList updateList = (P1UpdateList) packet;
                    JukeboxActivity.instance.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ListView jukeboxList = (ListView) JukeboxActivity.instance.findViewById(R.id.jukeboxPlaylist);
                            JukeboxPlaylistAdapter adapter = (JukeboxPlaylistAdapter) jukeboxList.getAdapter();
                            adapter.clear();
                            for(MusicInfos infos : updateList.getInfosList())
                            {
                                adapter.add(infos);
                            }
                            adapter.notifyDataSetChanged();
                            Log.d(JukeboxActivity.TAG, "Updated list: " + updateList.getInfosList().size() + " items ");
                        }
                    });
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
                break;
            }
        }
        Log.e(JukeboxActivity.TAG, "Stopped server checking");
    }
}
