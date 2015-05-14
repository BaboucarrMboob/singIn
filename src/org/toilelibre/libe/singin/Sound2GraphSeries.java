package org.toilelibre.libe.singin;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Sound2GraphSeries {

    public LineGraphSeries<DataPoint> convert (Channel channel, int screenWidth){
        int stepping = channel.getSamples ().length / screenWidth;
        DataPoint [] dataPointArray = new DataPoint [channel.getSamples ().length / stepping + 1];
        this.getNeutral (channel.getSampleSize ());
        for (int i = 0 ; i < channel.getSamples ().length ; i += stepping){
            dataPointArray [i / stepping] = new DataPoint (i, channel.getSamples () [i]);
        }
        return new LineGraphSeries<DataPoint> (dataPointArray);
    }
    
    public long getNeutral (int nbBytesPerSample){
        return (long)Math.pow (256, nbBytesPerSample) / 2;
    }
}
