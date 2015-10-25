package org.toilelibre.libe.singin;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RecordFragment extends Fragment {

    @Bind (R.id.btnRecordStart)
    ImageButton                        recordStart;
    @Bind (R.id.btnRecordStop)
    Button                             recordStop;

    private static Object              stop     = null;
    private final View.OnClickListener btnClick = new View.OnClickListener () {
        @Override
        public void onClick (final View v) {
            switch (v.getId ()) {
                case R.id.btnRecordStart : {
                    RecordFragment.this.enableButtons (v, true);
                    RecordFragment.this.startRecording ();
                    break;
                }
                case R.id.btnRecordStop : {
                    RecordFragment.this.enableButtons (v, false);
                    RecordFragment.this.stopRecording ();
                    break;
                }
            }
        }
    };
    protected StopObjectHandler        handler  = new StopObjectHandler ();
    protected RecordAudioService       service;

    static class StopObjectHandler extends Handler { // Handler of incoming messages from clients.
        public StopObjectHandler () {
    }

    @Override
    public void handleMessage (final Message msg) {
        RecordFragment.stop = msg.obj;
    }
    }

    private void enableButton (final Button button, final boolean isEnable) {
        if (button != null) {
            button.setEnabled (isEnable);
        }
    }

    private void enableButton(ImageButton button, boolean isEnable) {
        if (button != null) {
            button.setEnabled (isEnable);
        }
	}

    private void enableButtons (final View view, final boolean isRecording) {
        this.enableButton (this.recordStart, !isRecording);
        this.enableButton (this.recordStop, isRecording);
    }

	@Override
    public View onCreateView (final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate (R.layout.record, container, false);
        ButterKnife.bind (this, rootView);
        super.onCreate (savedInstanceState);

        this.setButtonHandlers (rootView);
        this.enableButtons (rootView, false);

        return rootView;
    }

    private void setButtonHandlers (final View view) {
        this.recordStart.setOnClickListener (this.btnClick);
        this.recordStop.setOnClickListener (this.btnClick);
    }

    private void startRecording () {
        final ServiceConnection connection = new ServiceConnection () {
            @Override
            public void onServiceConnected (final ComponentName componentName, final IBinder iBinder) {
                RecordFragment.this.service = ((RecordAudioService.LocalBinder) iBinder).getInstance ();
                RecordFragment.this.service.setStopObjectHandler (RecordFragment.this.handler);
            }

            @Override
            public void onServiceDisconnected (final ComponentName name) {
                RecordFragment.this.service = null;
            }
        };
        final Intent intent = new Intent (this.getActivity (), RecordAudioService.class);
        this.getActivity ().bindService (intent, connection, Context.BIND_AUTO_CREATE);
        this.getActivity ().startService (intent);
    }

    private void stopRecording () {
        if (RecordFragment.stop == null) {
            return;
        }
        synchronized (RecordFragment.stop) {
            RecordFragment.stop.notifyAll ();
        }
        RecordFragment.stop = null;
    }
}
