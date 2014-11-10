package org.jglrxavpok.jukebox;

import javazoom.jl.player.advanced.*;

import org.jglrxavpok.jukebox.api.*;
import org.jglrxavpok.jukebox.api.music.*;

public class JLayerListener extends PlaybackListener implements IMusicListener
{

    private IJukeboxPlayer player;
    private Music          music;

    public JLayerListener(IJukeboxPlayer player, Music music)
    {
        this.player = player;
        this.music = music;
    }

    @Override
    public Music getMusic()
    {
        return music;
    }

    @Override
    public void onStart(Music music)
    {

    }

    @Override
    public void onStop(Music music)
    {
        player.setCurrentMusic(null);
        player.playNextInQueue();
    }

    public void playbackStarted(PlaybackEvent evt)
    {
        onStart(music);
    }

    public void playbackFinished(PlaybackEvent evt)
    {
        onStop(music);
    }

}
