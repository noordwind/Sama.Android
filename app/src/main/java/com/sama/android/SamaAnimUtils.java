package com.sama.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;

/**
 * Created by adriankremski on 19/05/2018.
 */

public class SamaAnimUtils {

    public static void overrideEnterTransitionWithHorizontalSlide(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.scale_down);
    }

    public static void overrideExitTransitionWithHorizontalSlide(Activity activity) {
        activity.overridePendingTransition(R.anim.scale_up, R.anim.slide_out_right);
    }
}
