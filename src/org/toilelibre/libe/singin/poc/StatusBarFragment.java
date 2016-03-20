package org.toilelibre.libe.singin.poc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StatusBarFragment extends Fragment {

    private int statusBarId;

    public StatusBarFragment (int statusBarId) {
        this.statusBarId = statusBarId;
    }
    
    @Override
    public View onCreateView (final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View rootView = inflater.inflate (statusBarId, container, false);
        return rootView;
    }
}