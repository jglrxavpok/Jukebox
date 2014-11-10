package org.jglrxavpok.android.jukebox;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import org.jglrxavpok.jukebox.api.music.*;

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
                JukeboxActivity.instance.setContentView(R.layout.jukebox);
                TextView jukeboxNameView = (TextView) JukeboxActivity.instance.findViewById(R.id.jukeboxname);
                jukeboxNameView.setText(host.getName());
                ImageView jukeboxIconView = (ImageView) JukeboxActivity.instance.findViewById(R.id.jukeboxicon);
                jukeboxIconView.setImageBitmap(host.getIcon());

                ListView jukeboxPlayList = (ListView) JukeboxActivity.instance.findViewById(R.id.jukeboxPlaylist);
                JukeboxPlaylistAdapter adapter = new JukeboxPlaylistAdapter(getContext(), new ArrayList<MusicInfos>());
                jukeboxPlayList.setAdapter(adapter);

                Button button = (Button) JukeboxActivity.instance.findViewById(R.id.uploadButton);
                button.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        showFileChooser();
                    }
                });
            }
        });
    }

    private void showFileChooser()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try
        {
            JukeboxActivity.instance.startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    JukeboxActivity.CHOOSE_FILE_ACTIVITY);
        }
        catch(android.content.ActivityNotFoundException ex)
        {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(JukeboxActivity.instance, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void addJukeboxHost(JukeboxHost host)
    {
        adapter.add(host);
    }
}
