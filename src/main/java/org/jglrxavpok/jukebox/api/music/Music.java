package org.jglrxavpok.jukebox.api.music;

public class Music
{

    private byte[]     fileData;
    private MusicInfos infos;

    public Music(byte[] fileData, MusicInfos infos)
    {
        this.infos = infos;
        this.fileData = fileData;
    }

    public MusicInfos getInfos()
    {
        return infos;
    }

    public byte[] getFileData()
    {
        return fileData;
    }
}
