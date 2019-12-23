package com.laka.shoppingchat.mvp.login.model.repository

import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.model.bean.*
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.http.*

/**
 * @Author:summer
 * @Date:2019/1/7
 * @Description:
 */
interface PhoneLoginService {

    // 手机登录
    @POST(LoginConstant.PHONE_LOGIN_URL)
    fun onPhoneLogin(@QueryMap paramsMap: HashMap<String, String>): Observable<UserBean>

    // 获取手机验证码
    @GET(LoginConstant.GET_VERIFICATION_CODE)
    fun onGetVerificationCode(@QueryMap params: HashMap<String, String>): Observable<VerificationCodeDataBean>

}