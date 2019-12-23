package com.netease.nim.uikit.common.media.imagepicker.ui;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.widget.Toast;

import com.laka.androidlib.util.StatusBarUtil;
import com.netease.nim.uikit.common.activity.UI;

public abstract class ImageBaseActivity extends UI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, com.laka.androidlib.R.color.white), 0);
            StatusBarUtil.setLightModeNotFullScreen(this, true);
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, com.laka.androidlib.R.color.black), 0);
        }
    }

    public boolean checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void showToast(String toastText) {
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMemoryCache();
    }

    public abstract void clearRequest();

    public abstract void clearMemoryCache();

    protected void asFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
