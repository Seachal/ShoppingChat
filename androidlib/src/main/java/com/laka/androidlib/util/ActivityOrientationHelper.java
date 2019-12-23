package com.laka.androidlib.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;

public class ActivityOrientationHelper {

    //竖屏
    public static void setActivityPortrait(Activity activity) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    //横屏
    public static void setActivityLandscape(Activity activity) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
