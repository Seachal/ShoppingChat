package com.laka.shoppingchat.mvp.chat.model.responsitory

import com.laka.androidlib.util.rx.RxSchedulerComposer
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.customrx.RxCustomSubscriber
import com.laka.shoppingchat.mvp.chat.constact.ChatConstact
import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo
import com.laka.shoppingchat.mvp.user.model.responsitory.ChatRetrofitHelper
import io.reactivex.disposables.Disposable
import java.util.ArrayList

/**
 * @Author:summer
 * @Date:2019/10/9
 * @Description:
 */
class ChatModel : ChatConstact.IChatModel {

    private lateinit var mView: ChatConstact.IChatView
    private val mDisposableList = ArrayList<Disposable>()

    override fun setView(v: ChatConstact.IChatView) {
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

    override fun getEncryQrCodeInfo(
        params: HashMap<String, Any>,
        callBack: ResponseCallBack<QrCodeInfo>
    ) {
        ChatRetrofitHelper.instance
            .getEncryQrCodeInfo(params)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<QrCodeInfo, ChatConstact.IChatView>(mView, callBack) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }

    override fun getDecryptQrCodeInfo(
        params: HashMap<String, Any>,
        callBack: ResponseCallBack<QrCodeInfo>
    ) {
        ChatRetrofitHelper.instance
            .getDecryptQrCodeInfo(params)
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .subscribe(object :
                RxCustomSubscriber<QrCodeInfo, ChatConstact.IChatView>(mView, callBack) {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    mDisposableList.add(d)
                }
            })
    }
}