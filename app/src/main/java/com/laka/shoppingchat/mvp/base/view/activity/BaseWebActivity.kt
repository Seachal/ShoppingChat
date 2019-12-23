package com.laka.shoppingchat.mvp.base.view.activity

import android.annotation.SuppressLint
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.View
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.base.webConfig.IWebViewInitializer
import com.tencent.smtt.sdk.WebView


abstract class BaseWebActivity : BaseActivity() {

    protected var mWebView: WebView? = null
//    private var mIsWebViewAvailable = false

    @SuppressLint("JavascriptInterface")
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        mWebView = findViewById(R.id.my_webview)
        mWebView?.let {
            val initializer = setInitializer()
            mWebView = initializer.initWebViewSettings(it)
            initializer.initWebViewBefore()
            it.webViewClient = initializer.initWebViewClient()
            it.webChromeClient = initializer.initWebChromeClient()
            it.addJavascriptInterface(initializer.initWebJSInterface(), "WebViewJavascriptBridge")
//            mIsWebViewAvailable = true
        }
    }

    override fun onPause() {
        super.onPause()
        mWebView?.let {
            it.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        mWebView?.let {
            it.onResume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView?.let {
            it.visibility = View.GONE
            it.loadUrl("about:blank")
            it.stopLoading()
            it.webChromeClient = null
            it.webViewClient = null
            it.destroy()
        }
        mWebView = null
    }

    fun loadUrl(url: String) {
        mWebView?.let {
            it.loadUrl(url)
        }
    }

    abstract fun setInitializer(): IWebViewInitializer
}