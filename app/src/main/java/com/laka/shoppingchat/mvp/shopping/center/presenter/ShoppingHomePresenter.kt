package com.laka.shoppingchat.mvp.shopping.center.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.graphics.Bitmap
import com.laka.androidlib.util.ApplicationUtils
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.SPHelper
import com.laka.androidlib.util.image.BitmapUtils
import com.laka.androidlib.util.imageload.GlideLoader
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.exception.BaseException
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.mvp.shopping.center.constant.ShoppingCenterConstant
import com.laka.shoppingchat.mvp.shopping.center.contract.IShoppingHomeContract
import com.laka.shoppingchat.mvp.shopping.center.helper.SaveImageHelper
import com.laka.shoppingchat.mvp.shopping.center.model.bean.AdvertBean
import com.laka.shoppingchat.mvp.shopping.center.model.bean.HomePageResponse
import com.laka.shoppingchat.mvp.shopping.center.model.bean.HomeUrlBean
import com.laka.shoppingchat.mvp.shopping.center.model.repository.ShoppingHomeModel
import java.io.File


/**
 * @Author:Rayman
 * @Date:2018/12/24
 * @Description:商品主页Fragment P层
 */
class ShoppingHomePresenter : IShoppingHomeContract.IShoppingHomePresenter {


    private lateinit var mView: IShoppingHomeContract.IShoppingHomeView
    private var mModel = ShoppingHomeModel()

    override fun onViewCreate() {
    }

    override fun onViewDestroy() {
        mModel.onViewDestory()
    }

    override fun onLifeCycleChange(owner: LifecycleOwner, event: Lifecycle.Event) {
    }

    override fun setView(view: IShoppingHomeContract.IShoppingHomeView) {
        this.mView = view
        mModel.setView(view)
    }

    /**
     * V2.0.0
     * 刷新首页缓存
     * */
    override fun refreshHomePageData() {
        updateLocalHomePageData(false)
    }

    /**
     * V2.4.0
     * 首次请求首页数据
     * */
    override fun getHomePageDataFirst() {
        val response = SPHelper.getObject(
            ShoppingCenterConstant.SP_KEY_SHOPPING_HOME,
            HomePageResponse::class.java
        )
        if (response != null) {
            mView.onGetHomePageDataSuccess(response)
        }
        updateLocalHomePageData(true)
    }

    /**
     * V2.0.0
     * 更新本地数据
     * */
    private fun updateLocalHomePageData(isFirst: Boolean) {
        val params = HashMap<String, String>()
        mModel.getHomePageData(params, object : ResponseCallBack<HomePageResponse> {
            override fun onSuccess(response: HomePageResponse) {
                //存储本地数据
                SPHelper.saveObject(ShoppingCenterConstant.SP_KEY_SHOPPING_HOME, response)
                mView.onGetHomePageDataSuccess(response)
                mView.onRefreshFragmentData()
                //首次加载数据，刷新活动弹窗
                if (isFirst) {
                    mView.onLoadPopupDataSuccess(response.popup)
                }
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showCenterToast("加载失败：${e?.errorMsg}")
                mView.onGetHomePageDataFail()
            }
        })
    }

    override fun getAdvert(context: Context) {
        val params = HashMap<String, String>()
        params["ratio"] = "${ScreenUtils.getScreenWidth()}_${ScreenUtils.getScreenHeight()}"
        mModel.onGetAdvert(params, object : ResponseCallBack<List<AdvertBean>> {
            override fun onSuccess(t: List<AdvertBean>) {
                if (t.size > 0 && t[0] != null) {
                    SPHelper.saveObject(ShoppingCenterConstant.SP_KEY_ADVERT, t[0])
                    var helper = SaveImageHelper(context)
                    var advertBean = t[0]
                    var advertPath =
                        SPHelper.getString(ShoppingCenterConstant.SP_KEY_ADVERT_PATH, "")
                    //判断本地是否存在广告页的缓存
                    if (advertPath != advertBean.img_path) {
                        cacheImg(advertBean.img_path, helper)
                    } else {
                        var file =
                            File("${context.cacheDir.absolutePath}/${ShoppingCenterConstant.SP_KEY_ADVERT_IMG_NAME}")
                        if (!file.exists()) {
                            cacheImg(advertBean.img_path, helper)
                        }
                    }
                }

            }

            override fun onFail(e: BaseException?) {

            }

        })
    }

    override fun getH5Url() {

        mModel.onGetH5Url(object : ResponseCallBack<HomeUrlBean> {

            override fun onSuccess(t: HomeUrlBean) {
                mView.onGetH5UrlSuccess(t)
            }

            override fun onFail(e: BaseException?) {

            }
        })

    }

    fun cacheImg(path: String, helper: SaveImageHelper) {
        GlideLoader.downloadImage(
            ApplicationUtils.getContext(),
            path,
            object : GlideLoader.DownloadListener {
                override fun onSuccess(resourse: Bitmap) {
                    try {
                        helper.saveBitmap(
                            ShoppingCenterConstant.SP_KEY_ADVERT_IMG_NAME,
                            BitmapUtils.compressBitmapToByte(resourse, 50, false),
                            path
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailed() {
                    LogUtils.info("下载失败")
                }
            })
    }


}