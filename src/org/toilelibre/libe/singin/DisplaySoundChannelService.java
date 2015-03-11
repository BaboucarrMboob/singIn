package org.toilelibre.libe.singin;

import java.io.File;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.android.AndroidRootModule;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class DisplaySoundChannelService extends IntentService {

    private Handler       statusHandler;
    private Handler       graphHandler;
    private final IBinder mIBinder = new LocalBinder ();
    private Messenger messenger;
    private Messenger graphMessenger;

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public DisplaySoundChannelService () {
        super (DisplaySoundChannelService.class.getSimpleName ());
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns,
     * IntentService stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent (Intent intent) {
        Observer observer = this.createObserver ();
        try {
            new AndroidRootModule ();
            FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.INFO), observer);
            Sound [] sounds = FluentClient.start ().withAPack ("default", this, org.toilelibre.libe.soundtransform.R.raw.class, org.toilelibre.libe.soundtransform.R.raw.defaultpack)
                    .withFile (new File (Environment.getExternalStorageDirectory ().getPath () + "/before.wav")).convertIntoSound ().stopWithSounds ();
            LineGraphSeries<DataPoint> series = new Sound2GraphSeries ().convert (sounds [0], 1024);
            series.setThickness (2);
            this.graphMessenger.send (Message.obtain (this.graphHandler, 1, series));
        } catch (SoundTransformException e) {
            e.printStackTrace ();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Observer createObserver () {
        return new Observer () {

            @Override
            public void notify (final LogEvent logEvent) {
                if (DisplaySoundChannelService.this.statusHandler == null ||
                        logEvent.getLevel ().compareTo (LogLevel.VERBOSE) < 0) {
                    return;
                }
                DisplaySoundChannelService.this.statusHandler.post (new Runnable () {
                    public void run () {
                        try {
                            DisplaySoundChannelService.this.messenger.send (Message.obtain (statusHandler, 1, logEvent));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        };
    }

    public void setStatusHandler (Handler handler1) {
        this.messenger = new Messenger (handler1);
        this.statusHandler = handler1;
    }

    public void setGraphHandler (Handler handler1) {
        this.graphMessenger = new Messenger (handler1);
        this.graphHandler = handler1;
    }

    public class LocalBinder extends Binder {
        public DisplaySoundChannelService getInstance () {
            return DisplaySoundChannelService.this;
        }
    }

    @Override
    public IBinder onBind (Intent intent) {
        return mIBinder;
    }

    @Override
    public void onDestroy () {
        if (statusHandler != null) {
            statusHandler = null;
        }
    }
}
