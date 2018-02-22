package com.nilesh.lockbutton.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class AnimUtils {
    public static void scaleViewAnim(View v, float fromX, float toX, float fromY, float toY) {
        Animation anim = new ScaleAnimation(
                fromX, toX, // Start and end values for the X axis scaling
                fromY, toY, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        // anim.setDuration(Animation.INFINITE);
        v.startAnimation(anim);
    }
}
