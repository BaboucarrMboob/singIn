package org.toilelibre.libe.singin;

import java.io.File;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.ioc.android.AndroidRootModule;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

import se.jbee.inject.bind.BinderModule;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class SoundFragment extends Fragment {

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate (R.layout.channels, container, false);
        try {
            new AndroidRootModule ();
            Sound [] sounds = FluentClient.start ().withFile (new File (Environment.getExternalStorageDirectory().getPath() + "/before.wav")).convertIntoSound ().stopWithSounds ();
            GraphView graph = (GraphView) rootView.findViewById (R.id.graph1);
            LineGraphSeries<DataPoint> series = new Sound2GraphSeries ().convert (sounds [0]);
            graph.addSeries (series);
            series.setThickness (2);
        } catch (SoundTransformException e) {
            e.printStackTrace ();
        }
        return rootView;
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach (activity);
    }

}
