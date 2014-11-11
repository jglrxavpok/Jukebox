package org.jglrxavpok.jukebox.api.packets;

import java.io.*;
import java.util.*;

import org.jglrxavpok.jukebox.api.music.*;

public class P1UpdateList extends IPacket
{

    private List<MusicInfos> musics;

    public P1UpdateList()
    {
        musics = new ArrayList<MusicInfos>();
    }

    public P1UpdateList(List<MusicInfos> musics)
    {
        this.musics = musics;
    }

    @Override
    public void decodeFrom(DataInput input) throws IOException
    {
        int l = input.readInt();
        for(int i = 0; i < l; i++ )
        {
            String title = input.readUTF();
            String author = input.readUTF();
            String album = input.readUTF();
            String year = input.readUTF();
            MusicFormat format = MusicFormat.valueOf(input.readUTF());
            musics.add(new MusicInfos(title, author, album, year, format));
        }
    }

    @Override
    public void encodeInto(DataOutput output) throws IOException
    {
        output.writeInt(musics.size());
        for(MusicInfos infos : musics)
        {
            output.writeUTF(infos.getTitle());
            output.writeUTF(infos.getAuthor());
            output.writeUTF(infos.getAlbum());
            output.writeUTF(infos.getYear());
            output.writeUTF(infos.getFormat().name());
        }
    }

    public List<MusicInfos> getInfosList()
    {
        return musics;
    }

}
