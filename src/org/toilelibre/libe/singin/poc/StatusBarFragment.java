package org.toilelibre.libe.singin.poc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StatusBarFragment extends Fragment {


    public StatusBarFragment () {
    }
    
    @Override
    public View onCreateView (final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View rootView = inflater.inflate (savedInstanceState.getInt ("statusBarId"), container, false);
        return rootView;
    }
}