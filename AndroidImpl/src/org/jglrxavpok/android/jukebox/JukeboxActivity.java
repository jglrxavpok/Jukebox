package org.jglrxavpok.android.jukebox;

import android.app.*;
import android.os.*;
import android.view.*;

public class JukeboxActivity extends Activity
{

    public static final String    TAG = "Android Jukebox";
    private AndroidJukeboxClient  jukebox;
    private MainPanel             mainPanel;
    public static JukeboxActivity instance;

    public JukeboxActivity()
    {
        instance = this;
        jukebox = new AndroidJukeboxClient();
    }

    public AndroidJukeboxClient getJukebox()
    {
        return jukebox;
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainPanel = new MainPanel(this);
        setContentView(mainPanel);
    }

    public MainPanel getMainPanel()
    {
        return mainPanel;
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
