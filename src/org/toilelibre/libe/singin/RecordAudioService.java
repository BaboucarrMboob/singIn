package org.toilelibre.libe.singin;

import java.io.File;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.HarmonicProductSpectrumSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class RecordAudioService extends IntentService {

    private Handler       stopObjectHandler;
    private final IBinder mIBinder = new LocalBinder ();
    private Messenger messenger;

    private Object stop;

    public RecordAudioService () {
        super (RecordAudioService.class.getSimpleName ());
    }

    @Override
    protected void onHandleIntent (Intent intent) {
        this.stop = new Object ();
        try {
            Sound sound = FluentClient.start ().withRecordedInputStream (new StreamInfo (1, -1, 2, 8000, false, true, null), this.stop).importToSound ().stopWithSound();
            FluentClient.start().withSound(sound).exportToFile (new File (Environment.getExternalStorageDirectory () + "/recorded.wav"));
            FluentClient.start().withSound(sound).findLoudestFrequencies (new HarmonicProductSpectrumSoundTransform<Serializable> (false, true, 0.1f)).shapeIntoSound ("default", "simple_piano", new FormatInfo (2, 8000)).exportToFile (new File (Environment.getExternalStorageDirectory () + "/shaped.wav"));
        } catch (SoundTransformException e) {
            e.printStackTrace();
        }

    }


    public void setStopObjectHandler (Handler handler1) {
        this.messenger = new Messenger (handler1);
        this.stopObjectHandler = handler1;

        this.stopObjectHandler.post (new Runnable () {
            public void run () {
                try {
                    RecordAudioService.this.messenger.send (Message.obtain (stopObjectHandler, 1, RecordAudioService.this.stop));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public class LocalBinder extends Binder {
        public RecordAudioService getInstance () {
            return RecordAudioService.this;
        }
    }
    

    @Override
    public IBinder onBind (Intent intent) {
        return mIBinder;
    }

    @Override
    public void onDestroy () {
        if (stopObjectHandler != null) {
            stopObjectHandler = null;
        }
    }
}
