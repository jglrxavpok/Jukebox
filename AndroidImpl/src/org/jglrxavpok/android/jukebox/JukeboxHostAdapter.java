package org.jglrxavpok.android.jukebox;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;

public class JukeboxHostAdapter extends ArrayAdapter<JukeboxHost>
{

    private List<JukeboxSelectView> views;

    public JukeboxHostAdapter(Context context, List<JukeboxHost> objects)
    {
        super(context, android.R.layout.simple_list_item_1, objects);
        views = new ArrayList<JukeboxSelectView>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return views.get(position);
    }

    public void add(JukeboxHost host)
    {
        views.add(new JukeboxSelectView(getContext(), host));
        JukeboxActivity.instance.runOnUiThread(new Runnable()
        {
            public void run()
            {
                notifyDataSetChanged();
            }
        });
    }

}
