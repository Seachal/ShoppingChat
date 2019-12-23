package com.laka.shoppingchat.mvp.wallet.view.activity

import android.content.res.Resources
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.shoppingchat.R

/**
 * 作为中转activity调起支付，防止应用内部设置的字体大小影响到支付SDK里面的页面
 * */
class EmptyActivity : BaseActivity() {

    private var mIsAutoGetSms = false
    private var gatewayUrl: String = ""

    override fun setContentView(): Int {
        return R.layout.activity_emptry
    }

    override fun initIntent() {
        gatewayUrl = intent.getStringExtra("gatewayUrl")
    }

    override fun initViews() {

    }

    override fun initData() {
//        SecurePayService.securePay(
//            this,
//            gatewayUrl,
//            1, //正式
//            mResultListener,
//            mIsAutoGetSms
//        )
    }

    override fun initEvent() {

    }

    override fun getResources(): Resources {
        val res = super.getResources()
        val config = res.configuration
        if (ScreenUtils.getFontSizeScale() > 0.5) {
            config.fontScale = 1.0f//1 设置正常字体大小的倍数
        }
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

//    private var mResultListener: OnResultListener = OnResultListener { result ->
//        ToastHelper.showCenterToastLong("返回内容：\n$result")
//        finish()
//        //EventBusManager.postEvent(Event(WalletConstant.EVENT_RECHARGE_SUCCESS, -1))
//        //ToastHelper.showToast("充值成功")
//    }

}