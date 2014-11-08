package org.jglrxavpok.android.jukebox;

import org.jglrxavpok.jukebox.api.*;

public class AndroidJukebox implements IJukebox
{

    @Override
    public EnumJukeboxEnd getType()
    {
        return EnumJukeboxEnd.CLIENT;
    }

}
