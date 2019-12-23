package com.netease.nim.uikit.business.session.model.bean

import java.io.Serializable

/**
 * @Author:summer
 * @Date:2019/9/16
 * @Description:红包状态 ticket 响应体
 */
data class RedPackageRecordBean(
    val result: Int, //1-5 //int. 1:可以抢 2:抢完了  3:已抢过  4:拒绝自己领普通红包 5:无效红包
    val amount: String,  //"抢到的金额" //string
    val client_status: Int,  //1:亮红包 2:已被领完(抢完了)  3:已领取(抢到了或抢过了)  4:自己的普通红包，已被别人领取，但未被领完  5:已过期
    val last:Boolean  // 是否是最后一个红包
) :Serializable