package com.laka.shoppingchat.mvp.shop.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.text.TextUtils
import com.alibaba.baichuan.trade.biz.login.AlibcLogin
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.androidlib.util.rx.exception.BaseException
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.common.dsl.params
import com.laka.shoppingchat.common.util.ClipBoardManagerHelper
import com.laka.shoppingchat.mvp.shop.constant.ShopDetailConstant
import com.laka.shoppingchat.mvp.shop.contract.IShopDetailContract
import com.laka.shoppingchat.mvp.shop.model.bean.CouponInfo
import com.laka.shoppingchat.mvp.shop.model.bean.CustomProductDetail
import com.laka.shoppingchat.mvp.shop.model.bean.ImageDetail
import com.laka.shoppingchat.mvp.shop.model.bean.ProductRecommendResponse
import com.laka.shoppingchat.mvp.shop.model.repository.ShopDetailModel
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import io.reactivex.disposables.Disposable

/**
 * @Author:summer
 * @Date:2018/12/20
 * @Description:
 */
class ShopDetailPresenter : IShopDetailContract.IShopDetailPresenter {

    private var mDisposableList: ArrayList<Disposable> = ArrayList()
    private lateinit var mView: IShopDetailContract.IShopDetailView
    private var mModel: IShopDetailContract.IShopDetailModel = ShopDetailModel()

    override fun onViewCreate() {
    }

    override fun onViewDestroy() {
        mDisposableList.forEach {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        mDisposableList.clear()
        mModel.onViewDestory()
    }

    override fun onLifeCycleChange(owner: LifecycleOwner, event: Lifecycle.Event) {
    }

    override fun setView(view: IShopDetailContract.IShopDetailView) {
        mView = view
        mModel.setView(mView)
    }

    /**
     * 产品详情、banner图等信息（其他信息通过H5-url获取）
     * */
    override fun onLoadProductDetail(productId: String) {
        val params = HashMap<String, String>()
        params[ShopDetailConstant.ITEM_ID] = productId
        mModel.onLoadProductDetail(params, object : ResponseCallBack<CustomProductDetail> {
            override fun onSuccess(t: CustomProductDetail) {
                if (t.code == 0) {
                    mView.onLoadProductDetailSuccess(t)
                } else {
                    //有时候会有一些后台为处理的错误，返回的状态为 0，但是确实加载错误的数据
                    //所以在 CustomProductDetail 中也添加一个code，当code不为0 时，统一处理
                    ToastHelper.showCenterToast(t.sub_msg)
                    mView.onLoadListDataFail()
                    mView.onLoadProductDetailFail()
                }
                mView.dismissLoading()
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showCenterToast("${e?.errorMsg}")
                LogUtils.info("加载产品详情失败${e?.errorMsg}")
                mView.onLoadListDataFail()
                mView.onLoadProductDetailFail()
                mView.dismissLoading()
            }
        })
    }

    // 相关推荐
    override fun onLoadRecommendData(context: Context, itemId: String) {
        var params = HashMap<String, String>()
        params[ShopDetailConstant.ITEM_ID] = "$itemId"
        params[ShopDetailConstant.PAGE_NO] = "1"
        params[ShopDetailConstant.PAGE_SIZE_KEY] = "20"
        mModel.onLoadRecommendData(params, object : ResponseCallBack<ProductRecommendResponse> {
            override fun onSuccess(t: ProductRecommendResponse) {
                if (t?.list != null && t.list.size > 0) {
                    mView?.onLoadRecommendDataSuccess(t.list)
                } else {
                    LogUtils.info("推荐商品为空")
                }
            }

            override fun onFail(e: BaseException?) {
                LogUtils.info("获取推荐商品失败")
            }
        })
    }

    /**
     * 获取 H5 服务器地址，通过该地址去获取其他详情
     * */
    override fun onGetProductDetailH5ServiceUrl(productId: String) {
        val params = HashMap<String, String>()
        params[ShopDetailConstant.ITEM_ID] = productId
        mModel.onGetProductDetailH5ServiceUrl(params, object : ResponseCallBack<HashMap<String, String>> {
            override fun onSuccess(t: HashMap<String, String>) {
                LogUtils.info("hashMap-----------$t")
                mView.onGetProductDetailH5ServiceUrlSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showToast("${e?.errorMsg}")
                mView.onLoadListDataFail()
            }
        })
    }

    /**
     * 通过H5 url 获取商品详情（video、商家信息、商品详情图片、是否包邮）
     * */
    override fun onGetProductDetailForH5Url(url: String) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        mModel.onGetProductDetailForH5Url(url, object : ResponseCallBack<CustomProductDetail> {
            override fun onSuccess(t: CustomProductDetail) {
                mView.onGetProductDetailForH5UrlSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                mView.onGetProductDetailForH5UrlFail("${e?.errorMsg}")
                mView.onLoadListDataFail()
            }
        })
    }

    /**
     * 获取商品详情图片
     * */
    override fun onGetProductDetailImageList(url: String) {
        if (TextUtils.isEmpty(url)) {
            mView.onGetProcutDetailImageListFail("找不到产品详情图片")
            return
        }
        mModel.onGetProductDetailImageList(url, object : ResponseCallBack<ArrayList<ImageDetail>> {
            override fun onSuccess(t: ArrayList<ImageDetail>) {
                mView.onGetProcutDetailImageListSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                mView.onGetProcutDetailImageListFail("${e?.errorMsg}")
            }
        })
    }

    /**
     * 通过服务器返回的 get_desc 获取商品详情图片
     * */
    override fun onGetProductDetailImageList2(url: String) {
        if (url == null || TextUtils.isEmpty(url) || url == "null" || url == "NULL") {
            mView.onGetProcutDetailImageListFail("找不到产品详情图片")
            return
        }
        mModel.onGetProductDetailImageList2(url, object : ResponseCallBack<ArrayList<ImageDetail>> {
            override fun onSuccess(t: ArrayList<ImageDetail>) {
                mView.onGetProcutDetailImageListSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                mView.onGetProcutDetailImageListFail("${e?.errorMsg}")
            }
        })
    }

    override fun onTaoBaoAuthor() {
        if (!AlibcLogin.getInstance().isLogin) {
            mView.showLoading()
            //进入淘宝授权前，清空本地粘贴板内容，防止粘贴板含有淘口令从而引起淘宝弹窗搜索弹窗
            ClipBoardManagerHelper.getInstance.clearClipBoardContentForHasTkl()
            AlibcLogin.getInstance().showLogin(object : AlibcLoginCallback {
                override fun onSuccess(p0: Int, p1: String?, p2: String?) {
                    mView.dismissLoading()
                    LogUtils.error("获取手掏用户信息: " + AlibcLogin.getInstance().session)
                    val userInfoBean = UserUtils.getUserInfoBean()
                    userInfoBean.session = AlibcLogin.getInstance().session
                    UserUtils.updateUserInfo(userInfoBean)
                    mView?.onTaoBaoAuthorSuccess()
                }

                override fun onFailure(p0: Int, p1: String?) {
                    mView.dismissLoading()
                    ToastHelper.showToast("授权失败 ")
                }
            })
        } else {
            mView?.onTaoBaoAuthorSuccess()
        }
    }

    /**
     * 获取领券url
     * */
    override fun onGetCouponInfo(itemId: String) {
        mView.showLoading()
        mModel.onGetCouponInfo(params { "item_id" to itemId }, object : ResponseCallBack<CouponInfo> {
            override fun onSuccess(t: CouponInfo) {
                LogUtils.info(t.toString())
                mView.onCouponInfoSuccess(t)
            }

            override fun onFail(e: BaseException?) {
                ToastHelper.showToast("${e?.message}")
                LogUtils.info(e?.errorMsg)
            }
        })
    }
}