package com.laka.shoppingchat.mvp.shopping.center.view.adapter

import com.chad.library.adapter.base.BaseViewHolder
import com.laka.androidlib.base.adapter.MultipleAdapterItem
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.shop.model.bean.ProductBannerList
import com.laka.shoppingchat.mvp.shop.weight.ShopDetailBannerView

/**
 * @Author:summer
 * @Date:2019/4/28
 * @Description:产品详情banner
 */
class ProductDetailBannerItem : MultipleAdapterItem<ProductBannerList> {

    private var mBannerView: ShopDetailBannerView? = null

    override fun convert(helper: BaseViewHolder, item: ProductBannerList?) {
        mBannerView = helper!!.itemView?.findViewById(R.id.banner_view_shop_detail)
        item?.let {
            mBannerView?.setPageData(it.imageList)
        }
    }

    //释放banner轮播图
    fun release() {
        mBannerView?.onRelease()
    }

    fun onStart() {

    }

    fun onPause() {

    }

}