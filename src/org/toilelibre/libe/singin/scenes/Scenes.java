package org.toilelibre.libe.singin.scenes;

import org.toilelibre.libe.singin.R;

import com.transitionseverywhere.Scene;

import android.app.Activity;
import android.view.ViewGroup;

public class Scenes {

    public static Scene welcomeScene (Activity targetActivity) {
        ViewGroup sceneViewGroup = (ViewGroup) ((ViewGroup)targetActivity.findViewById(android.R.id.content)).getChildAt (0);
        return Scene.getSceneForLayout (sceneViewGroup, R.layout.welcome_scene, targetActivity);
    }
    
    public static Scene recordScene (Activity targetActivity) {
        ViewGroup sceneViewGroup = (ViewGroup) ((ViewGroup)targetActivity.findViewById(android.R.id.content)).getChildAt (0);
        return Scene.getSceneForLayout (sceneViewGroup, R.layout.record_scene, targetActivity);
    }


}
