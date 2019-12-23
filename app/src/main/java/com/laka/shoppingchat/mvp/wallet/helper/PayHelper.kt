package com.laka.shoppingchat.mvp.wallet.helper

import android.content.Context
import android.os.Handler
import com.lianlian.wallet.WalletService
import com.lianlian.wallet.regist.RegistService
import com.secure.pay.PayService
import org.json.JSONObject

object PayHelper {

    fun getParamsBean(params: HashMap<String, Any>): JSONObject {
        var paramsJson = JSONObject()
        val keys = params.keys
        for (key in keys) {
            val value = params[key]
            paramsJson.put(key, value.toString())
        }
        return paramsJson
    }

    /**
     * 开户
     * */
    fun open(context: Context, handler: Handler, params: String, type: Int) {
        RegistService.open(context, handler, params, type)
    }

    /**
     * 钱包管理
     * */
    fun walletManager(context: Context, handler: Handler, params: String, type: Int) {
        WalletService.manager(context, handler, params, type)
    }

    /**
     * 支付
     * */
    fun onPay(context: Context, handler: Handler, params: String, type: Int) {
        // PayService.TYPE_NORMAL ：钱包版
        // PayService.TYPE_EASY ：简易版
        PayService.pay(context, handler, params, PayService.TYPE_NORMAL, type)
    }


}