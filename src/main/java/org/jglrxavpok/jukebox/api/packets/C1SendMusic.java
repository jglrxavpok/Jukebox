package org.jglrxavpok.jukebox.api.packets;

import java.io.*;

import org.jglrxavpok.jukebox.api.music.*;

public class C1SendMusic extends IPacket
{

    private Music music;

    public C1SendMusic()
    {
    }

    public C1SendMusic(Music music)
    {
        this.music = music;
    }

    @Override
    public void decodeFrom(DataInput input) throws IOException
    {
        String title = input.readUTF();
        String author = input.readUTF();
        MusicFormat format = MusicFormat.valueOf(input.readUTF());
        byte[] fileData = readBytes(input);
        music = new Music(fileData, new MusicInfos(title, author, format));
    }

    @Override
    public void encodeInto(DataOutput output) throws IOException
    {
        String title = music.getInfos().getTitle();
        String author = music.getInfos().getAuthor();
        String format = music.getInfos().getFormat().name();
        output.writeUTF(title);
        output.writeUTF(author);
        output.writeUTF(format);
        writeBytes(output, music.getFileData());
    }

}
