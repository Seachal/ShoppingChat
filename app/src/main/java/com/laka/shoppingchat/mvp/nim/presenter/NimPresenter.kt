package com.laka.shoppingchat.mvp.nim.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.common.dsl.RequestWrapper
import com.laka.shoppingchat.common.dsl.params
import com.laka.shoppingchat.common.dsl.request
import com.laka.shoppingchat.mvp.circle.model.repository.NimModel
import com.laka.shoppingchat.mvp.nim.constract.INimConstract


/**
 * @Author:sming
 * @Date:2019/8/8
 * @Description:
 */
class NimPresenter : INimConstract.IBaseNimPresenter {

    override fun searchFriend(mobile: String) {
        mModel.searchFriend(
            request {
                params {
                    "mobile" to mobile
                }
                onSuccess {
                    mView.onSearchFriend(it)
                }
                onFail {
                    ToastHelper.showToast(it.errorMsg)
                }
            })
    }

    override fun getGroupIdForQrCode(qr: String) {
        mModel.getGroupIdForQrCode(
            request {
                params {
                    "qr" to qr
                }
                onSuccess {
                    mView.getGroupIdForQrCodeSuccess(it)
                }
                onFail { ToastHelper.showToast(it.errorMsg) }
            }
        )
    }

    private lateinit var mView: INimConstract.IBaseNimView
    private val mModel: INimConstract.IBaseNimModel = NimModel()

    override fun setView(view: INimConstract.IBaseNimView) {
        this.mView = view
        mModel.setView(mView)
    }

    override fun onViewCreate() {

    }

    override fun onViewDestroy() {
        mModel.onViewDestory()
    }

    override fun onLifeCycleChange(owner: LifecycleOwner, event: Lifecycle.Event) {

    }

}