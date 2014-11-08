package org.jglrxavpok.android.jukebox;

import android.content.*;
import android.widget.*;

public class MainPanel extends LinearLayout
{

    private ListView           hostList;
    private JukeboxHostAdapter adapter;

    public MainPanel(Context context)
    {
        super(context);
        hostList = new ListView(context);
        adapter = new JukeboxHostAdapter(context, JukeboxActivity.instance.getJukebox().getHosts());
        hostList.setAdapter(adapter);
        addView(hostList);
    }

    public void addJukeboxHost(JukeboxHost host)
    {
        adapter.add(host);
    }
}
