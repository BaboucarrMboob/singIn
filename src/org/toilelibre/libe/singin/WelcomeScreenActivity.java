package org.toilelibre.libe.singin;

import org.toilelibre.libe.singin.R;

import com.jjoe64.graphview.GraphView;
import com.transitionseverywhere.Explode;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Scene;
import com.transitionseverywhere.TransitionManager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeScreenActivity extends Activity {


    private Scene welcomeScene;

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.welcome_screen);
        welcomeScene = Scene.getSceneForLayout((ViewGroup)this.findViewById (R.id.welcome_screen), R.layout.welcome_scene, this);
        TransitionManager.go(welcomeScene, new Explode());
    }

}
