package com.netease.nim.uikit.business.session.model.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author:summer
 * @Date:2019/9/20
 * @Description:
 */
data class RedPackageDetailList(
    @SerializedName("receivers")
    val receivers: ArrayList<RedPackageDetailListBean>
)