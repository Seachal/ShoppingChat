package com.laka.shoppingchat.mvp.shopping.center.model.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author:summer
 * @Date:2019/5/13
 * @Description:活动专区
 */
data class PromotionBean(
        @SerializedName("title") val title: String = "",
        @SerializedName("img_path") val imgUrl: String = "",
        @SerializedName("scene_id") val sceneId: String = "",
        @SerializedName("scene_value") val sceneValue: String = "",
        @SerializedName("scene_extra") val sceneExtra: HashMap<String, String>
)