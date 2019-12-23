package com.laka.shoppingchat.mvp.wallet.model.bean

/**
 * @Author:summer
 * @Date:2019/10/9
 * @Description:
 */
data class RedPackageRecordHeader(
    val total_amount: String,
    val receive_count: Int,
    val luckiest_count: Int,
    val send_count:Int
)