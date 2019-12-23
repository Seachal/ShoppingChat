package com.laka.shoppingchat.mvp.user.view.activity

import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.ext.onClick
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.laka.shoppingchat.mvp.user.constract.UserSettingConstract
import com.laka.shoppingchat.mvp.user.presenter.UserSettingPresenter
import kotlinx.android.synthetic.main.activity_current_phone.*
import org.jetbrains.anko.startActivity


class CurrentPhoneActivity : BaseMvpActivity<String>(), UserSettingConstract.IUserSettingView {

    private var mMobile: String = ""

    override fun setContentView(): Int = R.layout.activity_current_phone

    override fun initIntent() {
        mMobile = intent.getStringExtra("mobile")
    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setTitle("个人信息")
            .setTitleTextColor(R.color.color_2d2d2d)
    }

    override fun initData() {
        tvPhone.text = "您的手机号：$mMobile"
    }

    override fun initEvent() {
        btn_change_phone.onClick {
            startActivity<ModifyPhoneActivity>(
                "mobile" to "$mMobile"
            )
        }
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