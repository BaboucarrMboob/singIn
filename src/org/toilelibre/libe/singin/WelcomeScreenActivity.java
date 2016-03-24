package org.toilelibre.libe.singin;


import org.toilelibre.libe.singin.scenes.Transitions;

import com.skyfishjy.library.RippleBackground;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.View.OnClickListener;
import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeScreenActivity extends Activity {
    
    @Bind(R.id.btnRecordSound)
    @Nullable
    FloatingActionButton recordASound;
    @Bind(R.id.btnOpenProject)
    @Nullable
    FloatingActionButton openAProject;
    @Bind(R.id.rippleEar)
    @Nullable
    RippleBackground earAnim;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.welcome_screen);
        Transitions.welcomeScene (this);
        ButterKnife.bind (this);
        this.recordASound.setOnClickListener (new OnClickListener () {

            @Override
            public void onClick (View v) {
                WelcomeScreenActivity activity = WelcomeScreenActivity.this;
                Transitions.recordScene (activity);
                ButterKnife.bind (activity);
                activity.earAnim.startRippleAnimation ();
            }
            
        });
        this.openAProject.setOnClickListener (new OnClickListener () {

            @Override
            public void onClick (View v) {
                
            }
            
        });
    }
    
    

}
