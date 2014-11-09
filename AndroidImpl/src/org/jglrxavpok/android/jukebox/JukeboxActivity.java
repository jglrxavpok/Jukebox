package org.jglrxavpok.android.jukebox;

import java.io.*;
import java.net.*;

import android.app.*;
import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;

import org.jglrxavpok.jukebox.api.music.*;

public class JukeboxActivity extends Activity
{

    public static final String    TAG                  = "Android Jukebox";
    public static final int       CHOOSE_FILE_ACTIVITY = 0;
    private AndroidJukeboxClient  jukebox;
    private MainPanel             mainPanel;
    public static JukeboxActivity instance;

    public JukeboxActivity()
    {
        instance = this;
        jukebox = new AndroidJukeboxClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case CHOOSE_FILE_ACTIVITY:
                if(resultCode == RESULT_OK)
                {
                    // Get the Uri of the selected file 
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    String path;
                    try
                    {
                        path = getPath(this, uri);
                        Log.d(TAG, "File Path: " + path);
                        File file = new File(path);
                        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                        byte[] buffer = new byte[4096];
                        int i;
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        while((i = in.read(buffer)) != -1)
                        {
                            out.write(buffer, 0, i);
                        }
                        out.flush();
                        out.close();
                        in.close();
                        jukebox.sendMusic(new Music(out.toByteArray(), new MusicInfos(file.getName(), MusicFormat.MP3))); // TODO: allow other formats
                        // TODO
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    // Get the file instance
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException
    {
        if("content".equalsIgnoreCase(uri.getScheme()))
        {
            String[] projection =
            {
                    "_data"
            };
            Cursor cursor = null;

            try
            {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if(cursor.moveToFirst())
                {
                    return cursor.getString(column_index);
                }
            }
            catch(Exception e)
            {
                // Eat it
            }
        }
        else if("file".equalsIgnoreCase(uri.getScheme()))
        {
            return uri.getPath();
        }

        return null;
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
