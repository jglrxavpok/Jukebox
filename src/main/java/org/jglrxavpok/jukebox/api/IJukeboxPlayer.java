package org.jglrxavpok.jukebox.api;

import org.jglrxavpok.jukebox.api.music.*;

public interface IJukeboxPlayer
{

    void addToQueue(Music music);

    void play(Music music);

    void playNextInQueue();

    public void setCurrentMusic(Music music);

    public Music getCurrentMusic();
}
