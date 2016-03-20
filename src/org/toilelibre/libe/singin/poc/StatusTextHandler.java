package org.toilelibre.libe.singin.poc;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class StatusTextHandler extends Handler { // Handler of incoming messages from clients.
    private final TextView statusTextView;

    public StatusTextHandler (final TextView statusTextView1) {
        this.statusTextView = statusTextView1;
    }

    @Override
    public void handleMessage (final Message msg) {
        this.statusTextView.setText (msg.obj.toString ());
    }
}