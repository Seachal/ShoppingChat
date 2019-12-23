package com.laka.shoppingchat.mvp.shopping.center.model.bean

data class AdvertBean(
    val img_path: String,
    val scene_extra: HashMap<String,String>,
    val scene_id: Int,
    val scene_value: String,
    val title: String
)