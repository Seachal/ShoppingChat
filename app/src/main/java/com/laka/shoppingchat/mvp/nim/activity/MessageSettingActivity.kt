package com.laka.shoppingchat.mvp.nim.activity

import android.os.Build
import android.support.v4.content.ContextCompat
import ch.ielse.view.SwitchView
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.nim.preference.UserPreferences
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nim.uikit.common.ToastHelper
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.mixpush.MixPushService
import kotlinx.android.synthetic.main.activity_message_setting.*

class MessageSettingActivity : BaseActivity() {

    override fun setContentView(): Int = R.layout.activity_message_setting

    override fun initIntent() {
    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setBackGroundColor(R.color.color_ededed)
            .showDivider(false)
            .setOnLeftClickListener {
                finish()
            }
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(
                this,
                ContextCompat.getColor(this, R.color.color_ededed),
                0
            )
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun initData() {
        vibrateNotify.toggleSwitch(UserPreferences.getVibrateToggle())
        msgNotify.toggleSwitch(UserPreferences.getNotificationToggle())
        voiceNotify.toggleSwitch(UserPreferences.getRingToggle())
    }

    override fun initEvent() {
        msgNotify.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView) {
                setToggleNotification(true)
            }

            override fun toggleToOff(view: SwitchView) {
                setToggleNotification(false)
            }
        })
        voiceNotify.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView) {
                setRing(true)
            }

            override fun toggleToOff(view: SwitchView) {
                setRing(false)
            }
        })
        vibrateNotify.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView) {
                setVibrate(true)
            }

            override fun toggleToOff(view: SwitchView) {
                setVibrate(false)
            }
        })
    }

    private fun setVibrate(checkState: Boolean) {
        vibrateNotify.toggleSwitch(checkState)
        UserPreferences.setVibrateToggle(checkState)
        val config = UserPreferences.getStatusConfig()
        config.vibrate = checkState
        UserPreferences.setStatusConfig(config)
        NIMClient.updateStatusBarNotificationConfig(config)
    }

    private fun setRing(checkState: Boolean) {
        voiceNotify.toggleSwitch(checkState)
        UserPreferences.setRingToggle(checkState)
        val config = UserPreferences.getStatusConfig()
        config.ring = checkState
        UserPreferences.setStatusConfig(config)
        NIMClient.updateStatusBarNotificationConfig(config)
    }



    private fun setToggleNotification(checkState: Boolean) {
        try {
            msgNotify.toggleSwitch(checkState)
            setNotificationToggle(checkState)
            NIMClient.toggleNotification(checkState)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setNotificationToggle(on: Boolean) {
        UserPreferences.setNotificationToggle(on)
    }
}