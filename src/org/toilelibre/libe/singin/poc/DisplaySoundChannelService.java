package org.toilelibre.libe.singin.poc;

import java.io.File;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.android.AndroidRootModule;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

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
    private Messenger     messenger;
    private Messenger     graphMessenger;

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
    protected void onHandleIntent (final Intent intent) {
        final Observer observer = this.createObserver ();
        try {
            new AndroidRootModule ();
            FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.INFO), observer);
            final Sound sound = FluentClient.start ().withAPack ("default", this, org.toilelibre.libe.soundtransform.R.raw.class, org.toilelibre.libe.soundtransform.R.raw.defaultpack).withFile (new File (Environment.getExternalStorageDirectory ().getPath () + "/before.wav")).convertIntoSound ()
                    .stopWithSound ();
            final LineGraphSeries<DataPoint> series = new Sound2GraphSeries ().convert (sound.getChannels () [0], 1024);
            series.setThickness (2);
            this.graphMessenger.send (Message.obtain (this.graphHandler, 1, series));
        } catch (final SoundTransformException e) {
            e.printStackTrace ();
        } catch (final RemoteException e) {
            e.printStackTrace ();
        }
    }

    private Observer createObserver () {
        return new Observer () {

            @Override
            public void notify (final LogEvent logEvent) {
                if (DisplaySoundChannelService.this.statusHandler == null || logEvent.getLevel ().compareTo (LogLevel.VERBOSE) < 0) {
                    return;
                }
                DisplaySoundChannelService.this.statusHandler.post (new Runnable () {
                    @Override
                    public void run () {
                        try {
                            DisplaySoundChannelService.this.messenger.send (Message.obtain (DisplaySoundChannelService.this.statusHandler, 1, logEvent));
                        } catch (final RemoteException e) {
                            e.printStackTrace ();
                        }
                    }
                });
            }

        };
    }

    public void setStatusHandler (final Handler handler1) {
        this.messenger = new Messenger (handler1);
        this.statusHandler = handler1;
    }

    public void setGraphHandler (final Handler handler1) {
        this.graphMessenger = new Messenger (handler1);
        this.graphHandler = handler1;
    }

    public class LocalBinder extends Binder {
        public DisplaySoundChannelService getInstance () {
            return DisplaySoundChannelService.this;
        }
    }

    @Override
    public IBinder onBind (final Intent intent) {
        return this.mIBinder;
    }

    @Override
    public void onDestroy () {
        if (this.statusHandler != null) {
            this.statusHandler = null;
        }
    }
}
