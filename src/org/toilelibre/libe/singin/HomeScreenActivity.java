package org.toilelibre.libe.singin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.jjoe64.graphview.GraphView;

public class HomeScreenActivity extends Activity {

    @Bind (R.id.graph1)
    GraphView graphView;
    @Bind (R.id.currentStatus)
    TextView  currentStatus;

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.activity_home_screen);
        ButterKnife.bind (this);
    }

    private StatusTextHandler          handler;
    private GraphHandler               graphHandler;
    private boolean                    isBound;
    private DisplaySoundChannelService displaySoundChannelService = null;
    private final ServiceConnection          connection                 = new ServiceConnection () {
        @Override
        public void onServiceConnected (final ComponentName componentName, final IBinder iBinder) {
            HomeScreenActivity.this.displaySoundChannelService = ((DisplaySoundChannelService.LocalBinder) iBinder).getInstance ();
            HomeScreenActivity.this.displaySoundChannelService.setStatusHandler (HomeScreenActivity.this.handler);
            HomeScreenActivity.this.displaySoundChannelService.setGraphHandler (HomeScreenActivity.this.graphHandler);

        }

        @Override
        public void onServiceDisconnected (final ComponentName componentName) {
            HomeScreenActivity.this.displaySoundChannelService = null;
        }
    };

    @Override
    public void onStart () {
        super.onStart ();
        this.getFragmentManager ().beginTransaction ().add (R.id.fragment_container, new SoundFragment ()).commit ();
        this.handler = new StatusTextHandler (this.currentStatus);
        this.graphHandler = new GraphHandler (this.graphView);
        this.startService (new Intent (this, DisplaySoundChannelService.class));
        this.doBindService ();
    }

    private void doBindService () {
        this.bindService (new Intent (this, DisplaySoundChannelService.class), this.connection, Context.BIND_AUTO_CREATE);
        this.isBound = true;
    }

    private void doUnbindService () {
        if (this.isBound) {
            // Detach our existing connection.
            this.unbindService (this.connection);
            this.isBound = false;
        }
    }

    @Override
    public void onDestroy () {
        super.onDestroy ();
        this.doUnbindService ();
    }

}
