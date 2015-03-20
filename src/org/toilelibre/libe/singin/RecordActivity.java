package org.toilelibre.libe.singin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class RecordActivity extends Activity {

    private static Object              stop     = null;
    private final View.OnClickListener btnClick = new View.OnClickListener () {
                                                    @Override
                                                    public void onClick (View v) {
                                                        switch (v.getId ()) {
                                                            case R.id.btnRecordStart: {
                                                                RecordActivity.this.enableButtons (true);
                                                                RecordActivity.this.startRecording ();
                                                                break;
                                                            }
                                                            case R.id.btnRecordStop: {
                                                                RecordActivity.this.enableButtons (false);
                                                                RecordActivity.this.stopRecording ();
                                                                break;
                                                            }
                                                        }
                                                    }
                                                };
    protected StopObjectHandler handler = new StopObjectHandler ();
    protected RecordAudioService service;

    static class StopObjectHandler extends Handler { // Handler of incoming messages from clients.
        public StopObjectHandler () {
        }

        @Override
        public void handleMessage (Message msg) {
            RecordActivity.stop = msg.obj;
        }
    }

    private void enableButton (int id, boolean isEnable) {
        ((Button) this.findViewById (id)).setEnabled (isEnable);
    }

    private void enableButtons (boolean isRecording) {
        this.enableButton (R.id.btnRecordStart, !isRecording);
        this.enableButton (R.id.btnRecordStop, isRecording);
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.record);

        this.setButtonHandlers ();
        this.enableButtons (false);

    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish ();
        }
        return super.onKeyDown (keyCode, event);
    }

    private void setButtonHandlers () {
        ((Button) this.findViewById (R.id.btnRecordStart)).setOnClickListener (this.btnClick);
        ((Button) this.findViewById (R.id.btnRecordStop)).setOnClickListener (this.btnClick);
    }

    private void startRecording () {    
        ServiceConnection          connection                 = new ServiceConnection () {
        @Override
        public void onServiceConnected (ComponentName componentName, IBinder iBinder) {
            RecordActivity.this.service = ((RecordAudioService.LocalBinder) iBinder).getInstance ();
            RecordActivity.this.service.setStopObjectHandler (RecordActivity.this.handler);
        }

        @Override
        public void onServiceDisconnected (ComponentName name) {
            RecordActivity.this.service = null;            
        }};
        Intent intent = new Intent (this, RecordAudioService.class);
        this.bindService (intent, connection, Context.BIND_AUTO_CREATE);
        this.startService (intent);
    }

    private void stopRecording () {
        if (RecordActivity.stop == null) {
            return;
        }
        synchronized (RecordActivity.stop) {
            RecordActivity.stop.notify ();
        }
        RecordActivity.stop = null;
    }
}
