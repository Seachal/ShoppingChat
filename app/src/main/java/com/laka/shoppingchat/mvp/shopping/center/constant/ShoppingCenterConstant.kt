package com.laka.shoppingchat.mvp.shopping.center.constant

import android.support.annotation.IntDef

/**
 * @Author:Rayman
 * @Date:2018/12/21
 * @Description:商品模块常量类
 */
object ShoppingCenterConstant {

    /**
     * description:主页商品列表父分类常量传递
     **/
    const val LIST_TYPE_ID = "LIST_TYPE_ID"
    const val LIST_TYPE_NAME = "LIST_TYPE_NAME"

    /**
     * description:当前模块EventBus传递
     **/
    const val EVENT_LIST_UI_TYPE_NORMAL = "EVENT_LIST_UI_TYPE_NORMAL"
    const val EVENT_LIST_UI_TYPE_GRID = "EVENT_LIST_UI_TYPE_GRID"

    /**
     * description:本地SP数据存储
     **/
    const val SP_KEY_SHOPPING_HOME = "SP_KEY_SHOPPING_HOME"
    const val SP_KEY_ADVERT = "SP_KEY_ADVERT"
    const val SP_KEY_ADVERT_PATH = "SP_KEY_ADVERT_PATH"
    const val SP_KEY_ADVERT_IMG_NAME = "laka_advert.jpg"

    /**
     * description:主页商品列表样式
     **/
    const val LIST_UI_TYPE_COMMON = 1
    const val LIST_UI_TYPE_GRID = 2

    @IntDef(LIST_UI_TYPE_COMMON, LIST_UI_TYPE_GRID)
    annotation class ProductListUiType

}