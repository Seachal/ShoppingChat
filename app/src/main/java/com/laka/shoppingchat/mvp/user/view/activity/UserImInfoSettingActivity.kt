package com.laka.shoppingchat.mvp.user.view.activity

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.View
import ch.ielse.view.SwitchView
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.nim.event.BlackEvent
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.laka.shoppingchat.mvp.user.helper.ImUserInfoHelper
import com.netease.nim.uikit.common.util.string.StringUtil
import kotlinx.android.synthetic.main.activity_user_im_info_setting.*


class UserImInfoSettingActivity : BaseActivity(), View.OnClickListener {

    private var account: String? = ""

    override fun setContentView(): Int {
        return R.layout.activity_user_im_info_setting
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_ededed), 0)
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun initIntent() {
        account = intent.getStringExtra(UserConstant.KEY_IM_ACCOUNT)
    }

    override fun initViews() {
        title_bar.setTitle("资料设置")
            .showDivider(false)
            .setBackGroundColor(R.color.color_ededed)
            .setTitleTextColor(R.color.black)
            .setRightTextColor(R.color.black)
            .setLeftIcon(R.drawable.selector_nav_btn_back)
    }

    override fun initData() {
        var alias = ImUserInfoHelper.getDispalyNickname("$account")
        if (!StringUtil.isEmpty(alias)) {
            pnb_marks.setRightText(alias)
        }
        val isBlackList = ImUserInfoHelper.isInBlackList("$account")
        if (isBlackList) {
            sv_add_blacklist.toggleSwitch(true)
        } else {
            sv_add_blacklist.toggleSwitch(false)
        }
    }

    override fun initEvent() {
        pnb_marks.setOnClickListener(this)
        pnb_complaint.setOnClickListener(this)
        sv_add_blacklist.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                startLoading()
                ImUserInfoHelper.addToBlackList("$account", {
                    stopLoading()
                    sv_add_blacklist.toggleSwitch(true)
                    ToastHelper.showToast("设置成功")
                    EventBusManager.postEvent(BlackEvent(true, account!!))
                }, {
                    stopLoading()
                    sv_add_blacklist.toggleSwitch(false)
                    ToastHelper.showToast("设置失败")
                })
            }

            override fun toggleToOff(view: SwitchView?) {
                startLoading()
                ImUserInfoHelper.removeFromBlackList("$account", {
                    stopLoading()
                    sv_add_blacklist.toggleSwitch(false)
                    ToastHelper.showToast("设置成功")
                    EventBusManager.postEvent(BlackEvent(false, account!!))
                }, {
                    stopLoading()
                    sv_add_blacklist.toggleSwitch(true)
                    ToastHelper.showToast("设置失败")
                })
            }
        })
    }

    private fun stopLoading() {
        iv_loading_add_blacklist.visibility = View.GONE
        (iv_loading_add_blacklist.drawable as AnimationDrawable).stop()
    }

    private fun startLoading() {
        iv_loading_add_blacklist.visibility = View.VISIBLE
        (iv_loading_add_blacklist.drawable as AnimationDrawable).start()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pnb_marks -> {
                UserCenterModuleNavigator.startSettingRemarksActivityForResult(
                    this,
                    "$account",
                    ImUserInfoHelper.getDispalyNickname("$account"),
                    UserConstant.REQUEST_CODE_ALIAS
                )
            }
            R.id.pnb_complaint -> {
                UserCenterModuleNavigator.startComplaintListActivity(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UserConstant.REQUEST_CODE_ALIAS) {
            initData()
        }
    }


}