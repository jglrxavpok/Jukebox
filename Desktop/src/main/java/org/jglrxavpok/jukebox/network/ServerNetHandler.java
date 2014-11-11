package org.jglrxavpok.jukebox.network;

import io.netty.channel.*;

import java.io.*;

import com.mpatric.mp3agic.*;

import org.jglrxavpok.jukebox.*;
import org.jglrxavpok.jukebox.api.music.*;
import org.jglrxavpok.jukebox.api.packets.*;

public class ServerNetHandler implements INetworkHandler
{

    private DesktopJukebox jukebox;

    public ServerNetHandler(DesktopJukebox desktopJukebox)
    {
        this.jukebox = desktopJukebox;
    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket packet)
    {
        if(packet instanceof C0Ping)
        {
            try
            {
                ChannelHelper.writeAndFlush(new P0Infos(jukebox.getName(), jukebox.getImageData()), ctx);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        else if(packet instanceof C1SendMusic)
        {
            C1SendMusic musicPacket = (C1SendMusic) packet;
            Music music = musicPacket.getMusic();
            File cacheFile = new File(jukebox.getCacheDir(), System.currentTimeMillis() + "." + music.getInfos().getFormat().extensions()[0].toLowerCase());
            try
            {
                cacheFile.createNewFile();
                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(cacheFile));
                output.write(music.getFileData());
                output.flush();
                output.close();
                String title = music.getInfos().getTitle();
                String author = music.getInfos().getAuthor();
                String album = music.getInfos().getAlbum();
                String year = music.getInfos().getYear();
                if(music.getInfos().getFormat() == MusicFormat.MP3)
                {
                    Mp3File mp3File = new Mp3File(cacheFile.getAbsolutePath());
                    if(mp3File.hasId3v1Tag())
                    {
                        ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                        author = id3v1Tag.getArtist();
                        title = id3v1Tag.getTitle();
                        album = id3v1Tag.getAlbum();
                        year = id3v1Tag.getYear();
                        System.out.println("Track: " + id3v1Tag.getTrack());
                        System.out.println("Artist: " + id3v1Tag.getArtist());
                        System.out.println("Title: " + id3v1Tag.getTitle());
                        System.out.println("Album: " + id3v1Tag.getAlbum());
                        System.out.println("Year: " + id3v1Tag.getYear());
                        System.out.println("Genre: " + id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
                        System.out.println("Comment: " + id3v1Tag.getComment());
                    }
                    else if(mp3File.hasId3v2Tag())
                    {
                        ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                        author = id3v2Tag.getArtist();
                        title = id3v2Tag.getTitle();
                        album = id3v2Tag.getAlbum();
                        year = id3v2Tag.getYear();
                        System.out.println("Track: " + id3v2Tag.getTrack());
                        System.out.println("Artist: " + id3v2Tag.getArtist());
                        System.out.println("Title: " + id3v2Tag.getTitle());
                        System.out.println("Album: " + id3v2Tag.getAlbum());
                        System.out.println("Year: " + id3v2Tag.getYear());
                        System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
                        System.out.println("Comment: " + id3v2Tag.getComment());
                    }
                }
                if(title == null && author == null && year == null && album == null)
                    ;
                else
                    music.setInfos(new MusicInfos(title, author, album, year, MusicFormat.MP3));
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            catch(UnsupportedTagException e)
            {
                e.printStackTrace();
            }
            catch(InvalidDataException e)
            {
                e.printStackTrace();
            }
            //            cacheFile.delete();
            jukebox.addToQueue(music);
        }
    }

    @Override
    public void onConnexionEstablished(ChannelHandlerContext ctx)
    {
        ;
    }

}
