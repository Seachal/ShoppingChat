package com.laka.shoppingchat.mvp.main.constant

/**
 * @Author:Rayman
 * @Date:2019/1/9
 * @Description:项目主页EventBus事件常量
 */
object HomeEventConstant {

    // Web页面初始化完毕事件
    const val EVENT_WEB_VIEW_INIT_COMPLETED = "EVENT_WEB_VIEW_INIT_COMPLETED"
    const val EVENT_WEB_VIEW_LOAD_FINISH = "EVENT_WEB_VIEW_LOAD_FINISH"
    // 网络变化---对应网络失败，网络重新切换，网络失败下的重新加载数据
    const val EVENT_ON_NETWORK_ERROR = "EVENT_ON_NETWORK_ERROR"
    const val EVENT_ON_NETWORK_RESUME = "EVENT_ON_NETWORK_RESUME"
    const val EVENT_RECYCLER_VIEW_SCROLL = "EVENT_RECYCLER_VIEW_SCROLL"
    const val EVENT_APPBARLAYOUT_STATE = "EVENT_APPBARLAYOUT_STATE"
    // app检查更新完成
    const val EVENT_CHECK_UPDATE_FINISH = "EVENT_CHECK_UPDATE_FINISH"
    //活动图片下载完成
    const val EVENT_IMAGE_DOWNLOAD_FINISH = "EVENT_IMAGE_DOWNLOAD_FINISH"
    //绑定渠道ID成功
    const val EVENT_BIND_RELEATION_ID_SUCCESS = "EVENT_BIND_RELEATION_ID_SUCCESS"
}