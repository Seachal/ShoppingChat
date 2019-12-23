package com.laka.shoppingchat.mvp.wallet.constract

import com.laka.androidlib.mvp.IBaseLoadingView
import com.laka.androidlib.mvp.IBaseModel
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.shoppingchat.common.dsl.RequestWrapper
import com.laka.shoppingchat.mvp.wallet.model.bean.MyWalletBean
import com.laka.shoppingchat.mvp.wallet.model.bean.WalletInitResponse
import org.json.JSONObject

interface IPayConstract {

    interface IPayView : IBaseLoadingView<WalletInitResponse> {
        fun walletInitSuccess(response: WalletInitResponse) {}
        fun walletInitFail(msg: String) {}
        fun walletManagerInitSuccess(response: WalletInitResponse) {}
        fun walletManagerInitFail(msg: String) {}
        fun initCallBackSuccess() {}
        fun initCallBackFail() {}
        fun walletStatusSuccess(status: Double) {}
        fun walletStatusFail(msg: String) {}
        fun onRechargeSuccess(response: HashMap<String, Any>) {}
        fun onWithdrawSuccess(response: WalletInitResponse) {}
        fun onWithdrawFail(code: Int, msg: String) {}
        fun onLoadWalletSuccess(response: MyWalletBean) {}
        fun onPsdVeriflySuccess(response: JSONObject) {}
        fun onPsdVeriflyFail(response: JSONObject) {}
        fun onLoadServiceChargeSuccess(response: HashMap<String, Any>) {}
        fun onLoadServiceChargeFail(msg: String) {}
        fun onLoadServiceChargeRateSuccess(response: HashMap<String, Any>) {}
        fun onLoadServiceChargeRateFail(msg: String) {}
    }

    interface IPayPresenter : IBasePresenter<IPayView> {
        fun walletInit()
        fun walletManagerInit(flagPara: String)
        fun initCallBack()
        fun walletStatus()
        fun onRecharge(amount: Double)
        fun onWithdraw(amount: String, psd: String)
        fun onLoadWallet()
        fun onPsdVerifly(psd: String)
        fun onLoadServiceCharge(amount: String)
        fun onLoadServiceChargeRate()
    }

    interface IPayModel : IBaseModel<IPayView> {
        fun onLoadServiceCharge(requestWrapper: RequestWrapper<HashMap<String, Any>>)
        fun onLoadServiceChargeRate(requestWrapper: RequestWrapper<HashMap<String, Any>>)
        fun onPsdVerifly(requestWrapper: RequestWrapper<JSONObject>)
        fun walletInit(requestWrapper: RequestWrapper<WalletInitResponse>)
        fun walletManagerInit(requestWrapper: RequestWrapper<WalletInitResponse>)
        fun onWithdraw(requestWrapper: RequestWrapper<WalletInitResponse>)
        fun onRecharge(requestWrapper: RequestWrapper<HashMap<String, Any>>)
        fun walletStatus(requestWrapper: RequestWrapper<MutableMap<String, Any>>)
        fun initCallBack(requestWrapper: RequestWrapper<JSONObject>)
        fun onLoadWallet(requestWrapper: RequestWrapper<MyWalletBean>)
    }

}