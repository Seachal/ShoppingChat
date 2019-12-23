package com.laka.shoppingchat.mvp.shopping.center.constant

import com.laka.shoppingchat.BuildConfig

/**
 * @Author:Rayman
 * @Date:2018/12/18
 * @Description:商品API请求常量类(需要用object声明为静态类)
 */

object ShoppingApiConstant {

    const val HOME_API_HOST = BuildConfig.ERGOU_BASE_HOST
    //获取首页banner、专题、活动分区、分类等数据
    const val API_GET_HOME_PAGE_DATA = "system/homepage"
    // 获取主页精品列表Banner数据
    const val API_GET_BANNER_LIST_BY_FAVORITES = "taobaoke/banner"
    // 获取主页列表精选数据
    const val API_GET_PRODUCT_LIST_BY_FAVORITES = "taobaoke/tbk-uatm-favorites-item-get"
    //通用物料搜索API（首页导购）
    const val API_GET_PRODUCT_LIST_BY_TYPE = "taobaoke/tbk-search-goods"
    //淘宝商品列表（专题：10: 9块9包邮, 20: 全网爆款, 30: 新人专享, 40: 高佣精品, 50: 购聊严选, 60: 超值优惠）
    const val API_GET_PRODUCT_LIST_BY_SPECIAL_ID = "taobaoke/goods"
    //首页带筛选的商品列表
    const val API_GET_PRODUCT_LIST_BY_SELECT = "product/list"
    //获取广告页信息
    const val API_GET_ADVERT = "system/bootpage"
    const val API_GET_H5_URL = "system/h5-url"
    /**
     * description:参数设置
     **/
    const val PARAM_PAGE = "page_no"
    const val PARAM_PAGE_SIZE = "page_size"
    const val PARAM_ORDER_FIELD = "order_field"
    const val PARAM_ORDER_SORT = "order"
    const val PARAM_DEFAULT_PAGE_SIZE = 20
    const val PARAM_SPECIAL_ID: String = "id"
}
