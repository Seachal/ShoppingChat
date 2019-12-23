package com.laka.shoppingchat.mvp.wallet.model.bean

/**
 * @Author:summer
 * @Date:2019/10/9
 * @Description:
 */
data class RedPackageRecord(
    val hongbao_no: String, //红包ID
    val hongbao_type: Int, //红包类型 普通红包:1 拼手气红包:2
    val sender_user_id: String, //红包用户的用户标识
    val record_time: Long, //领到红包时的时间
    val amount: String, //单位元
    val show_msg: String //展示信息， 如: 1/10个 已过期 1/10个 3/3个
)