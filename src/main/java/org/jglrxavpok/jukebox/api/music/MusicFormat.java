package org.jglrxavpok.jukebox.api.music;

public enum MusicFormat
{
    WAVE("wav"), OGG("ogg"), MP3("mp3"), UNKNOWN;

    private String[] extensions;

    private MusicFormat(String... extensions)
    {
        this.extensions = extensions;
    }

    public String[] extensions()
    {
        return extensions;
    }

    public static MusicFormat fromExtension(String extension)
    {
        for(MusicFormat format : values())
        {
            for(String e : format.extensions)
            {
                if(e.equals(extension))
                    return format;
            }
        }
        return UNKNOWN;
    }
}
