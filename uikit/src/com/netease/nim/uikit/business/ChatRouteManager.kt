package com.netease.nim.uikit.business

import android.content.Context

/**
 * @Author:summer
 * @Date:2019/9/18
 * @Description:
 */
object ChatRouteManager {

    const val ROUTE_WALLET_ACTIVITY: String = "ROUTE_WALLET_ACTIVITY"
    const val ROUTE_SEND_RED_PACKAGE_ACTIVITY: String = "ROUTE_SEND_RED_PACKAGE_ACTIVITY"
    const val ROUTE_REDPACKAGE_RECORD_ACTIVITY: String = "ROUTE_REDPACKAGE_RECORD_ACTIVITY"
    const val ROUTE_TEAM_QRCODE_ACTIVITY: String = "ROUTE_TEAM_QRCODE_ACTIVITY"
    const val ROUTE_USER_INFO_ACTIVITY: String = "ROUTE_USER_INFO_ACTIVITY"
    const val ROUTE_WALLET_RECHARGE_ACTIVITY: String = "ROUTE_WALLET_RECHARGE_ACTIVITY"
    const val ROUTE_UPDATE_PASSWORD_ACTIVITY: String = "ROUTE_UPDATE_PASSWORD_ACTIVITY"

    private val mRouteMap = HashMap<String, RouteJumpClickAction>()

    interface RouteJumpClickAction {
        fun onJump(context: Context, params: HashMap<String, Any>)
    }

    @JvmStatic
    fun put(key: String, action: RouteJumpClickAction) {
        mRouteMap[key] = action
    }

    @JvmStatic
    fun get(key: String): RouteJumpClickAction? {
        return mRouteMap[key]
    }
}