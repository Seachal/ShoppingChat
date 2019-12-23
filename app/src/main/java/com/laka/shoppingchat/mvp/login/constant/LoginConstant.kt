package com.laka.shoppingchat.mvp.login.constant

/**
 * @Author:summer
 * @Date:2018/12/19
 * @Description:
 */
interface LoginConstant {
    companion object {
        // 接口
        // 手机号注册
        const val PHONE_LOGIN_URL = "user/login"
        // 获取验证码
        const val GET_VERIFICATION_CODE = "sms/sendCode"
        const val PHONE: String = "phone"
        const val PRE_TIME: String = "pre_time"
        const val VERIFIY_TYPE: String = "verifiy_type"
        const val CODE: String = "code"
        const val OS_VERSION: String = "os_version"
        const val PLATFORM: String = "platform"
        const val USER_LOGIN_INFO: String = "user_info"
        const val PHONE_LOGIN_TYPE: Int = 1003
        const val TAOBAO_LOGIN_TYPE: Int = 1004
        const val USER_INFO_FILENAME: String = "user_info_filename"
        const val ANDROID_PLATFROM: String = "Android"
        const val JPUSH_REGISTER_ID: String = "jpush_id"
        const val JPUSH_REGISTER_CHANNEL: String = "channel"
        const val RESULT_SCAN_QR: String = "result_scan_qr"
        const val PHONE_NUMBER: String = "phone_number"
        const val PRE_GET_VERIFICATION_CODE_TIME: String = "pre_get_verification_code_time"

        /**登录类型（微信、手机号、普通注册）*/
        const val LOGIN_TYPE: String = "login_type"
        // 登录类型
        const val LOGIN_TYPE_PHONE: Int = 0x1103  //普通手机登录
        //验证码类型
        const val VERIFICATION_TYPE_LOGIN: Int = 1 //普通手机登录
        const val VERIFICATION_FORGET_PAY: Int = 2 //普通手机登录

        const val LOGIN_TYPE_PHONE_LOGIN: Int = 1 //普通手机登录
        const val EVENT_LOGIN_WX: String = "event_login_wx"//微信登录event
        //startActivityForResult 的请求 code
        const val REQUEST_SCAN_QR_CODE: Int = 0x1103
        const val RESULT_SCAN_QR_CODE: Int = 0x1104
        const val REQUEST_CHOICE_PHOTO_CODE: Int = 0x1105

    }
}
