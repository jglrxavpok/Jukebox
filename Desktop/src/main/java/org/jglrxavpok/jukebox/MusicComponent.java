package org.jglrxavpok.jukebox;

import javax.swing.*;

import org.jglrxavpok.jukebox.api.music.*;

public class MusicComponent extends JPanel
{

    private static final long serialVersionUID = -2723638553463319294L;
    private Music             music;

    public MusicComponent(Music music)
    {
        this.music = music;
        add(new JLabel("<html><u>" + music.getInfos().getTitle() + "</u></html>"));
    }

}
