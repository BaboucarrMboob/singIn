package org.toilelibre.libe.singin.poc;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Sound2GraphSeries {

    public LineGraphSeries<DataPoint> convert (final Channel channel, final int screenWidth) {
        final int stepping = channel.getSamplesLength () / screenWidth;
        final DataPoint [] dataPointArray = new DataPoint [channel.getSamplesLength () / stepping + 1];
        this.getNeutral (channel.getSampleSize ());
        for (int i = 0 ; i < channel.getSamplesLength () ; i += stepping) {
            dataPointArray [i / stepping] = new DataPoint (i, channel.getSampleAt (i));
        }
        return new LineGraphSeries<DataPoint> (dataPointArray);
    }

    public long getNeutral (final int nbBytesPerSample) {
        return (long) Math.pow (256, nbBytesPerSample) / 2;
    }
}
