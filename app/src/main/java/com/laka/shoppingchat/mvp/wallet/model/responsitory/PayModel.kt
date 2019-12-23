package com.laka.shoppingchat.mvp.wallet.model.responsitory

import com.laka.shoppingchat.common.dsl.RequestWrapper
import com.laka.shoppingchat.common.ext.excute
import com.laka.shoppingchat.mvp.wallet.constract.IPayConstract
import com.laka.shoppingchat.mvp.wallet.model.bean.MyWalletBean
import com.laka.shoppingchat.mvp.wallet.model.bean.WalletInitResponse
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.util.ArrayList

class PayModel : IPayConstract.IPayModel {

    private lateinit var mView: IPayConstract.IPayView
    private val mDisposableList = ArrayList<Disposable>()

    override fun setView(v: IPayConstract.IPayView) {
        this.mView = v
    }

    override fun onViewDestory() {
        mDisposableList.forEach {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        mDisposableList.clear()
    }

    override fun walletInit(requestWrapper: RequestWrapper<WalletInitResponse>) {
        WalletRetrofitHelper.instance
            .onWalletInit(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun walletManagerInit(requestWrapper: RequestWrapper<WalletInitResponse>) {
        WalletRetrofitHelper.instance
            .onWalletManagerInit(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun onWithdraw(requestWrapper: RequestWrapper<WalletInitResponse>) {
        WalletRetrofitHelper.instance
            .onWithdraw(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun onRecharge(requestWrapper: RequestWrapper<HashMap<String, Any>>) {
        WalletRetrofitHelper.instance
            .onRecharge(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun initCallBack(requestWrapper: RequestWrapper<JSONObject>) {
        WalletRetrofitHelper.instance
            .onInitCallBack(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun walletStatus(requestWrapper: RequestWrapper<MutableMap<String, Any>>) {
        WalletRetrofitHelper.instance
            .onWalletStatus(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun onLoadWallet(requestWrapper: RequestWrapper<MyWalletBean>) {
        WalletRetrofitHelper.instance
            .onLoadWalletInfo(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)

    }

    override fun onPsdVerifly(requestWrapper: RequestWrapper<JSONObject>) {
        WalletRetrofitHelper.instance
            .onPayPsdCheck(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun onLoadServiceCharge(requestWrapper: RequestWrapper<HashMap<String,Any>>) {
        WalletRetrofitHelper.instance
            .onLoadServiceCharge(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun onLoadServiceChargeRate(requestWrapper: RequestWrapper<HashMap<String,Any>>) {
        WalletRetrofitHelper.instance
            .onLoadServiceChargeRate(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }
}