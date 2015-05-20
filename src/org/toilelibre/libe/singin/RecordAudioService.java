package org.toilelibre.libe.singin;

import java.io.File;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.EqualizerSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.ReduceNoiseSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.NormalizeSoundTransform;
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
            Sound sound = FluentClient.start ().withRecordedInputStream (new StreamInfo (1, -1, 2, 8000, false, true, null), this.stop).importToSound ().apply (new NormalizeSoundTransform (1)).stopWithSound();
            FluentClient.start().withSound(sound).apply(new ReduceNoiseSoundTransform(30)).apply(new EqualizerSoundTransform(new double [] {0, 99, 100, 200, 300, 400, 500, 600, 700, 800, 801, 20000}, new double [] {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0})).exportToFile (new File (Environment.getExternalStorageDirectory () + "/recorded.wav"));
            FluentClient.start().withSound(sound).findLoudestFrequencies ().surroundInRange(130, 800).shapeIntoSound ("default", "simple_piano", new FormatInfo (2, 8000)).exportToFile (new File (Environment.getExternalStorageDirectory () + "/shaped.wav"));
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
