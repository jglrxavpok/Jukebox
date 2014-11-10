package org.jglrxavpok.android.jukebox;

import android.content.*;
import android.graphics.*;
import android.widget.*;

public class JukeboxSelectView extends LinearLayout
{

    public JukeboxSelectView(Context context, JukeboxHost host)
    {
        super(context);
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundColor(Color.TRANSPARENT);
        imageView.setImageBitmap(host.getIcon());
        addView(imageView);
        TextView titleView = new TextView(context);
        titleView.setTextSize(24f);
        titleView.setText(host.getName());
        addView(titleView);
    }
}
