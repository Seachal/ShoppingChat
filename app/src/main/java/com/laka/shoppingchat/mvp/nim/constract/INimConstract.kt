package com.laka.shoppingchat.mvp.nim.constract

import com.laka.androidlib.mvp.IBaseLoadingView
import com.laka.androidlib.mvp.IBaseModel
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.shoppingchat.common.dsl.RequestWrapper
import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo
import com.laka.shoppingchat.mvp.nim.model.bean.FriendDataResp
import com.netease.nimlib.sdk.RequestCallbackWrapper

/**
 * @Author:sming
 * @Date:2019/8/9
 * @Description:
 */
interface INimConstract {

    interface IBaseNimView : IBaseLoadingView<String> {
        fun onSearchFriend(resp: FriendDataResp){}
        fun getGroupIdForQrCodeSuccess(resp:QrCodeInfo){}
    }

    interface IBaseNimPresenter : IBasePresenter<IBaseNimView> {
        fun searchFriend(mobile: String)
        fun getGroupIdForQrCode(qr:String)
    }

    interface IBaseNimModel : IBaseModel<IBaseNimView> {
        fun searchFriend(callBack: RequestWrapper<FriendDataResp>)
        fun getGroupIdForQrCode(callBack: RequestWrapper<QrCodeInfo>)
    }

}