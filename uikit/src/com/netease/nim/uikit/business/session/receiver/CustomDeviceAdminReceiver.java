package com.netease.nim.uikit.business.session.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import com.netease.nim.uikit.common.ToastHelper;

public class CustomDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        ToastHelper.showToast(context, "onEnabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        ToastHelper.showToast(context, "onDisabled");
    }
}
