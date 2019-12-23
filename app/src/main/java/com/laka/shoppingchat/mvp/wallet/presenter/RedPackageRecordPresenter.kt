package com.laka.shoppingchat.mvp.wallet.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.exception.BaseException
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.mvp.wallet.constract.IRedPackageRecordConstract
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecordHeader
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecordList
import com.laka.shoppingchat.mvp.wallet.model.responsitory.RedPackageRecordModel
import org.json.JSONObject

/**
 * @Author:summer
 * @Date:2019/10/9
 * @Description:
 */
class RedPackageRecordPresenter : IRedPackageRecordConstract.IRedPackageRecordPresenter {

    private lateinit var mView: IRedPackageRecordConstract.IRedPackageRecordView
    private val mModel: IRedPackageRecordConstract.IRedPackageRecordModel = RedPackageRecordModel()

    override fun getRedPackageRecordList(json: JSONObject) {
        mModel.getRedPackageRecordList(json, object : ResponseCallBack<RedPackageRecordList> {
            override fun onSuccess(t: RedPackageRecordList) {
                mView.getRedPackageRecordListSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showToast("${e?.errorMsg}")
                mView.getRedPackageRecordListFail("${e?.errorMsg}")
            }
        })
    }

    override fun getRedPackageRecordHeader(json: JSONObject) {
        mModel.getRedPackageRecordHeader(json, object : ResponseCallBack<RedPackageRecordHeader> {
            override fun onSuccess(t: RedPackageRecordHeader) {
                mView.getRedPackageRecordHeaderSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showToast("${e?.errorMsg}")
                mView.getRedPackageRecordHeaderFail("${e?.errorMsg}")
            }
        })
    }

    override fun getSendRedPackageRecordHeader(json: JSONObject) {
        mModel.getSendRedPackageRecordHeader(json,
            object : ResponseCallBack<RedPackageRecordHeader> {
                override fun onSuccess(t: RedPackageRecordHeader) {
                    mView.getRedPackageRecordHeaderSuccess(t)
                }

                override fun onFail(e: BaseException?) {
                    ToastHelper.showToast("${e?.errorMsg}")
                    mView.getRedPackageRecordHeaderFail("${e?.errorMsg}")
                }
            })
    }

    override fun getSendRedPackageRecordList(json: JSONObject) {
        mModel.getSendRedPackageRecordList(json, object : ResponseCallBack<RedPackageRecordList> {
            override fun onSuccess(t: RedPackageRecordList) {
                mView.getRedPackageRecordListSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showToast("${e?.errorMsg}")
                mView.getRedPackageRecordListFail("${e?.errorMsg}")
            }
        })
    }

    override fun setView(view: IRedPackageRecordConstract.IRedPackageRecordView) {
        mView = view
        mModel.setView(view)
    }

    override fun onViewCreate() {

    }

    override fun onViewDestroy() {
        mModel.onViewDestory()
    }

    override fun onLifeCycleChange(owner: LifecycleOwner, event: Lifecycle.Event) {

    }
}