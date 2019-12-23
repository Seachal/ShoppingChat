package com.laka.shoppingchat.mvp.shop.contract

import android.content.Context
import com.laka.androidlib.mvp.IBaseLoadingView
import com.laka.androidlib.mvp.IBaseModel
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.rx.callback.ResponseCallBack
import com.laka.shoppingchat.mvp.shop.model.bean.CouponInfo
import com.laka.shoppingchat.mvp.shop.model.bean.CustomProductDetail
import com.laka.shoppingchat.mvp.shop.model.bean.ImageDetail
import com.laka.shoppingchat.mvp.shop.model.bean.ProductRecommendResponse
import com.laka.shoppingchat.mvp.shopping.center.model.bean.ProductWithCoupon

/**
 * @Author:summer
 * @Date:2018/12/20
 * @Description:
 */
interface IShopDetailContract {

    interface IShopDetailView : IBaseLoadingView<CustomProductDetail> {
        fun onLoadRecommendDataSuccess(list: ArrayList<ProductWithCoupon>)
        fun onLoadProductDetailSuccess(result: CustomProductDetail)
        fun onLoadProductDetailFail()
        fun onLoadListDataFail()
        fun onTaoBaoAuthorSuccess()
        fun onCouponInfoSuccess(result:CouponInfo)
        fun onGetProductDetailH5ServiceUrlSuccess(resultMap: HashMap<String, String>)
        fun onGetProductDetailH5ServiceUrlFail(msg: String)
        fun onGetProductDetailForH5UrlSuccess(result: CustomProductDetail)
        fun onGetProductDetailForH5UrlFail(msg: String)
        fun onGetProcutDetailImageListSuccess(imageDetailList: ArrayList<ImageDetail>)
        fun onGetProcutDetailImageListFail(msg: String)
    }

    interface IShopDetailPresenter : IBasePresenter<IShopDetailView> {
        fun onLoadRecommendData(context: Context, itemId: String)
        fun onLoadProductDetail(productId: String)
        fun onTaoBaoAuthor()
        fun onGetProductDetailH5ServiceUrl(productId: String)
        fun onGetProductDetailForH5Url(url: String)
        fun onGetProductDetailImageList(url: String)
        fun onGetProductDetailImageList2(url: String)
        fun onGetCouponInfo(itemId: String)
    }

    interface IShopDetailModel : IBaseModel<IShopDetailView> {
        fun onGetCouponInfo(params: MutableMap<String, Any>, callBack: ResponseCallBack<CouponInfo>)
        fun onLoadProductDetail(params: HashMap<String, String>, callBack: ResponseCallBack<CustomProductDetail>)
        fun onLoadRecommendData(params: HashMap<String, String>, callBack: ResponseCallBack<ProductRecommendResponse>)
        fun onGetProductDetailH5ServiceUrl(
            params: HashMap<String, String>,
            callBack: ResponseCallBack<HashMap<String, String>>
        )
        fun onGetProductDetailForH5Url(url: String, callBack: ResponseCallBack<CustomProductDetail>)
        fun onGetProductDetailImageList(url: String, callBack: ResponseCallBack<ArrayList<ImageDetail>>)
        fun onGetProductDetailImageList2(url: String, callBack: ResponseCallBack<ArrayList<ImageDetail>>)
    }

}