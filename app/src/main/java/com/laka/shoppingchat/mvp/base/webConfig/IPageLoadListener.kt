package com.laka.shoppingchat.mvp.base.webConfig

import com.tencent.smtt.sdk.WebView

interface IPageLoadListener {

    fun onLoadStart(view: WebView?, url: String?)

    fun onLoadEnd(view: WebView?, url: String?)
}