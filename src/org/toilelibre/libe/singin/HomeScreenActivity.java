package org.toilelibre.libe.singin;


import com.jjoe64.graphview.series.Series;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.TextView;

public class HomeScreenActivity extends Activity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView(R.layout.activity_home_screen);
    }

    static class GraphHandler extends Handler { // Handler of incoming messages from clients.
        private Activity context;
        public GraphHandler (Activity context){
            this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            ((com.jjoe64.graphview.GraphView)this.context.findViewById (R.id.graph1)).getViewport ().setMinX (0);
            ((com.jjoe64.graphview.GraphView)this.context.findViewById (R.id.graph1)).getViewport ().setMaxX (((Series<?>) msg.obj).getHighestValueX ());
            ((com.jjoe64.graphview.GraphView)this.context.findViewById (R.id.graph1)).addSeries ((Series<?>) msg.obj);
        }
    }
    
    static class StatusTextHandler extends Handler { // Handler of incoming messages from clients.
        private TextView statusTextView;
        public StatusTextHandler (TextView statusTextView1){
            this.statusTextView = statusTextView1;
        }
        @Override
        public void handleMessage(Message msg) {
            this.statusTextView.setText (msg.obj.toString ());
        }
    }
    
    
    private StatusTextHandler          handler;
    private GraphHandler               graphHandler;
    private boolean                    isBound;
    private DisplaySoundChannelService displaySoundChannelService = null;
    private ServiceConnection          connection                 = new ServiceConnection () {
                                                                      @Override
                                                                      public void onServiceConnected (ComponentName componentName, IBinder iBinder) {
                                                                          HomeScreenActivity.this.displaySoundChannelService = ((DisplaySoundChannelService.LocalBinder) iBinder).getInstance ();
                                                                          HomeScreenActivity.this.displaySoundChannelService.setStatusHandler (HomeScreenActivity.this.handler);
                                                                          HomeScreenActivity.this.displaySoundChannelService.setGraphHandler (HomeScreenActivity.this.graphHandler);

                                                                      }

                                                                      @Override
                                                                      public void onServiceDisconnected (ComponentName componentName) {
                                                                          HomeScreenActivity.this.displaySoundChannelService = null;
                                                                      }
    };


    @Override
    public void onStart () {
        super.onStart ();
        this.handler = new StatusTextHandler ((TextView) this.findViewById (R.id.currentStatus));
        this.graphHandler = new GraphHandler (this);
        this.startService (new Intent (this, DisplaySoundChannelService.class));
        this.doBindService ();
        ((com.jjoe64.graphview.GraphView)this.findViewById (R.id.graph1)).getViewport ().setScalable (true);
        ((com.jjoe64.graphview.GraphView)this.findViewById (R.id.graph1)).getViewport ().setScrollable (true);
        ((com.jjoe64.graphview.GraphView)this.findViewById (R.id.graph1)).getViewport ().setXAxisBoundsManual (true);
    }

    private void doBindService () {
        this.bindService (new Intent (this, DisplaySoundChannelService.class), this.connection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void doUnbindService () {
        if (isBound) {
            // Detach our existing connection.
            this.unbindService (connection);
            isBound = false;
        }
    }

    @Override
    public void onDestroy () {
        super.onDestroy ();
        this.doUnbindService ();
    }

}
