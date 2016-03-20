package org.toilelibre.libe.singin;


import org.toilelibre.libe.singin.scenes.Transitions;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.View.OnClickListener;
import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeScreenActivity extends Activity {
    
    @Bind(R.id.btnRecordSound)
    FloatingActionButton recordASound;
    @Bind(R.id.btnOpenProject)
    FloatingActionButton openAProject;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.welcome_screen);
        Transitions.welcomeScene (this);
        ButterKnife.bind (this);
        this.recordASound.setOnClickListener (new OnClickListener () {

            @Override
            public void onClick (View v) {
                Transitions.recordScene (WelcomeScreenActivity.this);
            }
            
        });
        this.openAProject.setOnClickListener (new OnClickListener () {

            @Override
            public void onClick (View v) {
                
            }
            
        });
    }
    
    

}
