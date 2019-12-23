package com.laka.shoppingchat.mvp.wallet.view.activity

import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.shoppingchat.R

/**
 * @Author:summer
 * @Date:2019/9/9
 * @Description:我的支付密码
 */
class MyPayPsdActivity : BaseMvpActivity<String>() {

    override fun setContentView(): Int {
        return R.layout.activity_my_psd
    }

    override fun initIntent() {

    }

    override fun initViews() {

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