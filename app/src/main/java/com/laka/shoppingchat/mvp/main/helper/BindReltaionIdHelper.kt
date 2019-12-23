package com.laka.ergou.mvp.main.helper

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import com.laka.androidlib.eventbus.EventBusManager
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.dialog.LoadingDialog
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.login.model.event.UserEvent
import com.laka.shoppingchat.mvp.main.constant.HomeConstant
import com.laka.shoppingchat.mvp.main.contract.IBindRelationIdContract
import com.laka.shoppingchat.mvp.main.presenter.BindRelationIdPresenter
import com.laka.shoppingchat.mvp.shop.ShopDetailModuleNavigator
import com.laka.shoppingchat.mvp.user.UserCenterModuleNavigator
import com.laka.shoppingchat.mvp.user.constant.UserConstant
import com.laka.shoppingchat.mvp.user.utils.UserUtils

/**
 * @Author:summer
 * @Date:2019/8/14
 * @Description:绑定淘宝渠道ID
 */
class BindReltaionIdHelper {

    private var mContext: Activity
    private var mLoadingDialog: LoadingDialog
    private var mView: IBindRelationIdContract.IBindRelationIdView
    private var mPresenter: IBindRelationIdContract.IBindRelationIdPresenter

    constructor(context: Activity) {
        this.mContext = context
        mLoadingDialog = LoadingDialog(mContext)
    }

    init {
        mView = BindRelationIdView()
        mPresenter = BindRelationIdPresenter()
        mPresenter.setView(mView)
    }

    fun bindRelationId() {
        if (!UserUtils.isLogin()) {
            UserCenterModuleNavigator.startLoginActivity(mContext)
        } else {
            mPresenter.onTaoBaoAuthor(mContext)
        }
    }

    inner class BindRelationIdView : IBindRelationIdContract.IBindRelationIdView {

        override fun showLoading() {
            mLoadingDialog.show()
        }

        override fun dismissLoading() {
            mLoadingDialog.dismiss()
        }

        override fun showData(data: String) {

        }

        override fun showErrorMsg(msg: String?) {

        }

        /**
         * 淘宝授权成功
         * */
        override fun onTaoBaoAuthorSuccess() {
            if (TextUtils.isEmpty(UserUtils.getRelationId())) {
                mPresenter.getUnionCodeUrl()
            }
        }

        /**
         * 获取绑定淘宝渠道ID的H5链接成功
         * */
        override fun getUnionCodeUrlSuccess(url: String) {
            UserCenterModuleNavigator.startBindUnionCodeActivityForResult(
                mContext,
                url,
                HomeConstant.BIND_UNION_REQUEST_CODE
            )
        }

        /**
         * 绑定渠道ID 成功
         * */
        override fun handleUnionCodeSuccess() {
            ToastHelper.showCenterToast("淘宝授权成功")
            EventBusManager.postEvent(UserEvent(UserConstant.TAOBAO_AUTHOR_SUCCESS_EVENT))
            ShopDetailModuleNavigator.startTaoBaoAuthorSuccessActivity(mContext)
            mContext.overridePendingTransition(R.anim.anim_slide_in, R.anim.anim_slide_out)
        }

        /**
         * 绑定渠道ID失败
         * */
        override fun handleUnionCodeFail() {
            ShopDetailModuleNavigator.startTaoBaoAuthorFailActivity(mContext)
            mContext.overridePendingTransition(R.anim.anim_slide_in, R.anim.anim_slide_out)
        }
    }

    /**
     * activity 返回时调用
     * */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == HomeConstant.BIND_UNION_REQUEST_CODE
            && resultCode == UserConstant.BIND_UNION_RESULT_CODE
        ) {
            val code = data?.getStringExtra(UserConstant.UNION_CODE)
            val state = data?.getStringExtra(UserConstant.UNION_STATE)
            if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(state)) {
                mPresenter.handleUnionCode(code!!, state!!)
            }
        }
    }

    fun onActivityResult(data: Intent) {
        onActivityResult(
            data.getIntExtra(HomeConstant.KEY_REQUEST_CODE, -1),
            data.getIntExtra(HomeConstant.KEY_RESULT_CODE, -1), data
        )
    }

    fun release() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
        mPresenter.onViewDestroy()
    }

}