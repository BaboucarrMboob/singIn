package org.toilelibre.libe.singin;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SoundFragment extends Fragment {

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate (R.layout.channels, container, false);
        Button recordButton = (Button) rootView.findViewById (R.id.recordButton);
        ((com.jjoe64.graphview.GraphView)rootView.findViewById (R.id.graph1)).getViewport ().setScalable (true);
        ((com.jjoe64.graphview.GraphView)rootView.findViewById (R.id.graph1)).getViewport ().setScrollable (true);
        ((com.jjoe64.graphview.GraphView)rootView.findViewById (R.id.graph1)).getViewport ().setXAxisBoundsManual (true);
        recordButton.setOnClickListener (new OnClickListener () {

            @Override
            public void onClick (View v) {

                RecordFragment rif = new RecordFragment ();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, rif);
                ft.commit ();
            }

        });
        return rootView;
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach (activity);
    }

}
