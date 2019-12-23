package com.netease.nim.uikit.business.session.model.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author:summer
 * @Date:2019/9/11
 * @Description:红包
 */
data class RedPackageResponse(
    @SerializedName("ex") //扩展
    val extend: String,
    @SerializedName("expired_at") //时间戳
    val expired_at: Int,
    @SerializedName("hongbao_no") //红包编号
    val hongbaoNo: String,
    @SerializedName("ope")  //0:个人  1:群
    val ope: Int,
    @SerializedName("title") //标题
    val title: String,
    @SerializedName("user_id") // 发红包的id
    val user_id: String,
    @SerializedName("to_id") //收红包的ID
    val toId: String,
    @SerializedName("op_user") //抢红包的人
    val opUser: String,
    @SerializedName("type") //红包类型
    val type: Int

)