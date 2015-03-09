package org.toilelibre.libe.singin;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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
        recordButton.setOnClickListener (new OnClickListener () {

            @Override
            public void onClick (View v) {
                Intent intent = new Intent (rootView.getContext (), RecordActivity.class);
                startActivity (intent);
            }

        });
        return rootView;
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach (activity);
    }

}
