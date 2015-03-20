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

public class RecordFragment extends Fragment {

    private static Object              stop     = null;
    private final View.OnClickListener btnClick = new View.OnClickListener () {
                                                    @Override
                                                    public void onClick (View v) {
                                                        switch (v.getId ()) {
                                                            case R.id.btnRecordStart: {
                                                                RecordFragment.this.enableButtons (v, true);
                                                                RecordFragment.this.startRecording ();
                                                                break;
                                                            }
                                                            case R.id.btnRecordStop: {
                                                                RecordFragment.this.enableButtons (v, false);
                                                                RecordFragment.this.stopRecording ();
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
            RecordFragment.stop = msg.obj;
        }
    }

    private void enableButton (final View view, int id, boolean isEnable) {
        if (view != null && view.findViewById (id) != null){
            ((Button) view.findViewById (id)).setEnabled (isEnable);
        }else if (this.getView () != null && this.getView ().findViewById (id) != null){
            ((Button) this.getView ().findViewById (id)).setEnabled (isEnable);
        }
    }

    private void enableButtons (final View view, boolean isRecording) {
        this.enableButton (view, R.id.btnRecordStart, !isRecording);
        this.enableButton (view, R.id.btnRecordStop, isRecording);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate (R.layout.record, container, false);
        super.onCreate (savedInstanceState);

        this.setButtonHandlers (rootView);
        this.enableButtons (rootView, false);

        return rootView;
    }

    private void setButtonHandlers (final View view) {
        ((Button) view.findViewById (R.id.btnRecordStart)).setOnClickListener (this.btnClick);
        ((Button) view.findViewById (R.id.btnRecordStop)).setOnClickListener (this.btnClick);
    }

    private void startRecording () {    
        ServiceConnection          connection                 = new ServiceConnection () {
        @Override
        public void onServiceConnected (ComponentName componentName, IBinder iBinder) {
            RecordFragment.this.service = ((RecordAudioService.LocalBinder) iBinder).getInstance ();
            RecordFragment.this.service.setStopObjectHandler (RecordFragment.this.handler);
        }

        @Override
        public void onServiceDisconnected (ComponentName name) {
            RecordFragment.this.service = null;            
        }};
        Intent intent = new Intent (this.getActivity (), RecordAudioService.class);
        this.getActivity ().bindService (intent, connection, Context.BIND_AUTO_CREATE);
        this.getActivity ().startService (intent);
    }

    private void stopRecording () {
        if (RecordFragment.stop == null) {
            return;
        }
        synchronized (RecordFragment.stop) {
            RecordFragment.stop.notify ();
        }
        RecordFragment.stop = null;
    }
}
