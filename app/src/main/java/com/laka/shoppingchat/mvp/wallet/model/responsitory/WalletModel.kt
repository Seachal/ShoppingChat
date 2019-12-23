package com.laka.shoppingchat.mvp.wallet.model.responsitory

import com.laka.androidlib.util.rx.RxSchedulerComposer
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.customrx.RxCustomSubscriber
import com.laka.shoppingchat.common.dsl.RequestWrapper
import com.laka.shoppingchat.common.ext.excute
import com.laka.shoppingchat.mvp.wallet.constract.IWalletConstract
import com.laka.shoppingchat.mvp.wallet.model.bean.MyWalletBean
import com.laka.shoppingchat.mvp.wallet.model.bean.RechargeResp
import com.laka.shoppingchat.mvp.wallet.model.bean.WalletInitResponse
import com.netease.nim.uikit.business.session.constract.RedPackageConstract
import com.netease.nim.uikit.business.session.model.respository.NormalRetrofitHelper
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.util.ArrayList

/**
 * @Author:summer
 * @Date:2019/9/10
 * @Description:
 */
class WalletModel : IWalletConstract.IWalletModel {

    override fun onLoadPayToken(requestWrapper: RequestWrapper<JSONObject>) {
        LianLianPayRetrofitHelper.instance
            .onLoadPayToken(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun onRecharge(requestWrapper: RequestWrapper<RechargeResp>) {
        WalletRetrofitHelper.instance
            .recharge(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    private lateinit var mView: IWalletConstract.IWalletView
    private val mDisposableList = ArrayList<Disposable>()

    override fun setView(v: IWalletConstract.IWalletView) {
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

    override fun onLoadMyWallet(requestWrapper: RequestWrapper<MyWalletBean>) {
        WalletRetrofitHelper.instance
            .onLoadWalletInfo(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }

    override fun onPayPsdCheck(requestWrapper: RequestWrapper<JSONObject>) {
        WalletRetrofitHelper.instance
            .onPayPsdCheck(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
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

    override fun walletStatus(requestWrapper: RequestWrapper<MutableMap<String, Any>>) {
        WalletRetrofitHelper.instance
            .onWalletStatus(requestWrapper.getParams())
            .excute(mDisposableList, requestWrapper.callBack, mView)
    }
}