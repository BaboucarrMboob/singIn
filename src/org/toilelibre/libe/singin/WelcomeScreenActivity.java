package org.toilelibre.libe.singin;


import java.util.Timer;
import java.util.TimerTask;

import org.toilelibre.libe.singin.scenes.Transitions;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.record.AmplitudeObserver;

import com.github.glomadrian.velocimeterlibrary.VelocimeterView;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.skyfishjy.library.RippleBackground;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

public class WelcomeScreenActivity extends Activity {

    @Bind(R.id.btn_record_cancel_button)
    @Nullable
    FloatingActionButton cancelRecord;
    @Bind(R.id.btn_record_sound)
    @Nullable
    FloatingActionButton recordASound;
    @Bind(R.id.btn_open_project)
    @Nullable
    FloatingActionButton openAProject;
    @Bind(R.id.rippleEar)
    @Nullable
    RippleBackground earAnim;
    @Bind(R.id.ready_textview)
    @Nullable
    TextView readyText;
    @Bind(R.id.countdown_textview)
    @Nullable
    ShimmerTextView countdownText;
    @Bind(R.id.velocimeter)
    @Nullable
    VelocimeterView velocimeterView;
    @Bind(R.id.btn_validate_record_button)
    @Nullable
    FloatingActionButton validateSoundRecord;
    @Bind(R.id.reveal_record_frame_layout)
    @Nullable
    RevealFrameLayout revealFrameLayout;
    @Bind(R.id.edition_screen)
    @Nullable
    LinearLayout editionScreenLayout;
    
    private Sound  sound;
    private Object stopRecording = new Object ();
    
    private Timer timer = null;
    private Handler handler = null;
    private Shimmer shimmer;
    private static final int COUNTDOWN = 5;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.welcome_screen);
        this.onWelcome ();
    }
    
    private void onWelcome () {
        Transitions.welcomeScene (this);
        ButterKnife.bind (this);
        this.recordASound.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick (View v) {
                WelcomeScreenActivity.this.onRecordSound ();
            }
            
        });
        this.openAProject.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick (View v) {
            }
            
        });
    }

    private void onRecordSound () {
        Transitions.recordScene (this);
        ButterKnife.bind (this);
        this.startTimerForSoundRecording ();
        this.velocimeterView.setVisibility (View.INVISIBLE);
        this.editionScreenLayout.setVisibility (View.INVISIBLE);
        this.readyText.setText (R.string.ready);
        this.cancelRecord.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick (View v) {
                WelcomeScreenActivity.this.cancelTimer();
                synchronized (WelcomeScreenActivity.this.stopRecording) {
                    WelcomeScreenActivity.this.stopRecording.notifyAll ();
                }
                WelcomeScreenActivity.this.earAnim.stopRippleAnimation ();
                WelcomeScreenActivity.this.onWelcome ();
            }
            
        });
        this.validateSoundRecord.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick (View v) {
                editionScreenLayout.setVisibility (View.VISIBLE);
                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(editionScreenLayout, 200, 200, 0, 1000);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(1500);
                animator.start();
            }
            
        });
    }

    private void startTimerForSoundRecording () {
        this.cancelTimer ();
        this.timer = new Timer ();
        this.handler = new Handler ();
        this.shimmer = new Shimmer();
        shimmer.start(this.countdownText);
        this.timer.scheduleAtFixedRate (new TimerTask () {
            int occurence = WelcomeScreenActivity.COUNTDOWN;
            @Override
            public void run () {
                if (occurence == 0) {
                    new Thread () {public void run () {WelcomeScreenActivity.this.startRecording ();}}.start();
                    WelcomeScreenActivity.this.handler.post (new Runnable () {
                        public void run () {
                            WelcomeScreenActivity.this.readyText.setText (R.string.sing_now);
                            WelcomeScreenActivity.this.countdownText.setText ("");
                            WelcomeScreenActivity.this.velocimeterView.setVisibility (View.VISIBLE);
                            WelcomeScreenActivity.this.velocimeterView.setProgress (new DecelerateInterpolator (10));
                            WelcomeScreenActivity.this.earAnim.startRippleAnimation ();
                    }});
                    this.cancel ();
                }else {
                    occurence--;
                    WelcomeScreenActivity.this.handler.post (new Runnable () {
                        public void run () {
                            WelcomeScreenActivity.this.countdownText.setText ("" + (occurence + 1));
                    }});
                }
            }
            
        } , 0, 1000);
    }

    protected void cancelTimer () {
        if (this.timer != null) {
            this.timer.cancel ();
            this.shimmer.cancel ();
            this.velocimeterView.setVisibility (View.INVISIBLE);
            this.timer = null;
            this.shimmer = null;
        }
        
    }
    
    protected void startRecording () {
        try {
            this.sound = FluentClient.start ().whileRecordingASound (
                    new StreamInfo (1, -1, 2, 8000, false, true, null), new AmplitudeObserver () {
                        @Override
                        public void update (final float soundLevel) {
                            WelcomeScreenActivity.this.handler.post (new Runnable () {
                                public void run () {
                                    VelocimeterView view = WelcomeScreenActivity.this.velocimeterView;
                                    if (view != null) {
                                        WelcomeScreenActivity.this.velocimeterView.setValue (soundLevel, false);
                                    }
                            }});
                        }
                    }, this.stopRecording).stopWithSound ();
        } catch (SoundTransformException e) {
            synchronized (this.stopRecording) {
                this.stopRecording.notifyAll ();
            }
            throw new RuntimeException (e);
        }
    }

}
