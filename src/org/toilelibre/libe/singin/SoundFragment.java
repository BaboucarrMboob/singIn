package org.toilelibre.libe.singin;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.jjoe64.graphview.GraphView;

public class SoundFragment extends Fragment {

    @Bind (R.id.graph1)
    GraphView graph1;

    @Bind (R.id.recordButton)
    Button    recordButton;

    @Override
    public View onCreateView (final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View rootView = inflater.inflate (R.layout.channels, container, false);
        ButterKnife.bind (this, rootView);
        this.graph1.getViewport ().setScalable (true);
        this.graph1.getViewport ().setScrollable (true);
        this.graph1.getViewport ().setXAxisBoundsManual (true);
        this.recordButton.setOnClickListener (new OnClickListener () {

            @Override
            public void onClick (final View v) {

                final RecordFragment rif = new RecordFragment ();
                final FragmentTransaction ft = SoundFragment.this.getFragmentManager ().beginTransaction ();
                ft.replace (R.id.fragment_container, rif);
                ft.commit ();
            }

        });
        return rootView;
    }

}
