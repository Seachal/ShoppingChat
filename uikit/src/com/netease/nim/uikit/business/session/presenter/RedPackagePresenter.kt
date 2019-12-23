package com.netease.nim.uikit.business.session.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.laka.androidlib.util.EncryptUtils
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.exception.BaseException
import com.laka.androidlib.util.toast.ToastHelper
import com.netease.nim.uikit.business.session.constract.RedPackageConstract
import com.netease.nim.uikit.business.session.model.bean.*
import com.netease.nim.uikit.business.session.model.respository.RedPackageModel
import org.json.JSONObject

/**
 * @Author:summer
 * @Date:2019/9/11
 * @Description:
 */
class RedPackagePresenter : RedPackageConstract.IRedPackagePresenter {

    private lateinit var mView: RedPackageConstract.IRedPackageView
    private var mModel: RedPackageConstract.IRedPackageModel =
        RedPackageModel()

    override fun setView(view: RedPackageConstract.IRedPackageView) {
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

    override fun sendRedPackage(json: JSONObject) {
        mView.showLoading()
        mModel.sendRedPackage(json, object : ResponseCallBack<RedPackageResponse> {
            override fun onSuccess(t: RedPackageResponse) {
                mView.dismissLoading()
                LogUtils.info("请求成功")
                mView.sendRedpackageSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                mView.dismissLoading()
                ToastHelper.showCenterToast("${e?.message}")
                mView.sendRedpackageFail("${e?.message}")
            }
        })
    }

    override fun onLoadMyWallet() {
        mModel.onLoadMyWallet(HashMap(), object : ResponseCallBack<WalletBean> {
            override fun onSuccess(t: WalletBean) {
                mView.onLoadMyWalletSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showCenterToast("获取余额失败，请稍后重试")
            }
        })
    }

    override fun redPackageTicket(json: JSONObject) {
        mView.showLoadingDialog()
        mModel.redPackageTicket(json, object : ResponseCallBack<RedPackageTicketBean> {
            override fun onSuccess(t: RedPackageTicketBean) {
                mView.redPackageTicketSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showCenterToast("${e?.message}")
                mView.dissLoadingDialog()
                mView.redPackageTicketFail("${e?.message}")
            }
        })
    }

    override fun robRedPackage(json: JSONObject) {
        mModel.robRedPackage(json, object : ResponseCallBack<RedPackageTicketBean> {
            override fun onSuccess(t: RedPackageTicketBean) {
                mView.onRobRedPackageSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showCenterToast("${e?.message}")
                mView.onRobRedPackageFail("${e?.message}")
            }
        })
    }

    override fun onLoadRedPackageRecord(json: JSONObject) {
        mView.showLoading()
        mModel.onLoadRedPackageRecord(json, object : ResponseCallBack<RedPackageRecordBean> {
            override fun onSuccess(t: RedPackageRecordBean) {
                mView.dismissLoading()
                mView.onLoadRedpackageRecordSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showCenterToast("${e?.message}")
                mView.onLoadRedpackageRecordFail("${e?.message}")
            }
        })
    }

    override fun onPayPsdCheck(psd: String) {
        mView.showLoading()
        val params = HashMap<String, String>()
        params["password"] = EncryptUtils.encryptCustomMd5ToString(psd)
        mModel.onPayPsdCheck(params, object : ResponseCallBack<JSONObject> {
            override fun onSuccess(t: JSONObject) {
                mView.dismissLoading()
                mView.onPayPsdCheckSuccess(psd)
            }

            override fun onFail(e: BaseException?) {
                mView.onPayPsdCheckFail(e?.code ?: -1, "${e?.message}")
            }
        })
    }

    override fun onLoadUnixtime() {
        mView.showLoadingDialog()
        mModel.onLoadUnixtime(object : ResponseCallBack<HashMap<String, String>> {
            override fun onSuccess(t: HashMap<String, String>) {
                mView.onLoadUnixtimeSuccess("${t["time"]}")
            }

            override fun onFail(e: BaseException?) {
                mView.dissLoadingDialog()
                ToastHelper.showCenterToast("获取红包数据失败，请稍后重试")
            }
        })
    }

    override fun onLoadRedPackageDetailHeader(json: JSONObject) {
        mView.showLoadingDialog()
        mModel.onLoadRedPackageDetailHeader(json,
            object : ResponseCallBack<RedPackageDetailHeader> {
                override fun onSuccess(t: RedPackageDetailHeader) {
                    mView.dissLoadingDialog()
                    mView.onLoadRedPackageDetailHeaderSuccess(t)
                }

                override fun onFail(e: BaseException?) {
                    mView.dissLoadingDialog()
                    ToastHelper.showCenterToast("${e?.message}")
                    mView.onLoadRedPackageDetailHeaderFail("${e?.message}")
                }
            })
    }

    override fun onLoadRedPackageDetail(json: JSONObject) {
        mView.showLoading()
        mModel.onLoadRedPackageDetail(json, object : ResponseCallBack<RedPackageDetailList> {
            override fun onSuccess(t: RedPackageDetailList) {
                mView.onLoadRedPackageDetailSuccess(t)
            }

            override fun onFail(e: BaseException?) {
//                if ("普通红包只有发送者能看到领取人的详情" != "${e?.errorMsg}") {
//                    ToastHelper.showCenterToast("${e?.message}")
//                }
                mView.onLoadRedPackageDetailFail("${e?.message}")
            }
        })
    }
}