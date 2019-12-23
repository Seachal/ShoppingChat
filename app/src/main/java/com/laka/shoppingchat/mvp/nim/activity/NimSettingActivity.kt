package com.laka.shoppingchat.mvp.nim.activity

import android.os.Build
import android.support.v4.content.ContextCompat
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import kotlinx.android.synthetic.main.activity_nim_setting.*
import org.jetbrains.anko.startActivity

class NimSettingActivity : BaseActivity() {
    override fun setContentView(): Int = R.layout.activity_nim_setting
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

    override fun initIntent() {
    }


    override fun initData() {
    }

    override fun initEvent() {
        general_setting.onClick {
            UserCenterModuleNavigator.startGeneralActivity(this)
        }
        message_setting.onClick {
            UserCenterModuleNavigator.startMessageSettingActivity(this)
        }
        privacy_setting.onClick {
            startActivity<PrivacyActivity>()
        }
    }
}