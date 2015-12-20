package com.romens.yjk.health.helper;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Created by siery on 15/12/19.
 */
public class AnimHelper {
    public static void addFavoritesAnim(View view) {
        ScaleAnimation a1 = new ScaleAnimation(1f, 1.1f, 1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f);
        a1.setRepeatCount(1);
        a1.setDuration(500);
        view.startAnimation(a1);
    }
}
