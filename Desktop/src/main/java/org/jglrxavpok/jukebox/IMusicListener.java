package org.jglrxavpok.jukebox;

import org.jglrxavpok.jukebox.api.music.*;

public interface IMusicListener
{

    void onStart(Music music);

    void onStop(Music music);

    Music getMusic();
}
