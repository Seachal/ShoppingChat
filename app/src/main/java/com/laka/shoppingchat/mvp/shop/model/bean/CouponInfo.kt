package com.laka.shoppingchat.mvp.shop.model.bean

data class CouponInfo(
    val item_info: ItemInfo
)

data class ItemInfo(
    val cat_leaf_name: String,
    val cat_name: String,
    val category_id: String,
    val coupon_click_url: String,
    val coupon_end_time: String,
    val coupon_info: String,
    val coupon_remain_count: Int,
    val coupon_start_time: String,
    val coupon_total_count: Int,
    val coupon_type: Int,
    val item_id: String,
    val item_url: String,
    val material_lib_type: String,
    val max_commission_rate: String,
    val nick: String,
    val num_iid: String,
    val pict_url: String,
    val presale_deposit: String,
    val presale_end_time: Long,
    val presale_start_time: Long,
    val presale_tail_end_time: Long,
    val presale_tail_start_time: Long,
    val provcity: String,
    val reserve_price: String,
    val seller_id: String,
    val small_images: SmallImages,
    val title: String,
    val tkl: String,
    val user_type: Int,
    val volume: Int,
    val zk_final_price: String
)
