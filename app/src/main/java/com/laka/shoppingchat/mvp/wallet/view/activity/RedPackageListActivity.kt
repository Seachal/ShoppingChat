package com.laka.shoppingchat.mvp.wallet.view.activity

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.network.NetworkUtils
import com.laka.androidlib.widget.dialog.JAlertDialog
import com.laka.androidlib.widget.refresh.OnResultListener
import com.laka.androidlib.widget.roundedimageview.RoundedImageView
import com.laka.androidlib.widget.text.RiseNumberTextView
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.dsl.refreshInit
import com.laka.shoppingchat.common.ext.loadImage
import com.laka.shoppingchat.mvp.user.utils.UserUtils
import com.laka.shoppingchat.mvp.user.helper.ImUserInfoHelper
import com.laka.shoppingchat.mvp.wallet.constract.IRedPackageRecordConstract
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecord
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecordHeader
import com.laka.shoppingchat.mvp.wallet.model.bean.RedPackageRecordList
import com.laka.shoppingchat.mvp.wallet.presenter.RedPackageRecordPresenter
import com.laka.shoppingchat.mvp.wallet.view.adapter.RedPackAdapter
import com.netease.nim.uikit.business.session.attachment.RobRedPackageAttachment
import com.netease.nim.uikit.business.session.constant.RedPackageConstant
import com.netease.nim.uikit.business.session.helper.ReceiveRedPackageHelper
import com.netease.nim.uikit.business.session.utils.RoundUtils
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import kotlinx.android.synthetic.main.activity_redpack_list.*
import org.json.JSONObject

class RedPackageListActivity : BaseMvpActivity<RedPackageRecordList>(),
    IRedPackageRecordConstract.IRedPackageRecordView {

    companion object {
        const val SEND_RED_PACKAGE_RECORD = 1
        const val RECEIVE_RED_PACKAGE_RECORD = 2
    }

    private lateinit var mReceiveRedPackageHelper: ReceiveRedPackageHelper
    private lateinit var mResultListener: OnResultListener
    private var mCurrentState = RECEIVE_RED_PACKAGE_RECORD
    private lateinit var mPresenter: RedPackageRecordPresenter
    private lateinit var mAdapter: RedPackAdapter
    private lateinit var mDialog: JAlertDialog
    override fun setContentView(): Int = R.layout.activity_redpack_list
    val data = mutableListOf<RedPackageRecord>()
    var ivAvatar: HeadImageView? = null
    var tvUserName: TextView? = null
    var tvPrice: RiseNumberTextView? = null
    var tvLeftNum: TextView? = null
    var tvRightNum: TextView? = null
    var tvSendCount: TextView? = null
    var tvRightTxt: TextView? = null
    var tvRedPackType: TextView? = null
    var flLoading: FrameLayout? = null
    override fun initIntent() {

    }

    override fun finish() {
        if (::mReceiveRedPackageHelper.isInitialized) {
            mReceiveRedPackageHelper.finish()
        }
        super.finish()
    }

    override fun onDestroy() {
        if (::mReceiveRedPackageHelper.isInitialized) {
            mReceiveRedPackageHelper.onDestory()
        }
        super.onDestroy()
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColorNoTranslucent(this, resources.getColor(R.color.color_f35543))
        } else {
            super.setStatusBarColor(color)
        }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        mCurrentState = RECEIVE_RED_PACKAGE_RECORD
//        initViews()
//        initData()
//        initEvent()
//    }

    override fun initViews() {
        mReceiveRedPackageHelper = ReceiveRedPackageHelper(this)
        title_bar?.run {
            setLeftText("关闭")
            setBackGroundColor(R.color.color_f35543)
            setLeftTextColor(R.color.color_ffe2b1)
            setTitleTextColor(R.color.color_ffe2b1)
            setRightIcon(R.drawable.selector_redpack_right)
            setTitle("收到的红包")
            showDivider(false)
            setOnRightClickListener {
                showBottom()
            }
        }

        mAdapter = RedPackAdapter(data)
        val headerView = layoutInflater.inflate(
            R.layout.header_redpack_list,
            ll_root as ViewGroup,
            false
        )
        flLoading = findViewById(R.id.fl_loading)
        ivAvatar = headerView.findViewById(R.id.ivAvatar)
        tvUserName = headerView.findViewById(R.id.tvUserName)
        tvPrice = headerView.findViewById(R.id.tvPrice)
        tvLeftNum = headerView.findViewById(R.id.tvLeftNum)
        tvRightNum = headerView.findViewById(R.id.tvRightNum)
        tvSendCount = headerView.findViewById(R.id.tv_send_count)
        tvRightTxt = headerView.findViewById(R.id.tv_right_txt)
        tvRedPackType = headerView.findViewById(R.id.tvRedPackType)
        mAdapter.addHeaderView(headerView)
        refreshInit {
            view = mRvList
            adapter = mAdapter
            enableRefresh = false
            onRequest = { page, resultListener ->
                mResultListener = resultListener
                if (page == 1) {
                    loadRecordHeader()
                } else {
                    onLoadRecordList(page)
                }
            }
        }
        mRvList.enableLoadMore(true)
        mRvList.refresh(false)
        mRvList.defaultPage = Int.MAX_VALUE
        showLoadingView()
    }

    override fun showData(data: RedPackageRecordList) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    private fun showLoadingView() {
        flLoading?.visibility = View.VISIBLE
    }

    private fun dismissLoadingView() {
        flLoading?.visibility = View.GONE
    }

    override fun initEvent() {
        mAdapter.setOnItemClickListener { _, _, position ->
            val attachment = RobRedPackageAttachment()
            attachment.setmHongBaoNo(mAdapter.mData[position].hongbao_no)
            if (mCurrentState == SEND_RED_PACKAGE_RECORD) {
                attachment.setmUserId(UserUtils.getImAccount())
                attachment.setmToId(UserUtils.getImAccount())
                attachment.ope = -1
                //todo 加载红包头部
                mReceiveRedPackageHelper.onRedPackageRecordClickStart(attachment)
            } else if (mCurrentState == RECEIVE_RED_PACKAGE_RECORD) {
                attachment.setmUserId(mAdapter.mData[position].sender_user_id)
                attachment.setmToId(UserUtils.getImAccount())
                attachment.ope = -1
                //todo 加载红包头部
                mReceiveRedPackageHelper.onRedPackageRecordClickStart(attachment)
            }
        }
    }

    override fun createPresenter(): IBasePresenter<*> {
        mPresenter = RedPackageRecordPresenter()
        return mPresenter
    }

    override fun initData() {
        var mUserInfo = ImUserInfoHelper.getCurrentUserInfo()
        mUserInfo?.let {
            ivAvatar?.loadImage(it.avatar)
            tvUserName?.text = "${it.name}共收到"
        }
        tvRedPackType?.text = "收到红包"
    }

    private fun loadRecordHeader() {
        val json = JSONObject()
        json.put("user_id", UserUtils.getImAccount())
        if (mCurrentState == RECEIVE_RED_PACKAGE_RECORD) {
            mPresenter.getRedPackageRecordHeader(json)
        } else if (mCurrentState == SEND_RED_PACKAGE_RECORD) {
            mPresenter.getSendRedPackageRecordHeader(json)
        }
    }

    private fun onLoadRecordList(page: Int) {
        val json = JSONObject()
        json.put("user_id", UserUtils.getImAccount())
        json.put("page", page)
        json.put("size", RedPackageConstant.DEFAULT_PAGE_SIZE)
        if (mCurrentState == SEND_RED_PACKAGE_RECORD) {
            mPresenter.getSendRedPackageRecordList(json)
        } else if (mCurrentState == RECEIVE_RED_PACKAGE_RECORD) {
            mPresenter.getRedPackageRecordList(json)
        }
    }

    private fun showBottom() {
        if (!::mDialog.isInitialized) {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_red_pack, null)
            mDialog = JAlertDialog.Builder(this)
                .setFromBottom()
                .setCancelable(true)
                .setAnimation(R.style.bottom_menu_animation)
                .setContentView(view)
                .setOnClick(R.id.tvReceive)
                .setOnClick(R.id.tvSend)
                .setOnClick(R.id.tvCancel)
                .setWightPercent(1f)
                .setOnJAlertDialogCLickListener { dialog, _, position ->
                    when (position) {
                        0 -> switchType(true) //收到的红包记录
                        1 -> switchType(false) //发出的红包记录
                        2 -> mDialog.dismiss()
                    }
                    dialog.dismiss()
                }
                .create()
        }
        mDialog.show()
    }

    private fun switchType(type: Boolean) {
        if (!::mAdapter.isInitialized) {
            return
        }
        if (mCurrentState == RECEIVE_RED_PACKAGE_RECORD && type) return
        if (mCurrentState == SEND_RED_PACKAGE_RECORD && !type) return
        mAdapter.mData.clear()
        mAdapter.notifyDataSetChanged()
        var mUserInfo = ImUserInfoHelper.getCurrentUserInfo()
        mCurrentState = if (type) {
            mUserInfo?.let {
                tvUserName?.text = "${it.name}共收到"
            }
            title_bar.setTitle("收到的红包")
            RECEIVE_RED_PACKAGE_RECORD
        } else {
            mUserInfo?.let {
                tvUserName?.text = "${it.name}共发出"
            }
            title_bar.setTitle("发出的红包")
            SEND_RED_PACKAGE_RECORD
        }
        mRvList.resetNoMoreData()
        mRvList.resetPage()
        showLoadingView()
        mRvList.refresh(false)
    }

    //加载头部成功
    override fun getRedPackageRecordHeaderSuccess(resp: RedPackageRecordHeader) {
        handlerRedPackageRecordHeader(resp)
        onLoadRecordList(1)
    }

    private fun handlerRedPackageRecordHeader(resp: RedPackageRecordHeader) {
        tvPrice?.withNumber(RoundUtils.roundForHalf(resp?.total_amount).toFloat())?.start()
        if (mCurrentState == RECEIVE_RED_PACKAGE_RECORD) {
            tvLeftNum?.visibility = View.VISIBLE
            tvRightNum?.visibility = View.VISIBLE
            tvRightTxt?.visibility = View.VISIBLE
            tvRedPackType?.visibility = View.VISIBLE
            tvSendCount?.visibility = View.GONE
            tvLeftNum?.text = "${resp?.receive_count}"
            tvRightNum?.text = "${resp?.luckiest_count}"
        } else {
            tvRightTxt?.visibility = View.GONE
            tvRedPackType?.visibility = View.GONE
            tvLeftNum?.visibility = View.GONE
            tvRightNum?.visibility = View.GONE
            tvSendCount?.visibility = View.VISIBLE
            tvSendCount?.text = "发出红包${resp?.send_count}个"
        }
    }

    //红包列表
    override fun getRedPackageRecordListSuccess(resp: RedPackageRecordList) {
        dismissLoadingView()
        mRvList.stopRefresh()
        mRvList.hideLoadingView()
        if (resp.records == null) {
            return
        }
        if (mAdapter.mData?.isEmpty() && resp?.records?.isEmpty()) {
            mRvList.showEmptyView()
            return
        }
        if (resp?.records?.size < RedPackageConstant.DEFAULT_PAGE_SIZE) {
            mRvList.setNoMoreData(true)
        }
        mAdapter.mData.addAll(resp.records)
        mAdapter.notifyDataSetChanged()
    }

    //获取红包记录失败（收到的/发出的）
    override fun getRedPackageRecordListFail(msg: String) {
        dismissLoadingView()
        mRvList.stopRefresh()
        mRvList.currentPage = mRvList.currentPage - 1
        if (mAdapter.mData?.isEmpty()) {
            mRvList.showErrorView(!NetworkUtils.isNetworkAvailable())
            return
        }
    }

    override fun getRedPackageRecordHeaderFail(msg: String) {
        dismissLoadingView()
        mRvList.showErrorView(!NetworkUtils.isNetworkAvailable())
    }

}