package org.jglrxavpok.android.jukebox;

import android.content.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

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
        hostList.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3)
            {
                JukeboxHost host = JukeboxActivity.instance.getJukebox().getHosts().get(index);
                JukeboxActivity.instance.getJukebox().setJukeboxHost(host);
                JukeboxActivity.instance.setContentView(R.layout.skeleton_activity);
                TextView jukeboxNameView = (TextView) JukeboxActivity.instance.findViewById(R.id.jukeboxname);
                jukeboxNameView.setText(host.getName());
                ImageView jukeboxIconView = (ImageView) JukeboxActivity.instance.findViewById(R.id.jukeboxicon);
                jukeboxIconView.setImageBitmap(host.getIcon());
            }
        });
    }

    public void addJukeboxHost(JukeboxHost host)
    {
        adapter.add(host);
    }
}
