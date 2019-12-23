package com.laka.shoppingchat.mvp.login.model.bean

/**
 * @Author:summer
 * @Date:2019/9/7
 * @Description:
 */
data class UserImInfo(
    var id: String = "",
    var mobile: String = "",
    var accid: String = "",
    var token: String = "",
    var nickname: String = "",
    var qr: String = "",
    var pay_password: Int = 0
)