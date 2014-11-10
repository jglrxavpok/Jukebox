package org.jglrxavpok.jukebox.api.packets;

import java.io.*;
import java.util.zip.*;

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
        GZIPInputStream compressedInput = new GZIPInputStream(new ByteArrayInputStream(fileData));
        byte[] buffer = new byte[65565];
        ByteArrayOutputStream uncompressedOutput = new ByteArrayOutputStream();
        int i;
        while((i = compressedInput.read(buffer)) != -1)
        {
            uncompressedOutput.write(buffer, 0, i);
        }
        uncompressedOutput.flush();
        uncompressedOutput.close();
        compressedInput.close();
        music = new Music(uncompressedOutput.toByteArray(), new MusicInfos(title, author, format));
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream out = new GZIPOutputStream(baos);
        out.write(music.getFileData());
        out.flush();
        out.close();
        writeBytes(output, baos.toByteArray());
    }

    public Music getMusic()
    {
        return music;
    }

}
