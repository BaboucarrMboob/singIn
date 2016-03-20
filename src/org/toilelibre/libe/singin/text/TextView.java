package org.toilelibre.libe.singin.text;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class TextView extends android.widget.TextView {

    public TextView (Context context, AttributeSet attrs) {
        super (context, attrs);
        this.setTypeface (Typeface.createFromAsset (context.getAssets (), "fonts/GeosansLight.ttf"));
    }

}
