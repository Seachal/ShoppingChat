package com.laka.shoppingchat.mvp.wallet.constract

import com.laka.androidlib.mvp.IBaseModel
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.mvp.IBaseView
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecordHeader
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecordList
import org.json.JSONObject

/**
 * @Author:summer
 * @Date:2019/10/9
 * @Description:
 */
interface IRedPackageRecordConstract {

    interface IRedPackageRecordView : IBaseView<RedPackageRecordList> {
        fun getRedPackageRecordListSuccess(resp: RedPackageRecordList){}
        fun getRedPackageRecordHeaderSuccess(resp: RedPackageRecordHeader){}
        fun getRedPackageRecordListFail(msg:String){}
        fun getRedPackageRecordHeaderFail(msg:String){}
    }

    interface IRedPackageRecordPresenter : IBasePresenter<IRedPackageRecordView> {
        fun getRedPackageRecordHeader(json: JSONObject)
        fun getRedPackageRecordList(json: JSONObject)
        fun getSendRedPackageRecordHeader(json: JSONObject)
        fun getSendRedPackageRecordList(json: JSONObject)
    }

    interface IRedPackageRecordModel : IBaseModel<IRedPackageRecordView> {
        fun getRedPackageRecordList(
            json: JSONObject,
            callBack: ResponseCallBack<RedPackageRecordList>
        )

        fun getRedPackageRecordHeader(
            json: JSONObject,
            callBack: ResponseCallBack<RedPackageRecordHeader>
        )

        fun getSendRedPackageRecordList(
            json: JSONObject,
            callBack: ResponseCallBack<RedPackageRecordList>
        )

        fun getSendRedPackageRecordHeader(
            json: JSONObject,
            callBack: ResponseCallBack<RedPackageRecordHeader>
        )


    }

}