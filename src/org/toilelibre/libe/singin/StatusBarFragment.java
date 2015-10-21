package org.toilelibre.libe.singin;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StatusBarFragment extends Fragment {

    @Override
    public View onCreateView (final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View rootView = inflater.inflate (R.layout.status_bar, container, false);
        return rootView;
    }
}