package com.laka.shoppingchat.mvp.circle.model.repository

import com.laka.androidlib.util.rx.RxSchedulerComposer
import com.laka.shoppingchat.common.dsl.RequestWrapper
import com.laka.shoppingchat.common.ext.excute
import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo
import com.laka.shoppingchat.mvp.nim.constract.INimConstract
import com.laka.shoppingchat.mvp.nim.model.bean.FriendDataResp
import com.laka.shoppingchat.mvp.nim.model.repository.NimRetrofixHelper
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * @Author:summer
 * @Date:2019/8/7
 * @Description:
 */
class NimModel : INimConstract.IBaseNimModel {

    override fun searchFriend(rtWrapper: RequestWrapper<FriendDataResp>) {
        NimRetrofixHelper.instance.getSearchUser(rtWrapper.getParams())
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .excute(mDisposableList, rtWrapper.callBack, mView)
    }

    override fun getGroupIdForQrCode(rtWrapper: RequestWrapper<QrCodeInfo>) {
        NimRetrofixHelper.instance.getGroupIdForQrCode(rtWrapper.getParams())
            .compose(RxSchedulerComposer.normalSchedulersTransformer())
            .excute(mDisposableList, rtWrapper.callBack, mView)
    }

    private lateinit var mView: INimConstract.IBaseNimView
    private val mDisposableList = ArrayList<Disposable>()

    override fun setView(v: INimConstract.IBaseNimView) {
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


}