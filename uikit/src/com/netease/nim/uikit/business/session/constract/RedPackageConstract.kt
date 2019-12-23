package com.netease.nim.uikit.business.session.constract

import com.alibaba.fastjson.JSON
import com.laka.androidlib.mvp.IBaseLoadingView
import com.laka.androidlib.mvp.IBaseModel
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.netease.nim.uikit.business.session.model.bean.*
import org.json.JSONObject

/**
 * @Author:summer
 * @Date:2019/9/11
 * @Description:
 */
interface RedPackageConstract {

    interface IRedPackageView : IBaseLoadingView<RedPackageResponse> {
        fun sendRedpackageSuccess(response: RedPackageResponse) {}
        fun sendRedpackageFail(msg: String) {}
        fun redPackageTicketSuccess(bean: RedPackageTicketBean) {}
        fun loadRedPackageTicketSuccess(bean: RedPackageTicketBean) {}
        fun redPackageTicketFail(msg: String) {}
        fun onLoadMyWalletSuccess(myWallet: WalletBean) {}
        fun onRobRedPackageSuccess(response: RedPackageTicketBean) {}
        fun onRobRedPackageFail(msg: String) {}
        fun onLoadRedpackageRecordSuccess(resule: RedPackageRecordBean) {}
        fun onLoadRedpackageRecordFail(msg: String) {}
        fun onPayPsdCheckSuccess(psd: String) {}
        fun onPayPsdCheckFail(code: Int, msg: String) {}
        fun onLoadUnixtimeSuccess(time: String) {}
        fun onLoadUnixtimeFail(time: String) {}
        fun onLoadRedPackageDetailHeaderSuccess(header: RedPackageDetailHeader) {}
        fun onLoadRedPackageDetailHeaderFail(msg: String) {}
        fun onLoadRedPackageDetailSuccess(response: RedPackageDetailList) {}
        fun onLoadRedPackageDetailFail(msg: String) {}
        fun showLoadingDialog() {}
        fun dissLoadingDialog() {}
    }

    interface IRedPackagePresenter : IBasePresenter<IRedPackageView> {
        fun sendRedPackage(json: JSONObject)
        fun redPackageTicket(json: JSONObject)
        fun robRedPackage(json: JSONObject)
        fun onLoadRedPackageRecord(json: JSONObject)
        fun onLoadMyWallet()
        fun onPayPsdCheck(psd: String)
        fun onLoadUnixtime()
        fun onLoadRedPackageDetailHeader(json: JSONObject)
        fun onLoadRedPackageDetail(json: JSONObject)
    }

    interface IRedPackageModel : IBaseModel<IRedPackageView> {
        fun onLoadRedPackageDetail(
            json: JSONObject,
            callBack: ResponseCallBack<RedPackageDetailList>
        )

        fun onLoadRedPackageDetailHeader(
            json: JSONObject,
            callBack: ResponseCallBack<RedPackageDetailHeader>
        )

        fun sendRedPackage(json: JSONObject, callBack: ResponseCallBack<RedPackageResponse>)
        fun redPackageTicket(json: JSONObject, callBack: ResponseCallBack<RedPackageTicketBean>)
        fun robRedPackage(json: JSONObject, callBack: ResponseCallBack<RedPackageTicketBean>)
        fun onLoadRedPackageRecord(
            json: JSONObject,
            callBack: ResponseCallBack<RedPackageRecordBean>
        )

        fun onLoadMyWallet(
            params: HashMap<String, String>,
            callBack: ResponseCallBack<WalletBean>
        )

        fun onPayPsdCheck(params: HashMap<String, String>, callBack: ResponseCallBack<JSONObject>)

        fun onLoadUnixtime(callBack: ResponseCallBack<HashMap<String, String>>)
    }

}