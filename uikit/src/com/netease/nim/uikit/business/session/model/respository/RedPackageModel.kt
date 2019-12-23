package com.netease.nim.uikit.business.session.model.respository

import com.laka.androidlib.util.rx.RxSchedulerComposer
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.customrx.RxCustomSubscriber
import com.netease.nim.uikit.business.session.constract.RedPackageConstract
import com.netease.nim.uikit.business.session.model.bean.*
import io.reactivex.disposables.Disposable
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.ArrayList

/**
 * @Author:summer
 * @Date:2019/9/11
 * @Description:
 */
class RedPackageModel : RedPackageConstract.IRedPackageModel {

    private lateinit var mView: RedPackageConstract.IRedPackageView
    private val mDisposableList = ArrayList<Disposable>()

    override fun setView(v: RedPackageConstract.IRedPackageView) {
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

    override fun sendRedPackage(json: JSONObject, callBack: ResponseCallBack<RedPackageResponse>) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        RedPackageRetrofitHelper.instance
            .sendRedPackage(requestBody)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<RedPackageResponse, RedPackageConstract.IRedPackageView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun onLoadMyWallet(
        params: HashMap<String, String>,
        callBack: ResponseCallBack<WalletBean>
    ) {
        NormalRetrofitHelper.instance
            .onLoadWalletInfo(params)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object : RxCustomSubscriber<WalletBean, RedPackageConstract.IRedPackageView>(
                mView,
                callBack
            ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }


    override fun redPackageTicket(
        json: JSONObject,
        callBack: ResponseCallBack<RedPackageTicketBean>
    ) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        RedPackageRetrofitHelper.instance
            .redPackageTicket(requestBody)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<RedPackageTicketBean, RedPackageConstract.IRedPackageView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun robRedPackage(json: JSONObject, callBack: ResponseCallBack<RedPackageTicketBean>) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        RedPackageRetrofitHelper.instance
            .robRedPackage(requestBody)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<RedPackageTicketBean, RedPackageConstract.IRedPackageView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun onLoadRedPackageRecord(
        json: JSONObject,
        callBack: ResponseCallBack<RedPackageRecordBean>
    ) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        RedPackageRetrofitHelper.instance
            .onLoadRedPackageRecord(requestBody)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<RedPackageRecordBean, RedPackageConstract.IRedPackageView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun onPayPsdCheck(
        params: HashMap<String, String>,
        callBack: ResponseCallBack<JSONObject>
    ) {
        NormalRetrofitHelper.instance
            .onPayPsdCheck(params)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object : RxCustomSubscriber<JSONObject, RedPackageConstract.IRedPackageView>(
                mView,
                callBack
            ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun onLoadUnixtime(callBack: ResponseCallBack<HashMap<String, String>>) {
        RedPackageRetrofitHelper.instance
            .onLoadUnixtime()
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<HashMap<String, String>, RedPackageConstract.IRedPackageView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun onLoadRedPackageDetailHeader(
        json: JSONObject,
        callBack: ResponseCallBack<RedPackageDetailHeader>
    ) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        RedPackageRetrofitHelper.instance
            .onLoadRedPackageDetailHeader(requestBody)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<RedPackageDetailHeader, RedPackageConstract.IRedPackageView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun onLoadRedPackageDetail(
        json: JSONObject,
        callBack: ResponseCallBack<RedPackageDetailList>
    ) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        RedPackageRetrofitHelper.instance
            .onLoadRedPackageDetail(requestBody)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<RedPackageDetailList, RedPackageConstract.IRedPackageView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }
}