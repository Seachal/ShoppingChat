package com.laka.shoppingchat.mvp.user.constant

/**
 * @Author:Rayman
 * @Date:2019/1/7
 * @Description:用户模块常量类
 */
object UserConstant {

    // 用户模块事件类型
    const val LOGIN_EVENT: Int = 1001
    const val LOGOUT_EVENT: Int = 1002
    const val EDIT_USER_INFO: Int = 1003
    const val TAOBAO_AUTHOR_SUCCESS_EVENT: Int = 1004 //淘宝授权成功
    const val READ_COMMISSION_MSG_EVENT: Int = 1009
    const val READ_OTHER_MSG_EVENT: Int = 1010
    const val SOCKET_CONNECT_EVENT: Int = 1012 //发送socket重连事件，进行Socket重连

    //startActivity 的 requestCode
    const val BIND_UNION_REQUEST_CODE: Int = 0x1001
    const val BIND_UNION_RESULT_CODE: Int = 0X1002
    //url
    const val UNION_CODE: String = "code"
    const val UNION_STATE: String = "state"
    //key
    const val KEY_IM_ACCOUNT: String = "key_im_account"
    const val KEY_IM_MARKS: String = "key_im_marks"
    //request code
    const val REQUEST_CODE_ALIAS: Int = 0x100 //设置备注
    //event
    const val EVENT_MODIFY_ALIAS:String=  "event_modify_alias"
}