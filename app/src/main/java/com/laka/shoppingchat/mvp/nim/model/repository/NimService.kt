package com.laka.shoppingchat.mvp.nim.model.repository

import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo
import com.laka.shoppingchat.mvp.nim.const.NimConstant
import com.laka.shoppingchat.mvp.nim.model.bean.FriendDataResp
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @Author:sming
 * @Date:2019/8/8
 * @Description:
 */
interface NimService {

    /**
     * description:获取 首页分类列表
     **/
    @GET(NimConstant.API_SEARCH_USER)
    fun getSearchUser(@QueryMap paramsMap: MutableMap<String, Any>): Observable<FriendDataResp>

    @POST("group/get")
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun getGroupIdForQrCode(@FieldMap paramsMap: MutableMap<String, Any>): Observable<QrCodeInfo>

//    @GET(CircleConstant.API_GET_ARTICLE_LIST)
//    fun getArticleList(@QueryMap paramsMap: MutableMap<String, Any>): Observable<CircleArticleResponse>
//
//
//    @POST(CircleConstant.API_GET_CIRCLE_COMMENT)
//    @FormUrlEncoded
//    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
//    fun getCircleComment(@FieldMap paramsMap: MutableMap<String, Any>): Observable<CircleCommentResponse>
}