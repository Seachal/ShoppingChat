package com.netease.nim.uikit.business.session.model.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author:summer
 * @Date:2019/9/20
 * @Description:
 */
data class RedPackageDetailListBean(
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("record_time")
    val recordTime:Long = 0,
    @SerializedName("amount")
    val amount:String = "0.00",
    @SerializedName("luckiest")
    val luckiest:Int = -1  //int. 0:普通红包 1:拼手气红包 2:最佳手气
)