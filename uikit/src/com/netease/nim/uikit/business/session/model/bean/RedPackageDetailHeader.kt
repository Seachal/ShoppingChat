package com.netease.nim.uikit.business.session.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @Author:summer
 * @Date:2019/9/19
 * @Description:红包详情头部
 */
data class RedPackageDetailHeader(
    @SerializedName("hongbao")
    val redPackage: RedPackage,
    @SerializedName("recv_record")
    val recvRecord: RecvRecord,
    @SerializedName("show_msg")
    val showMsg: String = ""
) : Serializable

data class RedPackage(
    @SerializedName("ex")
    val ex: String = "",
    @SerializedName("hongbao_type")
    val hongBaoType: Int = 1,  //int. 红包类型 1:普通红包，2:拼手气红包
    @SerializedName("owner_user_id")
    val ownerUserId: String = "", //发红包的用户
    @SerializedName("title")
    val title: String = ""
) : Serializable

//领取记录
data class RecvRecord(
    @SerializedName("amount")
    val amount: String = "",
    @SerializedName("pay_status")
    val payStatus: Int = 0 //int. 支付状态. 1:未支付 2:支付成功 3:支付失败
) : Serializable