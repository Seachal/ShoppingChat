package com.laka.shoppingchat.mvp.shop

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.laka.androidlib.util.BaseActivityNavigator
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.shop.constant.ShopDetailConstant
import com.laka.shoppingchat.mvp.shop.view.activity.*
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import org.jetbrains.anko.startActivity

/**
 * @Author:summer
 * @Date:2019/1/8
 * @Description:
 */
object ShopDetailModuleNavigator {

    private val mShopDetailActivityList: ArrayList<ShopDetailActivity> = ArrayList()
    private var mShopDetailActivityStackLength: Int = 5

    init {
        mShopDetailActivityStackLength = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.M
        ) {
            7
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            10
        } else {
            5
        }
    }

    @JvmStatic
    fun addElementForActivityStack(activity: ShopDetailActivity) {
        if (mShopDetailActivityList.size >= mShopDetailActivityStackLength
            && mShopDetailActivityStackLength > 0
        ) {
            val firstActivity = mShopDetailActivityList[0]
            if (firstActivity != null && !firstActivity.isFinishing && !firstActivity.isDestroyed) {
                firstActivity.finish(true)
            }
        }
        mShopDetailActivityList.add(activity)
    }

    @JvmStatic
    fun removeElementForActivityStack() {
        if (mShopDetailActivityList.isNotEmpty()) {
            mShopDetailActivityList.removeAt(mShopDetailActivityList.size - 1)
        }
    }

    @JvmStatic
    fun removeElementForActivityStack(index: Int) {
        if (index >= 0 && index < mShopDetailActivityList.size) {
            mShopDetailActivityList.removeAt(index)
        }
    }

    @JvmStatic
    fun setShopDetailActivityListSize(size: Int) {
        if (size > 0) {
            this.mShopDetailActivityStackLength = size
        }
    }

    @JvmStatic
    fun startShopDetailActivity(context: Context, productId: String) {
        val bundle = Bundle()
        bundle.putString(ShopDetailConstant.PRODUCT_ID, productId)
        bundle.putInt(ShopDetailConstant.ENTRANCE, ShopDetailConstant.PRODUCT_ITEM_CLICK_SKELETON)
        BaseActivityNavigator.startActivity(context, ShopDetailActivity::class.java, bundle)
    }

    @JvmStatic
    fun startShopDetailActivityForResult(context: Activity, productId: String, requestCode: Int) {
        val bundle = Bundle()
        bundle.putString(ShopDetailConstant.PRODUCT_ID, productId)
        bundle.putInt(ShopDetailConstant.ENTRANCE, ShopDetailConstant.PRODUCT_ITEM_CLICK_SKELETON)
        BaseActivityNavigator.startActivityForResult(
            context,
            ShopDetailActivity::class.java,
            bundle,
            requestCode
        )
    }

    fun startTaoBaoAuthorSuccessActivity(context: Context) {
        BaseActivityNavigator.startActivity(context, TaoBaoAuthorSuccessActivity::class.java)
    }

    fun startTaoBaoAuthorFailActivity(context: Context) {
        BaseActivityNavigator.startActivity(context, TaoBaoAuthorFailActivity::class.java)
    }

    fun startCouponBuyActivity(context: Context, productId: String) {
        if (loginHandle(context)) {
            context.startActivity<CouponBuyActivity>(
                ShopDetailConstant.PRODUCT_ID to productId
            )
        }
    }

    fun startCouponBuySuccessActivity(context: Context) {
        if (loginHandle(context)) {
            context.startActivity<CouponBuySuccessActivity>()
        }
    }

    private fun loginHandle(context: Context): Boolean {
        return if (!UserUtils.isLogin()) {
            LoginModuleNavigator.startLoginActivity(context)
            false
        } else {
            true
        }
    }

}