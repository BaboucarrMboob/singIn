package org.toilelibre.libe.singin.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 * service to shape a sound
 */
public class ShapeSoundService extends IntentService {

    public ShapeSoundService() {
        super("shapeSoundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Sound sound = (Sound) intent.getSerializableExtra("inputsound");

            Sound soundShapeResult =
                    FluentClient.start().withSound(sound).findLoudestFrequencies().shapeIntoSound(
                    intent.getStringExtra("pack"), intent.getStringExtra("instrument"), sound.getFormatInfo()).stopWithSound();

            Intent resultIntent = new Intent ("soundshaperesult");
            resultIntent.putExtra("soundshaperesult", soundShapeResult);
            LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
        } catch (SoundTransformException e) {
            Log.e("transform", "problem while shaping", e);
        }

    }
}
