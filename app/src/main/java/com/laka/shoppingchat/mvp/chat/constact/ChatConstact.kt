package com.laka.shoppingchat.mvp.chat.constact

import com.laka.androidlib.mvp.IBaseLoadingView
import com.laka.androidlib.mvp.IBaseModel
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo

/**
 * @Author:summer
 * @Date:2019/9/29
 * @Description:
 */
interface ChatConstact {

    interface IChatView:IBaseLoadingView<String>{
        fun getEncryQrCodeInfoSuccess(info:QrCodeInfo)
        fun getDecryptQrCodeInfoSuccess(info: QrCodeInfo)
    }

    interface IChatPresenter:IBasePresenter<IChatView>{
        fun getEncryQrCodeInfo(groupId:String)
        fun getDecryptQrCodeInfo(qr:String)
    }

    interface IChatModel:IBaseModel<IChatView>{
        fun getEncryQrCodeInfo(params:HashMap<String,Any>,callBack: ResponseCallBack<QrCodeInfo>)
        fun getDecryptQrCodeInfo(params: HashMap<String, Any>, callBack: ResponseCallBack<QrCodeInfo>)
    }
}