package com.laka.shoppingchat.mvp.user.model.responsitory

import com.laka.shoppingchat.mvp.user.constant.UserCenterConstant
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.http.*

/**
 * @Author:summer
 * @Date:2019/1/12
 * @Description:
 */
interface UserCenterService {

    @POST(UserCenterConstant.API_SETTING_PASSWORD)
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun settingPassword(@FieldMap params: MutableMap<String, Any>): Observable<JSONObject>

    @POST(UserCenterConstant.API_CHECK_UP_PAY)
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun checkUpPay(@FieldMap params: MutableMap<String, Any>): Observable<JSONObject>


    @POST(UserCenterConstant.API_EDIT_PASSWORD)
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun editPassword(@FieldMap params: MutableMap<String, Any>): Observable<JSONObject>

    @GET(UserCenterConstant.API_SEND_CODE)
    fun sendCode(@QueryMap paramsMap: MutableMap<String, Any>): Observable<JSONObject>

    @POST(UserCenterConstant.API_CHECK_CODE)
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun checkCode(@FieldMap paramsMap: MutableMap<String, Any>): Observable<JSONObject>

    // 获取手机验证码
    @POST(UserCenterConstant.API_CHANGE_MOBILE)
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun changeMobile(@FieldMap paramsMap: MutableMap<String, Any>): Observable<JSONObject>

    // 获取手机验证码
    @POST(UserCenterConstant.API_FORGET_PSW)
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun forgetPsw(@FieldMap paramsMap: MutableMap<String, Any>): Observable<JSONObject>
}