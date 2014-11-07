package org.jglrxavpok.jukebox;

import org.jglrxavpok.jukebox.api.*;

public class DesktopJukebox implements IJukebox
{

    @Override
    public EnumJukeboxEnd getType()
    {
        return EnumJukeboxEnd.PLAYER;
    }

}
