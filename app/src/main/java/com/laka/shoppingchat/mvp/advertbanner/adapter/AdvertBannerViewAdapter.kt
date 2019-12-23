package com.laka.shoppingchat.mvp.advertbanner.adapter

import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.mvp.advertbanner.model.bean.AdvertBannerBean
import com.laka.shoppingchat.mvp.main.contract.IAdvertBannerConstract

/**
 * @Author:summer
 * @Date:2019/7/29
 * @Description:
 */
abstract class AdvertBannerViewAdapter : IAdvertBannerConstract.IAdvertBannerView {

    override fun showLoading() {

    }

    override fun dismissLoading() {

    }

    override fun showData(data: ArrayList<AdvertBannerBean>) {

    }

    override fun showErrorMsg(msg: String?) {
        ToastHelper.showToast("$msg")
    }

}