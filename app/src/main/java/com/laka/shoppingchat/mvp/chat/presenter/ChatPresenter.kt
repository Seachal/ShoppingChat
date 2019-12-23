package com.laka.shoppingchat.mvp.chat.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.exception.BaseException
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.mvp.chat.constact.ChatConstact
import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo
import com.laka.shoppingchat.mvp.chat.model.responsitory.ChatModel

/**
 * @Author:summer
 * @Date:2019/10/9
 * @Description:
 */
class ChatPresenter : ChatConstact.IChatPresenter {

    private var mModel: ChatConstact.IChatModel = ChatModel()
    private lateinit var mView: ChatConstact.IChatView

    override fun setView(view: ChatConstact.IChatView) {
        mView = view
        mModel.setView(mView)
    }

    override fun onViewCreate() {

    }

    override fun onViewDestroy() {
        mModel.onViewDestory()
    }

    override fun onLifeCycleChange(owner: LifecycleOwner, event: Lifecycle.Event) {

    }

    override fun getEncryQrCodeInfo(groupId: String) {
        val params = HashMap<String, Any>()
        params["group"] = groupId
        mModel.getEncryQrCodeInfo(params, object : ResponseCallBack<QrCodeInfo> {
            override fun onSuccess(t: QrCodeInfo) {
                mView.getEncryQrCodeInfoSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showCenterToast("${e?.errorMsg}")
            }
        })
    }

    override fun getDecryptQrCodeInfo(qr: String) {
        val params = HashMap<String, Any>()
        params["qr"] = qr
        mModel.getDecryptQrCodeInfo(params, object : ResponseCallBack<QrCodeInfo> {
            override fun onSuccess(t: QrCodeInfo) {
                mView.getDecryptQrCodeInfoSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showCenterToast("${e?.errorMsg}")
            }
        })
    }
}