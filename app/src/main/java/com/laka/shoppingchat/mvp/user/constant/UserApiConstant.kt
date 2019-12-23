package com.laka.shoppingchat.mvp.user.constant

import com.laka.shoppingchat.BuildConfig

/**
 * @Author:Rayman
 * @Date:2019/1/18
 * @Description:用户模块接口常量类
 */
object UserApiConstant {

    /**
     * description:与前端交互的常量配置
     **/
    const val JS_ACTION = "action"
    const val JS_ACTION_SHARE = "share"
    const val JS_ACTION_POSTER = "create_poster"
    const val JS_ACTION_DETAIL = "goods_detail"
    const val JS_ACTION_MINE_COMMISSION = "mine_commission"
    const val JS_PARAMETERS = "parameters"
    const val JS_ITEM_ID = "item_id"

    /**
     * description:接口配置
     **/
    const val SHOPPING_BASE_HOST = BuildConfig.ERGOU_BASE_HOST

    // 获取联盟授权url
    const val API_GET_UNION_CODE_URL = "user/get-union-code-url"

    // 处理联盟授权code
    const val API_HANDLE_UNION_CODE_URL = "user/handle-union-code"

    //获取商品详情服务器地址
    const val API_GET_PRODUCT_DETAIL_SERVICE_URL = "taobaoke/product-detail-service"

    /**
     * description:响应体数据常量
     **/
    const val IMAGE_PATH = "img_path"
    const val SCENE_ID = "scene_id"
    const val SCENE_VALUE = "scene_value"
    const val SCENE_EXTRA = "scene_extra"
}