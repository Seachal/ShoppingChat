package com.laka.shoppingchat.mvp.shop.model.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.laka.shoppingchat.mvp.advertbanner.model.bean.AdvertBannerBean
import com.laka.shoppingchat.mvp.shop.constant.ShopDetailConstant

/**
 * @Author:summer
 * @Date:2019/7/30
 * @Description:
 */
class AdvertBannerListBean(var data: ArrayList<AdvertBannerBean> = ArrayList()):MultiItemEntity{
    override fun getItemType(): Int {
        return ShopDetailConstant.SHOP_DETAIL_ADVERT_BANNER
    }
}