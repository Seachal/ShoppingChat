package com.laka.shoppingchat.mvp.tmall.view.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.StringUtils
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.util.regex.RegexUtils
import com.laka.shoppingchat.mvp.base.dialog.ErGouDialog
import com.laka.shoppingchat.mvp.main.constant.HomeApiConstant
import com.laka.shoppingchat.mvp.main.helper.TmallHelper
import com.laka.shoppingchat.mvp.main.model.bean.InnerPageBean
import com.laka.shoppingchat.mvp.shop.ShopDetailModuleNavigator
import com.laka.shoppingchat.mvp.shop.utils.AliPageUtils
import com.laka.shoppingchat.mvp.tmall.constant.TmallConstant
import kotlinx.android.synthetic.main.activity_tmall_web.*

/**
 * @Author:summer
 * @Date:2019/4/24
 * @Description:天猫国际&天猫超市&聚划算
 */
class TmallWebActivity : BaseMvpActivity<String>() {

    private var mUrl: String = ""
    private var mTitle: String = ""
    private lateinit var mTallHelper: TmallHelper
    //特殊详情url匹配正则表达式
    private val mRegexStr = "/i([0-9]+)\\.htm"

    //loading view 显示的时间
    private var mLoadingViewShowTime: Long = 0
    //loading view 显示出来的最小时间，由于打开一个页面，onPageStarted()方法有时会出现走多次的情况，，onPageFinish()也会相应走多次
    //如果只是单纯在 onPageStarted() 显示 loadingView 在 onPageFinish() 隐藏 ，就会引起闪动的效果，体验是很不好的，所以设置 mTimeSpace
    //当loadingView 显示的时间少于指定值时，onPageFinish 方法中就不隐藏，等 handler 定时任务来隐藏。
    private var mTimeSpace: Long = 300
    //当前webView 的状态，1：默认状态  2：加载状态
    private var mStatus = TmallConstant.TMALL_STATUS_NORMAL
    //商品ID
    private var mProductId: String = ""

    private var mHandler = Handler()
    lateinit var dialog: ErGouDialog
    private var mRunnable = Runnable {

        if (dialog.isShowing
            && mStatus == TmallConstant.TMALL_STATUS_NORMAL) {
            if (::dialog.isInitialized) {
                dialog.dismiss()
            }

        }
    }

    override fun showData(data: String) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun createPresenter(): IBasePresenter<*>? {
        return null
    }

    override fun setContentView(): Int {
        return R.layout.activity_tmall_web
    }

    override fun initIntent() {
        mUrl = intent.getStringExtra(TmallConstant.KEY_H5_TMALL_URL)
        mTitle = intent.getStringExtra(TmallConstant.KEY_H5_TMALL_TITLE)
    }

    override fun initViews() {
        mTallHelper = TmallHelper(cl_root, this)
        dialog = ErGouDialog(this)
        title_bar.setTitle(mTitle)
            .setLeftIcon(R.drawable.seletor_nav_btn_back)
            .setTitleTextColor(R.color.black)
            .setTitleTextSize(16)
            .setOnLeftClickListener { onWebViewGoback() }
        AliPageUtils.openAliPageForH5(this, web_view, mWebViewClient, mWebChromeClient, mUrl)
    }

    private var mPreItemClickTime: Long = 0L

    private var mWebChromeClient: WebChromeClient = WebChromeClient()

    private var mWebViewClient: WebViewClient = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            LogUtils.info("test----shouldOverrideUrlLoading:$url")
            var innerPager = isProductDetailPage("$url")
            if (innerPager.isPager) { //详情页
                if (System.currentTimeMillis() - mPreItemClickTime > 700) {
                    val params = RegexUtils.findParamsForUrl("$url")
                    var id = "${params[innerPager.urlData.key]}"
                    if (StringUtils.isNotEmpty(id)) {
                        mProductId = id
                        LogUtils.info("test-------id1=$id")
                        mPreItemClickTime = System.currentTimeMillis()
                        ShopDetailModuleNavigator.startShopDetailActivity(this@TmallWebActivity, mProductId)
                        return true
                    } else {
                        //针对特殊链接获取商品ID
                        //比如：https://a.m.taobao.com/i587971675049.htm?
                        id = RegexUtils.findTergetStrForRegex("$url", mRegexStr, 1)
                        if (StringUtils.isNotEmpty(id)) {
                            mProductId = id
                            LogUtils.info("test-------id2=$id")
                            mPreItemClickTime = System.currentTimeMillis()
                            ShopDetailModuleNavigator.startShopDetailActivity(this@TmallWebActivity, mProductId)
                            return true
                        }
                    }
                }else{
                    return true
                }
            }
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            LogUtils.info("test-----pageStart-------url:${view?.url}")
            super.onPageStarted(view, url, favicon)
            if (::dialog.isInitialized && !dialog.isShowing) {
                if (!isFinishing) {
                    dialog.show()
                }
                mStatus = TmallConstant.TMALL_STATUS_LOADING
                mLoadingViewShowTime = System.currentTimeMillis()
                mHandler.removeCallbacksAndMessages(null)
                mHandler.postDelayed(mRunnable, mTimeSpace)
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            LogUtils.info("test-----pageFinish-----url:${view?.url}")
            mStatus = TmallConstant.TMALL_STATUS_NORMAL
            if (System.currentTimeMillis() - mLoadingViewShowTime > mTimeSpace
                && dialog.isShowing) {
                if (::dialog.isInitialized) {
                    dialog.dismiss()
                }

            }
//            目前需求是直接跳转本地详情页面，不再需要在H5中遮挡
//            if (::mTallHelper.isInitialized) {
//                mTallHelper.onPageFinished("${view?.url}")
//            }
            setWebTitle(view?.title)
            super.onPageFinished(view, url)
        }
    }

    private fun isProductDetailPage(url: String): InnerPageBean {
        var innerPager = InnerPageBean()
        if (StringUtils.isNotEmpty(url)) {
            for (i in 0 until HomeApiConstant.URL_TMALL_PREFIX_LIST.size) {
                val bean = HomeApiConstant.URL_TMALL_PREFIX_LIST[i]
                if (url.contains(bean.host)) {
                    innerPager.isPager = true
                    innerPager.urlData = bean
                    break
                }
            }
        }
        return innerPager
    }

    private fun setWebTitle(title: String?) {
        if (StringUtils.isNotEmpty(title)) {
            mTitle = "$title"
            title_bar.setTitle(mTitle)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onWebViewGoback()
        }
        return true
    }

    private fun onWebViewGoback() {
        //判断当前WebView是否仍然可回退，不可回退则直接退出当前activity
        if (web_view.canGoBack()) {
            web_view.goBack()
        } else {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::mTallHelper.isInitialized) {
            mTallHelper.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mTallHelper.isInitialized) {
            mTallHelper.onViewDestroy()
        }
        mHandler.removeCallbacksAndMessages(null)
    }
}