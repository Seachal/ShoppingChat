package com.laka.shoppingchat.mvp.user.view.activity

import android.text.Editable
import android.text.TextWatcher
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.ActivityManager
import com.laka.androidlib.util.SPHelper
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.extraDelegate
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.model.event.UserEvent
import com.laka.shoppingchat.mvp.user.constant.UserCenterConstant
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.laka.shoppingchat.mvp.user.constract.UserSettingConstract
import com.laka.shoppingchat.mvp.user.helper.UserUpdateHelper
import com.laka.shoppingchat.mvp.user.presenter.UserSettingPresenter
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum
import kotlinx.android.synthetic.main.activity_verify_code_new.*


class VerifyCodeActivity : BaseMvpActivity<String>(), UserSettingConstract.IUserSettingView {
    val mobile by extraDelegate(UserCenterConstant.MOBILE, "")
    override fun setContentView(): Int = R.layout.activity_verify_code_new

    override fun initIntent() {
    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setTitleTextColor(R.color.color_2d2d2d)
            .setTitle("填写验证码")
        tv_mobile.text = mobile
    }

    override fun initData() {
    }

    override fun initEvent() {
        sb_finish.onClick {
            var code = et_verify.text.toString()
            if (code.length == 6) {
                mPresenter.changeMobile(mobile, code)
            } else {
                ToastHelper.showToast("请输入正确的验证码")
            }
        }
        et_verify.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length != 6) {
                    sb_finish.setBgaColor("#9CE6BF")
                } else {
                    sb_finish.setBgaColor("#07C160")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    override fun showData(data: String) {
    }

    override fun showErrorMsg(msg: String?) {
    }

    lateinit var mPresenter: UserSettingPresenter
    override fun createPresenter(): IBasePresenter<*>? {
        mPresenter = UserSettingPresenter()
        mPresenter.setView(this)
        return mPresenter
    }

    override fun onLogoutSuccess() {
        ActivityManager.getInstance().finishActivity(CurrentPhoneActivity::class.java)
        ActivityManager.getInstance().finishActivity(UserSettingActivity::class.java)
        finish()
    }

    override fun modifyPhoneSuccess() {
        UserUpdateHelper.update(
            UserInfoFieldEnum.MOBILE,
            mobile,
            object : RequestCallbackWrapper<Void>() {
                override fun onResult(code: Int, p1: Void?, p2: Throwable?) {
                    if (code == ResponseCode.RES_SUCCESS.toInt()) {
                        ToastHelper.showCenterToast("修改成功")
                        EventBusManager.postEvent(UserEvent(UserConstant.EDIT_USER_INFO))
                        ActivityManager.getInstance().finishActivity(CurrentPhoneActivity::class.java)
                        ActivityManager.getInstance().finishActivity(UserSettingActivity::class.java)
                        UserUtils.updateMobile(mobile)
                        finish()
                    } else {
                        ToastHelper.showCenterToast("修改失败")
                    }
                }
            })
    }

}