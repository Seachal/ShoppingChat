package com.laka.shoppingchat.mvp.nim.activity

import android.content.Intent
import android.os.Build
import android.support.v4.content.ContextCompat
import ch.ielse.view.SwitchView
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.nim.blacklist.activity.BlackListActivity
import com.laka.shoppingchat.mvp.nim.model.bean.ExpansionBean
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.activity_privacy.*
import org.jetbrains.anko.startActivity
import com.laka.androidlib.net.utils.parse.GsonUtil
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.mvp.login.model.event.UserEvent
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.laka.shoppingchat.mvp.user.helper.UserUpdateHelper
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum


class PrivacyActivity : BaseActivity() {
    var userInfo: NimUserInfo? = null
    override fun setContentView(): Int = R.layout.activity_privacy

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
        userInfo = NimUIKit.getUserInfoProvider().getUserInfo(UserUtils.getImAccount()) as NimUserInfo?
        userInfo?.let {
            it.extension?.let {
                if (it.isEmpty()) {
                    verifyType.toggleSwitch(true)
                } else {
                    var expansionBean = GsonUtil.convertJson2Bean(it, ExpansionBean::class.java)
                    expansionBean?.let {
                        verifyType.toggleSwitch(it.validation)
                    }
                }
            }
        }
    }

    override fun initEvent() {
        black_list.onClick {
            startActivity<BlackListActivity>()
        }
        verifyType.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView) {
                changeVerify(true)
            }

            override fun toggleToOff(view: SwitchView) {
                changeVerify(false)
            }
        })
    }

    private fun changeVerify(verify: Boolean) {
        userInfo?.let {
            it.extension?.let {
                var expansionJson = ""
                if (it.isEmpty()) {
                    var expansionBean = ExpansionBean(verify)
                    expansionJson = GsonUtil.convertObject2Json(expansionBean)
                } else {
                    var expansionBean = GsonUtil.convertJson2Bean(it, ExpansionBean::class.java)
                    expansionBean.validation = verify
                    expansionJson = GsonUtil.convertObject2Json(expansionBean)
                }
                UserUpdateHelper.update(
                    UserInfoFieldEnum.EXTEND,
                    expansionJson,
                    object : RequestCallbackWrapper<Void>() {
                        override fun onResult(code: Int, p1: Void?, p2: Throwable?) {
                            if (code == ResponseCode.RES_SUCCESS.toInt()) {
                                verifyType.toggleSwitch(verify)
                            }
                        }
                    })
            }
        }
    }
}