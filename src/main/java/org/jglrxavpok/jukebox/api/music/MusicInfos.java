package org.jglrxavpok.jukebox.api.music;

public class MusicInfos
{

    private String      title;
    private String      author;
    private MusicFormat format;

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
        this.title = title;
        this.author = author;
        this.format = format;
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
