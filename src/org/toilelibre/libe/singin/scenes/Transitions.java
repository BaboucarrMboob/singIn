package org.toilelibre.libe.singin.scenes;

import java.util.HashMap;
import java.util.Map;

import com.transitionseverywhere.Explode;
import com.transitionseverywhere.Scene;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;

import android.app.Activity;

public class Transitions {
    
    private static final String            WELCOME_SCENE = "welcomeScene";
    private static final String RECORD_SCENE = "recordScene";
    private static Map<String, Transition> TRANSITIONS   = new HashMap<String, Transition> ();
    
    public static Transition welcomeScene (Activity activity) {
        Scene welcomeScene = Scenes.welcomeScene (activity);
        Transition transitionSet = TRANSITIONS.get (WELCOME_SCENE);
        if (transitionSet == null) {
//            transitionSet = new TransitionSet ();
//            Slide slide = new Slide (Gravity.START);
//            Explode explode = new Explode ();
//            transitionSet.addTarget (org.toilelibre.libe.singin.R.id.welcome_scene);
//            slide.addTarget (org.toilelibre.libe.singin.R.id.welcome_back);
//            explode.addTarget (org.toilelibre.libe.singin.R.id.welcome_actions);
//            ((TransitionSet)transitionSet).addTransition (slide);
//            ((TransitionSet)transitionSet).addTransition (explode);
//            ((TransitionSet)transitionSet).setOrdering (TransitionSet.ORDERING_TOGETHER);
//            ((TransitionSet)transitionSet).setDuration (5000);
            transitionSet = new Explode ();
            TRANSITIONS.put (WELCOME_SCENE, transitionSet);
        }
        TransitionManager.go (welcomeScene, transitionSet);
        return transitionSet;
    }

    public static Transition recordScene (Activity activity) {
        Scene recordScene = Scenes.recordScene (activity);
        Transition transitionSet = TRANSITIONS.get (RECORD_SCENE);
        if (transitionSet == null) {
            transitionSet = new Slide ();
            TRANSITIONS.put (RECORD_SCENE, transitionSet);
        }
        TransitionManager.go (recordScene, transitionSet);
        return transitionSet;
        
    }
    
}
