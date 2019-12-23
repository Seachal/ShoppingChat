package com.laka.shoppingchat.mvp.wallet.view.activity

import android.os.Build
import android.support.v4.content.ContextCompat
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import kotlinx.android.synthetic.main.activity_add_bank_card.*

class AddBankCardActivity : BaseMvpActivity<String>() {
    override fun setContentView(): Int = R.layout.activity_add_bank_card

    override fun initIntent() {
    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setTitle("我的银行卡")
            .setBackGroundColor(R.color.color_ededed)
            .setTitleTextColor(R.color.color_2d2d2d)
            .showDivider(false)
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
            super.setStatusBarColor(color)
        }
    }

    override fun initData() {
    }

    override fun initEvent() {
    }

    override fun showData(data: String) {
    }

    override fun showErrorMsg(msg: String?) {
    }

    override fun createPresenter(): IBasePresenter<*>? {
        return null
    }
}