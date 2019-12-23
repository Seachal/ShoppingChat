package com.laka.shoppingchat.mvp.nim.activity

import android.Manifest
import android.os.Build
import android.support.v4.content.ContextCompat
import ch.ielse.view.SwitchView
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.ActivityManager
import com.laka.androidlib.util.CacheUtil
import com.laka.androidlib.util.PermissionUtils
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.dialog.CommonConfirmDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.main.constant.HomeApiConstant
import com.laka.shoppingchat.mvp.main.view.activity.MainActivity
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import com.laka.shoppingchat.mvp.user.constract.UserSettingConstract
import com.laka.shoppingchat.mvp.user.presenter.UserSettingPresenter
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import kotlinx.android.synthetic.main.activity_general.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class GeneralActivity : BaseMvpActivity<String>(), UserSettingConstract.IUserSettingView {
    private lateinit var mPresenter: UserSettingPresenter
    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {
    }

    override fun createPresenter(): IBasePresenter<*> {
        mPresenter = UserSettingPresenter()
        return mPresenter
    }

    private var mLogoutConfirmDialog: CommonConfirmDialog? = null
    override fun setContentView(): Int = R.layout.activity_general

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
        if (PermissionUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            tv_clear_cache.text = CacheUtil.getTotalCacheSize(this)
        } else {
            tv_clear_cache.text = "未知（没有权限）"
        }
        var phoneModeEnable = NimUIKit.isEarPhoneModeEnable()
        sv_notify_type.isOpened = phoneModeEnable
    }

    override fun initEvent() {
        ll_privacy_policy.onClick {
            UserCenterModuleNavigator.startPrivacyPolicyActivity(this)
        }
        ll_about_us.onClick {
            UserCenterModuleNavigator.startAboutUsActivity(this)
        }
        ll_clear_cache.onClick {
            if (!PermissionUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                PermissionUtils.requestPermission(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                return@onClick
            }
            if (isFinishing || isDestroyed) {
                return@onClick
            }
            val commonConfirmDialog = CommonConfirmDialog(this)
            commonConfirmDialog?.show()
            commonConfirmDialog?.setDefaultTitleTxt("您确定要清除缓存吗？")
            commonConfirmDialog?.setOnClickSureListener {
                onClearCache()
            }
        }
        tv_logout.onClick {
            if (mLogoutConfirmDialog == null) {
                mLogoutConfirmDialog = CommonConfirmDialog(this)
                mLogoutConfirmDialog?.setDefaultTitleTxt("您确定要退出吗？")
                mLogoutConfirmDialog?.setOnClickSureListener {
                    onLogout()
                }
            }
            mLogoutConfirmDialog?.show()
        }
        ll_clear_chat_record.onClick {
            val commonConfirmDialog = CommonConfirmDialog(this)
            commonConfirmDialog?.show()
            commonConfirmDialog?.setDefaultTitleTxt("您确定要清除聊天记录吗？")
            commonConfirmDialog?.setOnClickSureListener {
                NIMClient.getService(MsgService::class.java).clearMsgDatabase(true)
                toast(R.string.clear_msg_history_success)
            }
        }
        sv_notify_type.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView) {
                NimUIKit.setEarPhoneModeEnable(true)
                sv_notify_type.toggleSwitch(true)
            }

            override fun toggleToOff(view: SwitchView) {
                NimUIKit.setEarPhoneModeEnable(false)
                sv_notify_type.toggleSwitch(false)
            }
        })
        ll_font_size.onClick {
            startActivity<FontSizeActivity>()
        }
    }

    private fun onClearCache() {
        if (tv_clear_cache.text.toString().trim() == "0M") {
            ToastHelper.showToast("没有可清理的缓存哦")
            return
        }
        CacheUtil.clearAllCache(this)
        ToastHelper.showCenterToast("缓存清除成功！")
        tv_clear_cache.text = CacheUtil.getTotalCacheSize(this)
    }

    private fun onLogout() {

        mPresenter.onLogout()
    }

    override fun onLogoutSuccess() {
        ActivityManager.getInstance().finishActivity(NimSettingActivity::class.java)
        MainActivity.logout(this@GeneralActivity, false)
        finish()
    }
}