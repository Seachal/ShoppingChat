package com.laka.shoppingchat.mvp.wallet.model.responsitory

import com.laka.shoppingchat.mvp.wallet.constant.WalletConstant
import com.laka.shoppingchat.mvp.wallet.model.bean.MyWalletBean
import com.laka.shoppingchat.mvp.wallet.model.bean.RechargeResp
import com.laka.shoppingchat.mvp.wallet.model.bean.WalletInitResponse
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.http.*

/**
 * @Author:summer
 * @Date:2019/9/10
 * @Description:
 */
interface WalletService {

    /**
     * 修改用户信息
     * */
    @GET(WalletConstant.API_GET_WALLET)
    fun onLoadWalletInfo(@QueryMap params: MutableMap<String, Any>): Observable<MyWalletBean>

    @POST(WalletConstant.API_RECHARGE)
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun recharge(@FieldMap paramsMap: MutableMap<String, Any>): Observable<RechargeResp>

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("wallet/pay")
    fun onPayPsdCheck(@FieldMap params: MutableMap<String, Any>): Observable<JSONObject>

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("v1")
    fun onLoadPayToken(@FieldMap params: MutableMap<String, Any>): Observable<JSONObject>

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("wallet/init")
    fun onWalletInit(@FieldMap params: MutableMap<String, Any>): Observable<WalletInitResponse>

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("wallet/manageInit")
    fun onWalletManagerInit(@FieldMap params: MutableMap<String, Any>): Observable<WalletInitResponse>

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("order/cash")
    fun onWithdraw(@FieldMap params: MutableMap<String, Any>): Observable<WalletInitResponse>

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("order/charge")
    fun onRecharge(@FieldMap params: MutableMap<String, Any>): Observable<HashMap<String, Any>>

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("wallet/initCallback")
    fun onInitCallBack(@FieldMap params: MutableMap<String, Any>): Observable<JSONObject>

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("wallet/walletStatus")
    fun onWalletStatus(@FieldMap params: MutableMap<String, Any>): Observable<MutableMap<String, Any>>

    // 提现手续费
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("order/serviceCash")
    fun onLoadServiceCharge(@FieldMap params: MutableMap<String, Any>): Observable<HashMap<String,Any>>

    // 提现手续费费率
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("wallet/serviceCashRate")
    fun onLoadServiceChargeRate(@FieldMap params: MutableMap<String, Any>): Observable<HashMap<String,Any>>

}