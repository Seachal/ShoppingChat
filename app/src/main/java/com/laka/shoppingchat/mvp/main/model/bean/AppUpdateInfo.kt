package com.laka.shoppingchat.mvp.main.model.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author:Rayman
 * @Date:2019/1/22
 * @Description:App升级数据Bean
 */
class AppUpdateInfo(
    @SerializedName("id") var id: Int = -1,
    @SerializedName("state") var state: Int = 0,//1：提醒更新  2：强制更新
    @SerializedName("platform") var platform: String = "", //平台
    @SerializedName("url") var downLoadUrl: String = "", //apk 下载url
    @SerializedName("version_build") var versionBuild: Int = -1, //app构建版本
    @SerializedName("version_name") var appVersionName: String = "", //app版本名称
    @SerializedName("description") var updateDescription: String = "" //版本更新描述信息
) {
    fun isForceUpdate(): Boolean { //强制更新
        return state == 2 //1：提醒更新  2：强制更新
    }

    fun isNeedUpdate(): Boolean {
        //只要 state 有数据，则判断为需要更新
        return state == 1 || state == 2
    }
}
