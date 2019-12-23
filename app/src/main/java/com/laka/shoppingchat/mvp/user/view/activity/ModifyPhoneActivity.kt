package com.laka.shoppingchat.mvp.user.view.activity

import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StringUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.laka.shoppingchat.mvp.user.constant.UserCenterConstant
import com.laka.shoppingchat.mvp.user.constract.UserSettingConstract
import com.laka.shoppingchat.mvp.user.presenter.UserSettingPresenter
import kotlinx.android.synthetic.main.activity_modify_phone.*
import org.jetbrains.anko.startActivity

class ModifyPhoneActivity : BaseMvpActivity<String>(), UserSettingConstract.IUserSettingView {

    private var mMobile: String = ""
    override fun setContentView(): Int = R.layout.activity_modify_phone

    override fun initIntent() {
        mMobile = intent.getStringExtra("mobile")
    }

    override fun initViews() {
        title_bar
            .setLeftText("取消")
            .setTitle("更换手机号")
            .setRightTextBg(R.drawable.bg_send_friend_bg)
            .setRightTextColor(R.color.white)
            .setRightText("完成")
            .setTitleTextColor(R.color.color_2d2d2d)
            .setOnRightClickListener {
                onSave()
            }
        tv_mobile.text = "$mMobile"
    }

    private fun onSave() {
        val mobile = et_mobile.text.toString()
        if (StringUtils.isEmpty(mobile)) {
            ToastHelper.showCenterToast("请输入手机号")
            return
        }
        if (!mobile.startsWith("1") || mobile.length != 11) {
            ToastHelper.showCenterToast("请输入正确格式的手机号")
            return
        }

//        UserUpdateHelper.update(
//            UserInfoFieldEnum.MOBILE,
//            mobile,
//            object : RequestCallbackWrapper<Void>() {
//                override fun onResult(code: Int, p1: Void?, p2: Throwable?) {
//                    if (code == ResponseCode.RES_SUCCESS.toInt()) {
//                        EventBusManager.postEvent(UserEvent(UserConstant.EDIT_USER_INFO))
//                        ToastHelper.showCenterToast("修改成功")
//                        finish()
//                    } else {
//                        ToastHelper.showCenterToast("修改手机号失败，请稍后重试")
//                    }
//                }
//            })
        mPresenter.sendCode(mobile,"changemobile")
    }

    override fun sendCodeSuccess() {
        ToastHelper.showToast("验证码发送成功")
        val mobile = et_mobile.text.toString()
        startActivity<VerifyCodeActivity>(
            UserCenterConstant.MOBILE to mobile
        )
        finish()
    }

    override fun initData() {
    }

    override fun initEvent() {
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

}