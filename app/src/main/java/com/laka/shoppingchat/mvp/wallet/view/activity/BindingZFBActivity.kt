package com.laka.shoppingchat.mvp.wallet.view.activity

import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.wallet.constract.IWalletConstract

class BindingZFBActivity : BaseMvpActivity<String>(){

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun setContentView(): Int = R.layout.activitu_binding_zfb

    override fun initIntent() {

    }

    override fun initViews() {

    }

    override fun initEvent() {

    }

    override fun createPresenter(): IBasePresenter<*>? {
        return null
    }

    override fun initData() {

    }
}