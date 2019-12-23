package com.laka.shoppingchat.mvp.main.model.repository

import com.laka.androidlib.util.rx.RxResponseComposer
import com.laka.shoppingchat.common.util.MetaDataUtils
import com.laka.shoppingchat.mvp.main.constant.HomeApiConstant
import com.laka.shoppingchat.mvp.main.model.bean.AppUpdateInfo
import io.reactivex.Observable
import okhttp3.ResponseBody

/**
 * @Author:Rayman
 * @Date:2019/1/22
 * @Description:主页API请求类---单独请求类。可供全局使用
 */
object HomeApiRepository {

    fun getAppUpdateInfo(): Observable<AppUpdateInfo> {
//        val channel = MetaDataUtils.getMateDataForApplicationInfo("UMENG_CHANNEL")
//        val params = HashMap<String, String?>()
//        params[HomeApiConstant.APP_CHANNEL] = channel
//        params[HomeApiConstant.PLATFORM] = "android"
        return HomeRetrofitHelper.newDownLoadInstance().getAppUpdateInfo(HashMap())
            .compose(RxResponseComposer.flatResponse())
    }

    fun downloadAPK(downLoadUrl: String): Observable<ResponseBody> {
        return HomeRetrofitHelper.newDownLoadInstance().downloadApk(downLoadUrl)
    }
}