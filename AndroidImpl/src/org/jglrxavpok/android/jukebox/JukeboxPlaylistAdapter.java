package org.jglrxavpok.android.jukebox;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;

import org.jglrxavpok.jukebox.api.music.*;

public class JukeboxPlaylistAdapter extends ArrayAdapter<MusicInfos>
{

    private List<JukeboxPlaylistItemView> views;

    public JukeboxPlaylistAdapter(Context context, List<MusicInfos> objects)
    {
        super(context, android.R.layout.simple_list_item_1, objects);
        views = new ArrayList<JukeboxPlaylistItemView>();
    }

    public void clear()
    {
        views.clear();
        super.clear();
    }

    public int getCount()
    {
        return views.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return views.get(position);
    }

    public void add(MusicInfos infos)
    {
        views.add(new JukeboxPlaylistItemView(getContext(), infos));
    }

}
