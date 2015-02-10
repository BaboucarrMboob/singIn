package org.toilelibre.libe.singin;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Sound2GraphSeries {

    public LineGraphSeries<DataPoint> convert (Sound sound){
        int stepping = 10000;
        DataPoint [] dataPointArray = new DataPoint [sound.getSamples ().length / stepping + 1];
        long neutral = this.getNeutral (sound.getNbBytesPerSample ());
        for (int i = 0 ; i < sound.getSamples ().length ; i += stepping){
            dataPointArray [i / stepping] = new DataPoint (i, sound.getSamples () [i] - neutral);
        }
        return new LineGraphSeries<DataPoint> (dataPointArray);
    }
    
    public long getNeutral (int nbBytesPerSample){
        return (long)Math.pow (256, nbBytesPerSample) / 2;
    }
}
