package com.laka.shoppingchat.mvp.user.model.responsitory

import com.laka.androidlib.net.response.BaseResponse
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.model.bean.UserBean
import com.laka.shoppingchat.mvp.login.model.bean.VerificationCodeData
import com.laka.shoppingchat.mvp.user.constant.UserApiConstant
import com.laka.shoppingchat.mvp.user.model.bean.BannerAdvBean
import com.laka.shoppingchat.mvp.user.model.bean.RelationResponse
import com.laka.shoppingchat.mvp.user.model.bean.UrlResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * @Author:summer
 * @Date:2019/1/12
 * @Description:
 */
interface UserService {

    /**
     * description:更换手机号---获取验证码
     **/
    @POST(LoginConstant.GET_VERIFICATION_CODE)
    fun onGetVerificationCode(@QueryMap params: HashMap<String, String?>): Observable<BaseResponse<VerificationCodeData>>

    /**
     * description：获取联盟授权codeUrl
     * */
    @POST(UserApiConstant.API_GET_UNION_CODE_URL)
    fun getUnionCodeUrl(@QueryMap params: HashMap<String, String>): Observable<UrlResponse>

    @POST(UserApiConstant.API_HANDLE_UNION_CODE_URL)
    fun handleUnionCode(@QueryMap params: HashMap<String, String>): Observable<RelationResponse>

    @GET(UserApiConstant.API_GET_PRODUCT_DETAIL_SERVICE_URL)
    fun getProductDetailService(@QueryMap params: HashMap<String, String>): Observable<HashMap<String, String>>


}