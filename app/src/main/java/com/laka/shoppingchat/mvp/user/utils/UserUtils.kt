package com.laka.shoppingchat.mvp.user.utils

import android.text.TextUtils
import com.laka.androidlib.util.SPHelper
import com.laka.androidlib.util.StringUtils
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.model.bean.Rank
import com.laka.shoppingchat.mvp.login.model.bean.UserInfoBean
import com.netease.nim.uikit.impl.NimUIKitImpl
import com.netease.nimlib.sdk.StatusBarNotificationConfig
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author:summer
 * @Date:2019/1/14
 * @Description:用户工具类
 */
object UserUtils {

    private var userInfoBean: UserInfoBean? = null
    private var notificationConfig: StatusBarNotificationConfig? = null
    fun isLogin(): Boolean {
        if (userInfoBean == null || userInfoBean?.userBean == null) {
            return false
        } else if (userInfoBean?.userBean?.userImInfo == null) {
            return false
        } else if (StringUtils.isEmpty(userInfoBean?.userBean?.userImInfo?.accid)) {
            return false
        } else if (StringUtils.isEmpty("${userInfoBean?.userBean?.userImInfo?.token}")) {
            return false
        } else if (userInfoBean?.userBean?.userTokenInfo == null) {
            return false
        } else if (StringUtils.isEmpty(userInfoBean?.userBean?.userTokenInfo?.token)) {
            return false
        }
        return true
    }

    @JvmStatic
    fun setNotificationConfig(notificationConfig: StatusBarNotificationConfig) {
        UserUtils.notificationConfig = notificationConfig
    }

    @JvmStatic
    fun getNotificationConfig(): StatusBarNotificationConfig? {
        return notificationConfig
    }

    fun getUploadObjectKey(): String {
        val sdf = SimpleDateFormat("yyyy/MMdd")
        return "avatar/${sdf.format(Date())}_${userInfoBean?.userBean?.id}_${System.currentTimeMillis()}.png"
    }

    fun getUserId(): Int {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.userBean?.id!!
        } else {
            0
        }
    }

    fun getUserToken(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null
            && userInfoBean?.userBean?.userTokenInfo != null
        ) {
            "${userInfoBean?.userBean?.userTokenInfo?.token}"
        } else {
            ""
        }
    }

    fun getUserPhone(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.userBean?.phone!!
        } else {
            ""
        }
    }

    fun getUserName(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.getNickName()!!
        } else {
            ""
        }
    }

    fun getUserAvatar(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.getAvatarUrl()!!
        } else {
            ""
        }
    }

    fun getUserGender(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.getGender()!!
        } else {
            ""
        }
    }

    fun getWeChatId(): String? {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.userBean?.wechat_id!!
        } else {
            ""
        }
    }

    fun getWeChatAccount(): String? {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.userBean?.wxAccount!!
        } else {
            ""
        }
    }

    fun isWeChatBind(): Boolean {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.userBean?.wxBind!!
        } else {
            false
        }
    }

    fun getUserAliAccount(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.userBean?.aliUserName!!
        } else {
            ""
        }
    }

    fun getUserAliRealName(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.userBean?.aliUserRealName!!
        } else {
            ""
        }
    }

    fun getUserAgentCode(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.userBean?.agentCode!!
        } else {
            ""
        }
    }

    fun getWxAgentCode(): String? {
        return if (userInfoBean != null && userInfoBean?.userBean != null && userInfoBean?.userBean?.wxAgentCode != null) {
            userInfoBean?.userBean?.wxAgentCode!!
        } else {
            ""
        }
    }


    @JvmStatic
    fun setImAccount(account: String) {
        NimUIKitImpl.setAccount(account)
    }

    @JvmStatic
    fun getImAccount(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null
            && userInfoBean?.userBean?.userImInfo?.accid != null
        ) {
            "${userInfoBean?.userBean?.userImInfo?.accid}"
        } else {
            ""
        }
    }

    @JvmStatic
    fun getPayPasswordStatus(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null
            && userInfoBean?.userBean?.userImInfo?.pay_password != null
        ) {
            "${userInfoBean?.userBean?.userImInfo?.pay_password}"
        } else {
            "0"
        }
    }

    @JvmStatic
    fun getQR(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null
            && userInfoBean?.userBean?.userImInfo?.qr != null
        ) {
            "${userInfoBean?.userBean?.userImInfo?.qr}"
        } else {
            ""
        }
    }

    @JvmStatic
    fun getMobile(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null
            && userInfoBean?.userBean?.userImInfo?.mobile != null
        ) {
            "${userInfoBean?.userBean?.userImInfo?.mobile}"
        } else {
            "0"
        }
    }

    @JvmStatic
    fun updateMobile(mobile: String) {
        if (userInfoBean?.userBean?.userImInfo != null) {
            userInfoBean?.userBean?.userImInfo?.mobile = mobile
            updateUserInfo()
        }
    }

    @JvmStatic
    fun updatePayPasswordStatus(pay: Boolean) {
        userInfoBean?.userBean?.userImInfo?.pay_password = if (pay) {
            1
        } else {
            0
        }
        updateUserInfo()
    }

    @JvmStatic
    fun getImToken(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null
            && userInfoBean?.userBean?.userImInfo?.token != null
        ) {
            "${userInfoBean?.userBean?.userImInfo?.token}"
        } else {
            ""
        }
    }

    fun getImUserId(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null
            && userInfoBean?.userBean?.userImInfo?.id != null
        ) {
            "${userInfoBean?.userBean?.userImInfo?.id}"
        } else {
            ""
        }
    }

    fun isOpenChat(): Boolean {
        return if (userInfoBean == null || userInfoBean?.userBean == null) {
            false
        } else {
            userInfoBean?.userBean?.isOpenChat!!
        }
    }

    fun getTmpToken(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.userBean?.tmp_token!!
        } else {
            ""
        }
    }

    fun getAgentCode(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            userInfoBean?.userBean?.agentCode!!
        } else {
            ""
        }
    }

    fun getUnReadMsgCount(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null && userInfoBean?.userBean?.unreadMsgCount != null) {
            userInfoBean?.userBean?.unreadMsgCount!!
        } else {
            "0"
        }
    }

    fun getRelationId(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null && userInfoBean?.userBean?.relation_id != null) {
            userInfoBean?.userBean?.relation_id!!
        } else {
            ""
        }
    }

    fun getAgentLevel(): String {
        return if (userInfoBean != null && userInfoBean?.userBean != null && userInfoBean?.userBean?.agent_level != null) {
            userInfoBean?.userBean?.agent_level!!
        } else {
            ""
        }
    }

    fun getUserInfoBean(): UserInfoBean {
        return if (userInfoBean != null) {
            userInfoBean!!
        } else {
            UserInfoBean()
        }
    }

    fun getAgentInfo(): Rank {
        return if (userInfoBean != null && userInfoBean?.userBean != null && userInfoBean?.userBean?.rank != null) {
            userInfoBean?.userBean?.rank!!
        } else {
            Rank()
        }
    }

    /**是否有未读消息*/
    fun hasUnReadMsgCount(): Boolean {
        return if (userInfoBean != null && userInfoBean?.userBean != null) {
            when {
                userInfoBean?.userBean?.other_msg_read != "0" -> // "0" 表示已读
                    true
                userInfoBean?.userBean?.comm_msg_read != "0" -> // "0" 表示已读
                    true
                else -> false
            }
        } else {
            false
        }
    }

    fun updateUserName(userName: String?) {
        userInfoBean?.userBean?.nickname = if (TextUtils.isEmpty(userName)) {
            ""
        } else {
            userName!!
        }
        updateUserInfo()
    }

    fun updateUserGender(gender: String?) {
        userInfoBean?.userBean?.gender = if (TextUtils.isEmpty(gender)) {
            ""
        } else {
            gender!!
        }
        updateUserInfo()
    }

    fun updateUserAvatar(avatar: String?) {
        userInfoBean?.userBean?.avatar = if (TextUtils.isEmpty(avatar)) {
            ""
        } else {
            avatar!!
        }
        updateUserInfo()
    }

    fun updateUserPhone(phone: String) {
        userInfoBean?.userBean?.phone = phone
        updateUserInfo()
    }

    fun updateBindAliAccount(account: String, realName: String) {
        userInfoBean?.userBean?.aliUserName = account
        userInfoBean?.userBean?.aliUserRealName = realName
        updateUserInfo()
    }

    fun updateWeChatInfo(wxId: String) {
        userInfoBean?.userBean?.wxAccount = wxId
        updateUserInfo()
    }

    fun updateWeChatBind(isBind: Boolean) {
        userInfoBean?.userBean?.wxBind = isBind
        updateUserInfo()
    }

    fun updateOpenChat(isOpen: Boolean) {
        userInfoBean?.userBean?.openGouXiaoEr = if (isOpen) 1 else 0
        updateUserInfo()
    }

    fun updateUnReadMsgCount(count: String) {
        userInfoBean?.userBean?.unreadMsgCount = count
        updateUserInfo()
    }

    fun updateRelationId(id: String) {
        userInfoBean?.userBean?.relation_id = id
        updateUserInfo()
    }


    fun clearOtherUnReadMsg() {
        userInfoBean?.userBean?.other_msg_read = "0"
        updateUserInfo()
    }

    fun clearCommissionUnReadMsg() {
        userInfoBean?.userBean?.comm_msg_read = "0"
        updateUserInfo()
    }


    fun clearUserInfo() {
        // 退出登陆的时候，清除SP数据
        SPHelper.clearByFileName(LoginConstant.USER_INFO_FILENAME)
        userInfoBean = null
    }

    private fun updateUserInfo() {
        SPHelper.saveObject(
            LoginConstant.USER_INFO_FILENAME,
            LoginConstant.USER_LOGIN_INFO,
            userInfoBean
        )
    }

    fun updateUserInfo(userInfoBean: UserInfoBean) {
        SPHelper.saveObject(
            LoginConstant.USER_INFO_FILENAME,
            LoginConstant.USER_LOGIN_INFO,
            userInfoBean
        )
        NimUIKitImpl.setAccount(userInfoBean.userBean?.userImInfo?.accid)
        UserUtils.userInfoBean = userInfoBean
    }

    /**
     * description:更新本地信息
     **/
    fun updateLocalUserInfo() {
        userInfoBean = SPHelper.getObject(
            LoginConstant.USER_INFO_FILENAME,
            LoginConstant.USER_LOGIN_INFO,
            UserInfoBean::class.java
        )
    }

}