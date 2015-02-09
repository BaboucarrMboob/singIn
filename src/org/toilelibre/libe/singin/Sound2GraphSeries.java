package org.toilelibre.libe.singin;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Sound2GraphSeries {

    public LineGraphSeries<DataPoint> convert (Sound sound){
        DataPoint [] dataPointArray = new DataPoint [sound.getSamples ().length];
        long neutral = this.getNeutral (sound.getNbBytesPerSample ());
        for (int i = 0 ; i < dataPointArray.length ; i++){
            dataPointArray [i] = new DataPoint (i, sound.getSamples () [i] - neutral);
        }
        return new LineGraphSeries<DataPoint> (dataPointArray);
    }
    
    public long getNeutral (int nbBytesPerSample){
        return (long)Math.pow (256, nbBytesPerSample) / 2;
    }
}
