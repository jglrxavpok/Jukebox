package org.jglrxavpok.android.jukebox;

import java.util.*;

import android.app.*;
import android.os.*;
import android.view.*;

public class JukeboxActivity extends Activity
{

    public static final String     TAG = JukeboxActivity.class.getSimpleName();
    private AndroidJukebox         jukebox;
    public static JukeboxActivity  instance;
    private ArrayList<JukeboxHost> hosts;
    private JukeboxHost            currentHost;

    public JukeboxActivity()
    {
        hosts = new ArrayList<JukeboxHost>();
        instance = this;
        jukebox = new AndroidJukebox();
    }

    public ArrayList<JukeboxHost> getHosts()
    {
        return hosts;
    }

    public void setJukeboxHost(JukeboxHost host)
    {
        this.currentHost = host;
    }

    public JukeboxHost getCurrentHost()
    {
        return currentHost;
    }

    public AndroidJukebox getJukebox()
    {
        return jukebox;
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(new MainPanel(this));
    }

    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
    }

    /**
     * Called when your activity's options menu needs to be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    /**
     * Called right before your activity's option menu is displayed.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
        }
        return super.onOptionsItemSelected(item);
    }

}
