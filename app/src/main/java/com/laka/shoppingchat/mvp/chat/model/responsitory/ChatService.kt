package com.laka.shoppingchat.mvp.user.model.responsitory

import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @Author:summer
 * @Date:2019/1/12
 * @Description:
 */
interface ChatService {

    @POST("group/qr")
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun getEncryQrCodeInfo(@FieldMap params: MutableMap<String, Any>): Observable<QrCodeInfo>

    @POST("group/get")
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun getDecryptQrCodeInfo(@FieldMap params: MutableMap<String, Any>): Observable<QrCodeInfo>

}