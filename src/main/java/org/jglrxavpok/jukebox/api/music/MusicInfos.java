package org.jglrxavpok.jukebox.api.music;

public class MusicInfos
{

    private String      title;
    private String      author;
    private MusicFormat format;
    private String      album;
    private String      year;

    public MusicInfos(MusicFormat format)
    {
        this("Untitled", format);
    }

    public MusicInfos(String title, MusicFormat format)
    {
        this(title, "unknown", format);
    }

    public MusicInfos(String title, String author, MusicFormat format)
    {
        this(title, author, "<unknown>", format);
    }

    public MusicInfos(String title, String author, String album, MusicFormat format)
    {
        this(title, author, album, "<undated>", format);
    }

    public MusicInfos(String title, String author, String album, String year, MusicFormat format)
    {
        if(title == null)
            title = "NO TITLE, PLEASE FIX";
        if(author == null)
            author = "NO AUTHOR, PLEASE FIX";
        if(album == null)
            album = "NO ALBUM, PLEASE FIX";
        if(year == null)
            year = "<undated>";
        if(format == null)
            throw new NullPointerException("Format can't be null!");
        this.title = title;
        this.author = author;
        this.format = format;
        this.album = album;
        this.year = year;
    }

    public String getYear()
    {
        return year;
    }

    public String getAlbum()
    {
        return album;
    }

    public MusicFormat getFormat()
    {
        return format;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAuthor()
    {
        return author;
    }
}
