package org.jglrxavpok.android.jukebox;

import java.net.*;
import java.util.*;

import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class MainPanel extends LinearLayout
{

    public MainPanel(Context context)
    {
        super(context);

        Button button = new Button(context);
        button.setText("Click me!");
        addView(button);

        OnClickListener listener = new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Log.d(JukeboxActivity.TAG, "Hello!");
                try
                {
                    tryFindIps();
                }
                catch(SocketException e)
                {
                    e.printStackTrace();
                }
            }
        };
        button.setOnClickListener(listener);
    }

    public void tryFindIps() throws SocketException
    {
        ipAdresses();
    }

    private ArrayList<InetAddress> ipAdresses() throws SocketException
    {
        return null;
    }
}
