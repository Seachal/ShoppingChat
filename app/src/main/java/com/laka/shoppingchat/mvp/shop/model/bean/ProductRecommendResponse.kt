package com.laka.shoppingchat.mvp.shop.model.bean

import com.google.gson.annotations.SerializedName
import com.laka.shoppingchat.mvp.shopping.center.model.bean.ProductWithCoupon

/**
 * @Author:summer
 * @Date:2019/1/8
 * @Description: 猜您喜欢
 */
data class ProductRecommendResponse(
        @SerializedName("list")
        val list: ArrayList<ProductWithCoupon> = ArrayList(),
        val total_results: Int = 0,
        val timestamp: Long = 0
)
