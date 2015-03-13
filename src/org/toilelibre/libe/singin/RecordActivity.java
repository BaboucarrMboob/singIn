package org.toilelibre.libe.singin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class RecordActivity extends Activity {

    private static final int           RECORDER_SAMPLERATE     = 8000;
    private static final int           RECORDER_CHANNELS       = AudioFormat.CHANNEL_IN_MONO;
    private static final int           RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord                recorder                = null;
    private Thread                     recordingThread         = null;
    private boolean                    isRecording             = false;

    int                                bufferElements2Rec      = 1024;                          // want to play 2048 (2K) since 2 bytes we use only 1024

    int                                bytesPerElement         = 2;                             // 2 bytes in 16bit format

    private final View.OnClickListener btnClick                = new View.OnClickListener () {
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
    private List<Byte> buffer;
    private int bufferSize;

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

        this.bufferSize = AudioRecord.getMinBufferSize (RecordActivity.RECORDER_SAMPLERATE, RecordActivity.RECORDER_CHANNELS, RecordActivity.RECORDER_AUDIO_ENCODING);
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

    //convert short to byte
    private Byte [] short2byte (short [] sData) {
        final int shortArrsize = sData.length;
        final Byte [] bytes = new Byte [shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes [i * 2] = (byte) (sData [i] & 0x00FF);
            bytes [ (i * 2) + 1] = (byte) (sData [i] >> 8);
            sData [i] = 0;
        }
        return bytes;

    }

    private static int[] SAMPLE_RATES = new int[] { 8000, 11025, 22050, 44100 };
    
    public AudioRecord findAudioRecorder() {
        for (int rate : SAMPLE_RATES) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        return null;
    }

    private void startRecording () {

        this.recorder = findAudioRecorder();

        this.recorder.startRecording ();
        this.isRecording = true;
        this.recordingThread = new Thread (new Runnable () {
            @Override
            public void run () {
                RecordActivity.this.writeAudioDataToFile ();
            }
        }, "AudioRecorder Thread");
        this.recordingThread.start ();
    }

    private void stopRecording () {
        // stops the recording activity
        if (null != this.recorder) {
            this.isRecording = false;
            this.recorder.stop ();
            this.recorder.release ();
            StreamInfo si = new StreamInfo (1, this.buffer.size () / this.bytesPerElement, this.bytesPerElement, RECORDER_SAMPLERATE, false, true, null);
            byte[] b2 = new byte[this.buffer.size ()];
            for (int i = 0; i < this.buffer.size (); i++)
            {
                b2[i] = this.buffer.get (i);
            }
            ByteArrayInputStream baos = new ByteArrayInputStream (b2);
            File file = new File (Environment.getExternalStorageDirectory () + "/shaped.wav");
            try {
                FluentClient.start ().withRawInputStream (baos, si).importToSound ().findLoudestFrequencies ().shapeIntoSound ("default", "simple_piano", si).exportToFile (file);
            } catch (SoundTransformException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.recorder = null;
            this.recordingThread = null;
        }
    }

    private void writeAudioDataToFile () {
        // Write the output audio in byte

        final short sData [] = new short [this.bufferElements2Rec];

        this.buffer = new ArrayList<Byte> (this.bufferSize);

        while (this.isRecording) {
            // gets the voice output from microphone to byte format

            this.recorder.read (sData, 0, this.bufferElements2Rec);
            final Byte bData [] = this.short2byte (sData);
            buffer.addAll (Arrays.asList (bData));
        }
    }
}
