package com.laka.shoppingchat.mvp.wallet.model.responsitory

import com.laka.androidlib.util.rx.RxSchedulerComposer
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.customrx.RxCustomSubscriber
import com.laka.shoppingchat.mvp.wallet.constract.IRedPackageRecordConstract
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecordHeader
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecordList
import io.reactivex.disposables.Disposable
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.*

/**
 * @Author:summer
 * @Date:2019/10/9
 * @Description:
 */
class RedPackageRecordModel : IRedPackageRecordConstract.IRedPackageRecordModel {

    private lateinit var mView: IRedPackageRecordConstract.IRedPackageRecordView
    private val mDisposableList = ArrayList<Disposable>()

    override fun getRedPackageRecordList(
        json: JSONObject,
        callBack: ResponseCallBack<RedPackageRecordList>
    ) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        RedPackageRecordRetrofitHelper.instance
            .receiveRecordList(requestBody)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<RedPackageRecordList, IRedPackageRecordConstract.IRedPackageRecordView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun getRedPackageRecordHeader(
        json: JSONObject,
        callBack: ResponseCallBack<RedPackageRecordHeader>
    ) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        RedPackageRecordRetrofitHelper.instance
            .receiveRecordHeader(requestBody)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<RedPackageRecordHeader, IRedPackageRecordConstract.IRedPackageRecordView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }


    override fun getSendRedPackageRecordList(
        json: JSONObject,
        callBack: ResponseCallBack<RedPackageRecordList>
    ) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        RedPackageRecordRetrofitHelper.instance
            .sendRecordList(requestBody)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<RedPackageRecordList, IRedPackageRecordConstract.IRedPackageRecordView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun getSendRedPackageRecordHeader(
        json: JSONObject,
        callBack: ResponseCallBack<RedPackageRecordHeader>
    ) {
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        RedPackageRecordRetrofitHelper.instance
            .sendRecordHeader(requestBody)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<RedPackageRecordHeader, IRedPackageRecordConstract.IRedPackageRecordView>(
                    mView,
                    callBack
                ) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun setView(v: IRedPackageRecordConstract.IRedPackageRecordView) {
        mView = v
    }

    override fun onViewDestory() {
        mDisposableList.forEach {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        mDisposableList.clear()
    }
}