package org.jglrxavpok.android.jukebox;

import android.content.*;
import android.widget.*;

import org.jglrxavpok.jukebox.api.music.*;

public class JukeboxPlaylistItemView extends LinearLayout
{

    public JukeboxPlaylistItemView(Context context, MusicInfos infos)
    {
        super(context);
        setOrientation(LinearLayout.VERTICAL);

        TextView titleView = new TextView(context);
        titleView.setTextSize(16f);
        titleView.setText(infos.getAlbum() + " - " + infos.getTitle());
        addView(titleView);

        TextView authorView = new TextView(context);
        authorView.setTextSize(12f);
        authorView.setText(infos.getAuthor() + " (" + infos.getYear() + ")");
        addView(authorView);
    }

}
