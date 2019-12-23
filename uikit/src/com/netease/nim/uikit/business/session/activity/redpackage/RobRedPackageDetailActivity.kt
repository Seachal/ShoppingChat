package com.netease.nim.uikit.business.session.activity.redpackage

import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.imageload.GlideLoader
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.widget.refresh.NimRefreshLayout
import com.netease.nim.uikit.R
import com.netease.nim.uikit.business.ChatRouteManager
import com.netease.nim.uikit.business.session.adapter.RobRedPackageListAdapter
import com.netease.nim.uikit.business.session.attachment.RobRedPackageAttachment
import com.netease.nim.uikit.business.session.constant.RedPackageConstant
import com.netease.nim.uikit.business.session.constract.RedPackageConstract
import com.netease.nim.uikit.business.session.model.bean.*
import com.netease.nim.uikit.business.session.presenter.RedPackagePresenter
import com.netease.nim.uikit.business.session.utils.RoundUtils
import com.netease.nim.uikit.business.uinfo.UserInfoHelper
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.impl.NimUIKitImpl
import org.json.JSONObject

/**
 * @Author:summer
 * @Date:2019/9/7
 * @Description:抢红包详情红包页面（领取成功或者领取失败）
 */
class RobRedPackageDetailActivity : BaseMvpActivity<RedPackageResponse>(),
    RedPackageConstract.IRedPackageView {

    private lateinit var mPresenter: RedPackagePresenter
    private var mIvBack: ImageView? = null
    private var mIvPortrait: HeadImageView? = null
    private var mTvName: TextView? = null
    private var mTvTitle: TextView? = null
    private var mTvAmount: TextView? = null
    private var mTvWallet: TextView? = null
    private var mTvYuan: TextView? = null
    private var mTvRedPackageRecord: TextView? = null
    private var mIvRedpackageType: ImageView? = null
    private var mClAmount: ConstraintLayout? = null
    private var mHeadView: View? = null
    private var mViewDivder: View? = null
    private var mViewLine: View? = null
    private var mTvRedPackageExplain: TextView? = null
    private var mRvList: RecyclerView? = null
    private var mRefreshLayout: NimRefreshLayout? = null
    private lateinit var mAdapter: RobRedPackageListAdapter
    private var mDataList = ArrayList<RedPackageDetailListBean>()

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(
                this,
                ContextCompat.getColor(this, R.color.color_f35543),
                0
            )
            StatusBarUtil.setLightModeNotFullScreen(this, false)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun setContentView(): Int {
        return R.layout.activity_recent_redpackage //群红包领取页面
    }

    private var mDetailHeader: RedPackageDetailHeader? = null
    private var mAttachment: RobRedPackageAttachment? = null

    override fun initIntent() {
        intent?.extras?.let {
            mDetailHeader = it.getSerializable("detail_header") as? RedPackageDetailHeader
            mAttachment = it.getSerializable("attachment") as? RobRedPackageAttachment
        }
    }

    override fun initViews() {
        mRefreshLayout = findViewById(R.id.refresh_layout)
        mViewDivder = findViewById(R.id.view_divder)
        mViewLine = findViewById(R.id.view_line)
        mTvRedPackageExplain = findViewById(R.id.tv_redpackage_explain)
        mRvList = findViewById(R.id.rv_receive_user_list)
        mIvBack = findViewById(R.id.iv_back)
        mIvPortrait = findViewById(R.id.iv_portrait)
        mTvName = findViewById(R.id.tv_name)
        mIvRedpackageType = findViewById(R.id.iv_redpackage_type)

        mHeadView = findViewById(R.id.layout_head)
        mTvTitle = findViewById(R.id.tv_redpackage_title)
        mTvAmount = findViewById(R.id.tv_redpackage_amount)
        mTvWallet = findViewById(R.id.tv_wallet)
        mClAmount = findViewById(R.id.cl_amount)
        mTvYuan = findViewById(R.id.tv_yuan_txt)
        mTvRedPackageRecord = findViewById(R.id.tv_redpackage_record)

        mRefreshLayout?.setEnableLoadMore(true)
        mRefreshLayout?.setEnableRefresh(false)
        mRefreshLayout?.setOnLoadMoreListener {
            initData()
        }
        mRvList?.isNestedScrollingEnabled = false
        mRvList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //头部数据处理
        handleRedPackageDetail()
    }

    override fun initData() {
        val json = JSONObject()
        json.put("hongbao_no", mAttachment?.getmHongBaoNo())
        json.put("user_id", mAttachment?.getmUserId())
        json.put("to_id", mAttachment?.getmToId())
        json.put("op_user", NimUIKitImpl.getAccount())
        json.put("ope", mAttachment?.ope)
        json.put("page", mRefreshLayout?.loadCurrentPage())
        json.put("size", RedPackageConstant.DEFAULT_PAGE_SIZE)
        mPresenter.onLoadRedPackageDetail(json)
    }

    override fun initEvent() {
        mTvWallet?.setOnClickListener {
            ChatRouteManager.get(ChatRouteManager.ROUTE_WALLET_ACTIVITY)?.onJump(this, HashMap())
        }
        mTvRedPackageRecord?.setOnClickListener {
            ChatRouteManager.get(ChatRouteManager.ROUTE_REDPACKAGE_RECORD_ACTIVITY)
                ?.onJump(this, HashMap())
        }
        mIvBack?.setOnClickListener {
            finish()
        }
    }

    override fun showData(data: RedPackageResponse) {

    }

    override fun showErrorMsg(msg: String?) {

    }

    override fun createPresenter(): IBasePresenter<*> {
        mPresenter = RedPackagePresenter()
        return mPresenter
    }

    private fun handleRedPackageDetail() {
        //头部背景色显示
        if (mDetailHeader?.recvRecord != null) {
            mClAmount?.visibility = View.VISIBLE
            mTvWallet?.visibility = View.VISIBLE
            mHeadView?.setBackgroundResource(R.drawable.hb_bg2)
        } else {
            mClAmount?.visibility = View.GONE
            mTvWallet?.visibility = View.GONE
            mHeadView?.setBackgroundResource(R.drawable.hb_bg3)
        }

        if (mDetailHeader != null) {
            mIvPortrait?.loadAvatar(UserInfoHelper.getUserHeadImage(mAttachment?.getmUserId()))
            mTvName?.text = UserInfoHelper.getUserName(mAttachment?.getmUserId()) + "的红包"
            mTvAmount?.text =
                RoundUtils.roundForHalf("${mDetailHeader?.recvRecord?.amount}")
            mTvTitle?.text = "${mDetailHeader?.redPackage?.title}"

            //详情底部列表显示隐藏判断逻辑
            if (mDetailHeader?.redPackage?.hongBaoType == 1) {
                //普通红包，发红包者不可领取，view_divder 不显示，领取红包的人只能看到自己的红包数据，下方列表不显示
                //发红包的用户则可以看到红包领取详情列表
                mViewDivder?.visibility = View.GONE
                mIvRedpackageType?.visibility = View.GONE
                if (NimUIKitImpl.getAccount() == mAttachment?.getmUserId()) {
                    //当前用户是发红包者，则显示领取红包记录列表
                    mRvList?.visibility = View.VISIBLE
                    mViewLine?.visibility = View.VISIBLE
                    mTvRedPackageExplain?.visibility = View.VISIBLE
                    val layoutParams =
                        mTvRedPackageExplain?.layoutParams as? LinearLayout.LayoutParams
                    layoutParams?.topMargin = ScreenUtils.dp2px(30f)
                    mTvRedPackageExplain?.text = mDetailHeader?.showMsg
                } else {
                    //当前用户是领红包者（私发红包的领红包者），列表不显示
                    mRvList?.visibility = View.GONE
                    mViewLine?.visibility = View.GONE
                    mTvRedPackageExplain?.visibility = View.GONE
                }
            } else { //拼手气红包
                mRvList?.visibility = View.VISIBLE
                mViewLine?.visibility = View.VISIBLE
                mViewDivder?.visibility = View.VISIBLE
                mIvRedpackageType?.visibility = View.VISIBLE
                mTvRedPackageExplain?.visibility = View.VISIBLE
                mTvRedPackageExplain?.text = mDetailHeader?.showMsg
            }
        }
    }

    //====================================== view 层接口 ===========================================

    override fun onLoadRedPackageDetailSuccess(response: RedPackageDetailList) {
        mRefreshLayout?.finishLoadMore()
        if (response.receivers.size < RedPackageConstant.DEFAULT_PAGE_SIZE) {
            mRefreshLayout?.setNoMoreData(true)
        }
        mDataList.addAll(response.receivers)
        if (!::mAdapter.isInitialized) {
            mAdapter = RobRedPackageListAdapter(R.layout.item_redpackage_record_list, mDataList)
            mRvList?.adapter = mAdapter
        } else {
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onLoadRedPackageDetailFail(msg: String) {
        mRefreshLayout?.finishLoadMore()
    }

}