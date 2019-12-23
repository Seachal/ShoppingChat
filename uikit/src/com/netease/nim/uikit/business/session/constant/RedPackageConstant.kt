package com.netease.nim.uikit.business.session.constant

/**
 * @Author:summer
 * @Date:2019/9/16
 * @Description:
 */
object RedPackageConstant {

    const val RED_PACKAGE_AMOUNT_MIN:Double = 0.01 //单个红包最大限额
    const val RED_PACKAGE_AMOUNT_MAX:Int = 200 //单个红包最大限额
    const val RED_PACKAGE_PAY_MAX:Int = 20000 //单次支付最大限额
    const val DEFAULT_PAGE_SIZE:Int = 20
    //抢红包前红包的状态 1:可以抢 2:抢完了  3:已抢过  4:拒绝自己领普通红包 5:无效红包
    const val RED_PACKAGE_STATUS_ENABLE: Int = 1
    const val RED_PACKAGE_STATUS_ROBBED: Int = 2
    const val RED_PACKAGE_STATUS_ALREADY_ROB: Int = 3
    const val RED_PACKAGE_STATUS_SELF: Int = 4 //自己的红包不能领取
    const val RED_PACKAGE_STATUS_INVALID: Int = 5 //失效红包


    //客户端显示红包的状态  1:亮红包 2:已被领完(抢完了)  3:已领取(抢到了或抢过了)  4:自己的普通红包，已被别人领取，但未被领完  5:已过期
    const val RED_PACKAGE_CLIENT_STATUS_DEFAULT: Int = 0 //默认
    const val RED_PACKAGE_CLIENT_STATUS_NORMAL: Int = 1
    const val RED_PACKAGE_CLIENT_STATUS_ROBBED: Int = 2
    const val RED_PACKAGE_CLIENT_STATUS_ALREADY_ROB: Int = 3
    const val RED_PACKAGE_CLIENT_STATUS_SEFT: Int = 4
    const val RED_PACKAGE_CLIENT_STATUS_OVERDUE: Int = 5

    // key
    //红包客户端状态 1:亮红包 2:已被领完(抢完了)  3:已领取(抢到了或抢过了)  4:自己的普通红包，已被别人领取，但未被领完  5:已过期
    @JvmField
    val KEY_RED_PACKAGE_CLIENT_STATUS: String = "KEY_RED_PACKAGE_CLIENT_STATUS"

    //红包类型
    const val RED_PACKAGE_TYPE_NORMAL:Int = 1 //普通红包
    const val RED_PACKAGE_TYPE_RANDOM:Int = 2 //拼手气红包

    //key
    const val KEY_SESSION_TYPE = "key_session_type"
    const val KEY_USER_ACCOUNT = "key_user_account"

}