package org.toilelibre.libe.singin;

import java.util.Locale;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

public class WelcomeScreenActivity extends Activity {
    
    @Bind (R.id.btn_record_cancel_button)
    @Nullable
    FloatingActionButton cancelRecord;
    @Bind (R.id.btn_record_sound)
    @Nullable
    FloatingActionButton recordASound;
    @Bind (R.id.btn_open_project)
    @Nullable
    FloatingActionButton openAProject;
    @Bind (R.id.earAnim)
    @Nullable
    ImageView            earAnimPicture;
    @Bind (R.id.rippleEar)
    @Nullable
    RippleBackground     earAnim;
    @Bind (R.id.ready_textview)
    @Nullable
    TextView             readyText;
    @Bind (R.id.countdown_textview)
    @Nullable
    ShimmerTextView      countdownText;
    @Bind (R.id.velocimeter)
    @Nullable
    VelocimeterView      velocimeterView;
    @Bind (R.id.btn_validate_record_button)
    @Nullable
    FloatingActionButton validateSoundRecord;
    @Bind (R.id.reveal_record_frame_layout)
    @Nullable
    RevealFrameLayout    revealFrameLayout;
    @Bind (R.id.edition_screen)
    @Nullable
    LinearLayout         editionScreenLayout;
    @Bind (R.id.edition_screen_text_view)
    @Nullable
    TextView             endOfRecordingTextView;
    @Bind (R.id.timer_textview)
    @Nullable
    TextView             timerTextView;
    
    private Sound  sound;
    private Object stopRecording = new Object ();

    private Timer            recordTimer = null;
    private Timer            countdownTimer = null;
    private Handler          handler   = null;
    private Shimmer          shimmer;
    private long             recordStartTimestamp;
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
        landOnRecordSceneAnimation ();
        this.cancelRecord.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick (View v) {
                cancelRecordAnimation ();
                WelcomeScreenActivity.this.onWelcome ();
            }
            
        });
        this.validateSoundRecord.setOnClickListener (new OnClickListener () {
            @Override
            public void onClick (View v) {
                endOfRecordingAnimation ();
            }
        });
    }
    
    private void startTimerForSoundRecording () {
        this.cancelTimer ();
        this.countdownTimer = new Timer ();
        this.recordTimer = new Timer ();
        this.handler = new Handler ();
        this.shimmer = new Shimmer ();
        this.timerTextView.setText (this.getText (R.string.zerozero));
        shimmer.start (this.countdownText);
        this.countdownTimer.scheduleAtFixedRate (new TimerTask () {
            int occurence = WelcomeScreenActivity.COUNTDOWN;
            
            @Override
            public void run () {
                if (occurence == 0) {
                    new Thread () {
                        public void run () {
                            WelcomeScreenActivity.this.startRecording ();
                        }
                    }.start ();
                    WelcomeScreenActivity.this.handler.post (new Runnable () {
                        public void run () {
                            startRecordingAnimation ();
                        }
                    });
                    this.cancel ();
                } else {
                    occurence--;
                    WelcomeScreenActivity.this.handler.post (new Runnable () {
                        public void run () {
                            WelcomeScreenActivity.this.countdownText.setText ("" + (occurence + 1));
                        }
                    });
                }
            }
            
        }, 0, 1000);
    }
    
    private void cancelTimer () {
        if (this.countdownTimer != null) {
            this.countdownTimer.cancel ();
            this.recordTimer.cancel ();
            this.shimmer.cancel ();
            this.countdownTimer = null;
            this.shimmer = null;
        }
        
    }
    
    private void startRecording () {
        try {
            this.sound = FluentClient.start ().whileRecordingASound (new StreamInfo (1, -1, 2, 8000, false, true, null), this.stopRecording, new AmplitudeObserver () {
                @Override
                public void update (final float soundLevel) {
                    WelcomeScreenActivity.this.handler.post (new Runnable () {
                        public void run () {
                            VelocimeterView view = WelcomeScreenActivity.this.velocimeterView;
                            if (view != null) {
                                WelcomeScreenActivity.this.velocimeterView.setValue (soundLevel, false);
                            }
                        }
                    });
                }
            }).stopWithSound ();
            this.recordStartTimestamp = System.currentTimeMillis () - 1000;
            this.countdownTimer.scheduleAtFixedRate (new TimerTask () {
                @Override
                public void run () {
                    int secs = (int) Math.round ((System.currentTimeMillis () - WelcomeScreenActivity.this.recordStartTimestamp * 1.0) / 1000);
                    final String minutes = String.format (Locale.getDefault (), "%02d", secs / 60);
                    final String seconds = String.format (Locale.getDefault (), "%02d", secs % 60);
                    WelcomeScreenActivity.this.handler.post (new Runnable () {
                        public void run () {
                            WelcomeScreenActivity.this.timerTextView.setText (minutes + ":" + seconds);
                        }
                    });
                }
            }, 0, 1000);
        } catch (SoundTransformException e) {
            synchronized (this.stopRecording) {
                this.stopRecording.notifyAll ();
            }
            throw new RuntimeException (e);
        }
    }

    private void landOnRecordSceneAnimation () {
        this.startTimerForSoundRecording ();
        this.earAnimPicture.setVisibility (View.VISIBLE);
        this.cancelRecord.setVisibility (View.VISIBLE);
        this.validateSoundRecord.setVisibility (View.INVISIBLE);
        this.velocimeterView.setVisibility (View.INVISIBLE);
        this.timerTextView.setVisibility (View.INVISIBLE);
        this.editionScreenLayout.setVisibility (View.INVISIBLE);
        this.readyText.setText (R.string.ready);
    }
    
    private void cancelRecordAnimation () {
        this.validateSoundRecord.setVisibility (View.INVISIBLE);
        this.velocimeterView.setVisibility (View.INVISIBLE);
        this.endOfRecordingTextView.setText (R.string.nevermind);
        this.countdownText.setVisibility (View.INVISIBLE);
        this.earAnimPicture.setVisibility (View.INVISIBLE);
        this.timerTextView.setVisibility (View.INVISIBLE);
        this.cancelTimer ();
        synchronized (this.stopRecording) {
            this.stopRecording.notifyAll ();
        }
        this.earAnim.stopRippleAnimation ();
    }
    
    private void startRecordingAnimation () {
        this.readyText.setText (R.string.sing_now);
        this.countdownText.setText ("");
        this.validateSoundRecord.setVisibility (View.VISIBLE);
        this.velocimeterView.setVisibility (View.VISIBLE);
        this.timerTextView.setVisibility (View.VISIBLE);
        this.velocimeterView.setProgress (new DecelerateInterpolator (10));
        this.earAnim.startRippleAnimation ();
    }

    private void endOfRecordingAnimation () {
        cancelRecordAnimation();
        this.validateSoundRecord.setVisibility (View.VISIBLE);
        this.cancelRecord.setVisibility (View.INVISIBLE);
        endOfRecordingTextView.setText (R.string.impressive);
        editionScreenLayout.setVisibility (View.VISIBLE);
        int sizeOfTheButton = validateSoundRecord.getWidth ();
        int screenDiagonal = (int) Math.sqrt (Math.pow (editionScreenLayout.getWidth (), 2) + Math.pow (editionScreenLayout.getHeight (), 2));
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal (editionScreenLayout, editionScreenLayout.getWidth () - sizeOfTheButton / 2,
                editionScreenLayout.getHeight () - sizeOfTheButton / 2, sizeOfTheButton, screenDiagonal);
        animator.setInterpolator (new AccelerateDecelerateInterpolator ());
        animator.setDuration (1500);
        animator.start ();
    }
    
}
