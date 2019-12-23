package com.laka.shoppingchat.mvp.wallet.model.responsitory

import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecordHeader
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecordList
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * @Author:summer
 * @Date:2019/9/11
 * @Description:
 */
interface RedPackageRecordService {

    @Headers("isRedPackage:1")
    @POST("v1/hongbao/receiveRecordHeader")
    fun receiveRecordHeader(@Body body: RequestBody): Observable<RedPackageRecordHeader>

    @Headers("isRedPackage:1")
    @POST("v1/hongbao/receiveRecords")
    fun receiveRecordList(@Body body: RequestBody): Observable<RedPackageRecordList>

    @Headers("isRedPackage:1")
    @POST("v1/hongbao/sendRecordHeader")
    fun sendRecordHeader(@Body body: RequestBody): Observable<RedPackageRecordHeader>

    @Headers("isRedPackage:1")
    @POST("v1/hongbao/sendRecords")
    fun sendRecordList(@Body body: RequestBody): Observable<RedPackageRecordList>

}