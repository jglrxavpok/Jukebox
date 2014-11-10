package org.jglrxavpok.android.jukebox;

import android.content.*;
import android.widget.*;

import org.jglrxavpok.jukebox.api.music.*;

public class JukeboxPlaylistItemView extends LinearLayout
{

    public JukeboxPlaylistItemView(Context context, MusicInfos infos)
    {
        super(context);
        TextView view = new TextView(context);
        view.setTextSize(24f);
        view.setText(infos.getTitle());
        addView(view);
    }

}
