package org.toilelibre.libe.singin.transition;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by lionel on 17/09/16.
 */
public class BlinkAnimation extends Animation {

    public BlinkAnimation() {
        setDuration(3000);
    }

    @Override
    protected void applyTransformation(float x, Transformation t) {
        t.setAlpha((float) ((Math.cos(2-2/x)+1)/2));
    }

    @Override
    public boolean willChangeTransformationMatrix() {
        return false;
    }

    @Override
    public boolean willChangeBounds() {
        return false;
    }

}
