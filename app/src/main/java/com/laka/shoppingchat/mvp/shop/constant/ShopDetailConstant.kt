package com.laka.shoppingchat.mvp.shop.constant

import com.laka.shoppingchat.BuildConfig

/**
 * @Author:summer
 * @Date:2018/12/20
 * @Description:
 */
interface ShopDetailConstant {
    companion object {
        /*淘宝客总接口，通过method参数区分不同子接口*/
        const val TAOBAOKE_SHOP_URL: String = "tbkGetRequest"
        /*产品详情图片*/
        const val TAOBAO_DETAIL_IMAGE: String = "taobaoke/image-detail"
        /*产品详情*/
        const val CUSTOM_PRODUCT_URL: String = "taobaoke/product-detail-no-tao-h5"
        //获取高佣链接
        const val API_TBK_PRIVILEGE_GET = "taobaoke/tbk-privilege-get"
        //创建淘口令
        const val API_TBK_CREATE_TPWD = "taobaoke/tbk-tpwd-create"

        //淘宝客二维码url
        const val API_TBK_QRCODE_URL = "taobaoke/tkl-url"

        //淘宝相关推荐
        const val API_PRODUCT_RECOMMEND = "product/related-recommend"

        //获取领券url
        const val API_COUPON_INFO = "product/couponInfo"

        // 多样式显示 type
        const val SHOP_DETAIL_RECOMMEND_ITEM: Int = 0x1001
        const val SHOP_DETAIL_BASIC: Int = 0x1002
        const val SHOP_DETAIL_MORE: Int = 0x1003
        const val SHOP_DETAIL_RECOMMEND_TITLE: Int = 0x1004
        const val SHOP_DETAIL_BANNER: Int = 0x1005
        const val SHOP_DETAIL_IMAGE_DETAIL: Int = 0x1006
        const val SHOP_DETAIL_STORE_DETAIL: Int = 0x1007
        const val SHOP_DETAIL_ADVERT_BANNER: Int = 0x1008

        // 请求key
        const val SHOPPING_BASE_HOST: String = BuildConfig.ERGOU_BASE_HOST
        const val PAGE_NO: String = "page_no"
        const val PAGE_SIZE_KEY: String = "page_size"
        const val TEXT: String = "text"
        const val URL: String = "url"
        const val LOGO: String = "logo"
        const val PRODUCT_ID: String = "product_id"
        const val ITEM_ID: String = "item_id"
        const val TPWD_CREATE: String = "tpwd_create"

        // 詳情页入口区分
        const val ENTRANCE: String = "entrance"
        const val PRODUCT_ITEM_CLICK_SKELETON = 3  //商品列表点击进入
        //加载/更新 高佣优惠券信息
        const val TYPE_LOAD_COUPONINFO_FOR_RECEIVE: Int = 1  //领券类型
        const val TYPE_LOAD_COUPONINFO_FOR_SHARE: Int = 2  //分享
        const val TYPE_LOAD_COUPONINFO_FOR_NORMAL: Int = 3  //普通加载高佣优惠券

    }
}