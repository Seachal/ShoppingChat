package com.laka.shoppingchat.mvp.shopping.center.helper

import android.app.Activity
import android.graphics.Bitmap
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.laka.androidlib.eventbus.Event
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.SPHelper
import com.laka.androidlib.util.imageload.GlideLoader
import com.laka.shoppingchat.mvp.main.constant.HomeConstant
import com.laka.shoppingchat.mvp.main.constant.HomeEventConstant
import com.laka.shoppingchat.mvp.shopping.center.model.bean.HomePopupBean
import java.io.File
import java.io.FileOutputStream

/**
 * @Author:summer
 * @Date:2019/8/1
 * @Description:
 */
class ActivitsPopupHelper {

    fun downloadShopDetailImage(context: Activity, popupBean: HomePopupBean?) {
        LogUtils.info("popup image download start----$popupBean")
        if (popupBean == null) {
            SPHelper.saveObject(HomeConstant.KEY_HOME_POPUP_BEAN, null)
            return
        }
        GlideLoader.downloadImage(context,"${popupBean?.imgPath}",object :GlideLoader.DownloadListener{
            override fun onSuccess(resource: Bitmap?) {
                resource?.let {
                    LogUtils.info("popup image download success----$it")
                    val folder = File(context.cacheDir, HomeConstant.ERGOU_IMAGE_CACHE_PATH)
                    if (!folder.exists()) {
                        LogUtils.info("popup image !folder.exists()")
                        if (folder.mkdirs()) {
                            LogUtils.info("popup image folder.mkdirs() success")
                            onSaveBitmap(folder, it, popupBean)
                        } else {
                            LogUtils.info("popup image保存操作失败")
                        }
                    } else {
                        onSaveBitmap(folder, it, popupBean)
                    }
                }
            }

            override fun onFailed() {

            }
        })

    }

    private fun onSaveBitmap(folder: File, resource: Bitmap, popupBean: HomePopupBean?) {
        LogUtils.info("popup image save start---$resource")
        val file = File(folder, "ergou_${System.currentTimeMillis()}.png")
        if (resource.isRecycled) return
        val isSave = resource.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
        if (isSave) {
            popupBean?.localImgPath = file.absolutePath
            val preBean = SPHelper.getObject(HomeConstant.KEY_HOME_POPUP_BEAN, HomePopupBean::class.java)
            if (!TextUtils.isEmpty(preBean?.localImgPath)
                    && File(preBean?.localImgPath).exists()) {
                File(preBean?.localImgPath).delete()
            }
            if (preBean != null) {
                popupBean?.preShopTimestamp = preBean.preShopTimestamp
                SPHelper.saveObject(HomeConstant.KEY_HOME_POPUP_BEAN, popupBean)
            } else {
                SPHelper.saveObject(HomeConstant.KEY_HOME_POPUP_BEAN, popupBean)
            }
            LogUtils.info("popup image save success")
            resource.recycle()
            EventBusManager.postEvent(Event(HomeEventConstant.EVENT_IMAGE_DOWNLOAD_FINISH, 0))
        }
    }


}