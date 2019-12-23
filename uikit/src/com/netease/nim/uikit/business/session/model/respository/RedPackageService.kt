package com.netease.nim.uikit.business.session.model.respository

import com.netease.nim.uikit.business.session.model.bean.*
import io.reactivex.Observable
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.*

/**
 * @Author:summer
 * @Date:2019/9/11
 * @Description:
 */
interface RedPackageService {

    //红包的接口需要设置header参数，用来在intercepter中区分普通接口和红包接口
    @Headers("isRedPackage:1")
    @POST("v1/hongbao/create")
    fun sendRedPackage(@Body body: RequestBody): Observable<RedPackageResponse>

    /**
     * 修改用户信息
     * */
    @GET("wallet/cashInfo")
    fun onLoadWalletInfo(@QueryMap params: HashMap<String, String>): Observable<WalletBean>

    //抢红包流程--------- 抢红包前状态查询
    @Headers("isRedPackage:1")
    @POST("v1/hongbao/ticket")
    fun redPackageTicket(@Body body: RequestBody): Observable<RedPackageTicketBean>

    //抢红包流程--------- 抢红包接口
    @Headers("isRedPackage:1")
    @POST("v1/hongbao/grab")
    fun robRedPackage(@Body body: RequestBody): Observable<RedPackageTicketBean>

    //抢红包流程--------- 抢红包接口
    @Headers("isRedPackage:1")
    @POST("v1/hongbao/grab")
    fun onLoadRedPackageRecord(@Body body: RequestBody): Observable<RedPackageRecordBean>

    //抢红包流程--------- 红包详情头部
    @Headers("isRedPackage:1")
    @POST("v1/hongbao/detailHeader")
    fun onLoadRedPackageDetailHeader(@Body body: RequestBody): Observable<RedPackageDetailHeader>

    //抢红包流程--------- 红包详情
    @Headers("isRedPackage:1")
    @POST("v1/hongbao/detailGrab")
    fun onLoadRedPackageDetail(@Body body: RequestBody): Observable<RedPackageDetailList>

    //抢红包流程--------- 获取服务器的时间戳
    @Headers("isRedPackage:1")
    @POST("v1/getUnixtime")
    fun onLoadUnixtime(): Observable<HashMap<String, String>>

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("wallet/pay")
    fun onPayPsdCheck(@FieldMap params: HashMap<String, String>): Observable<JSONObject>

}