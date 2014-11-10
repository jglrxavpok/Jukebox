package org.jglrxavpok.jukebox;

import java.awt.*;

import javax.swing.*;

import org.jglrxavpok.jukebox.api.music.*;

public class MusicCellRenderer<T extends Music> implements ListCellRenderer<T>
{

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus)
    {
        return new MusicComponent(value);
    }

}
