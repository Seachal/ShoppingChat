package com.laka.shoppingchat.mvp.wallet.constract

import com.laka.androidlib.mvp.IBaseLoadingView
import com.laka.androidlib.mvp.IBaseModel
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.shoppingchat.common.dsl.RequestWrapper
import com.laka.shoppingchat.mvp.wallet.model.bean.MyWalletBean
import com.laka.shoppingchat.mvp.wallet.model.bean.RechargeResp
import com.laka.shoppingchat.mvp.wallet.model.bean.WalletInitResponse
import org.json.JSONObject

/**
 * @Author:summer
 * @Date:2019/1/19
 * @Description:
 */
interface IWalletConstract {

    interface IWalletView : IBaseLoadingView<MyWalletBean> {
        fun onLoadMyWalletSuccess(myWallet: MyWalletBean) {}
        fun onRechargeSuccess(orderNo: String) {}
        fun onPayPsdCheckSuccess() {}
        fun onPayPsdCheckFail(msg: String) {}
        fun onLoadPayTokenSuccess(token: String) {}
        fun onLoadPayTokenFail(msg: String) {}

        //支付
        fun walletInitSuccess(response: WalletInitResponse) {}
        fun walletInitFail(msg: String) {}
        fun walletManagerInitSuccess(response: WalletInitResponse) {}
        fun walletManagerInitFail(msg: String) {}
        fun walletStatusSuccess(status: Double,type:Int) {}
        fun walletStatusFail(msg: String) {}
    }

    interface IWalletPresenter : IBasePresenter<IWalletView> {
        fun onLoadMyWallet()
        fun onRecharge(amount: String, password: String)
        fun onPayPsdCheck(psd: String)
        fun onLoadPayToken(token: String)

        //支付
        fun walletInit()
        fun walletStatus(type:Int)
        fun walletManagerInit(flagPara: String)
    }

    interface IWalletModel : IBaseModel<IWalletView> {
        fun onLoadPayToken(requestWrapper: RequestWrapper<JSONObject>)
        fun onLoadMyWallet(requestWrapper: RequestWrapper<MyWalletBean>)

        fun onRecharge(requestWrapper: RequestWrapper<RechargeResp>)
        fun onPayPsdCheck(requestWrapper: RequestWrapper<JSONObject>)

        //支付
        fun walletInit(requestWrapper: RequestWrapper<WalletInitResponse>)
        fun walletManagerInit(requestWrapper: RequestWrapper<WalletInitResponse>)
        fun walletStatus(requestWrapper: RequestWrapper<MutableMap<String, Any>>)
    }
}