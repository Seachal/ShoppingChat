package com.laka.shoppingchat.mvp.shop.view.activity


import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.jzvd.Jzvd
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams
import com.alibaba.baichuan.trade.biz.login.AlibcLogin
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.laka.androidlib.base.activity.BaseMvpActivity
import com.laka.androidlib.features.login.OnRequestListener
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.*
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.androidlib.widget.dialog.TaoBaoAuthorDialog
import com.laka.androidlib.widget.refresh.OnResultListener
import com.laka.androidlib.widget.refresh.RefreshRecycleView
import com.laka.shoppingchat.BuildConfig
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.widget.SpacesStaggeredDecoration
import com.laka.shoppingchat.common.widget.refresh.FrogRefreshRecyclerView
import com.laka.shoppingchat.mvp.advertbanner.adapter.AdvertBannerViewAdapter
import com.laka.shoppingchat.mvp.advertbanner.constant.AdvertBannerConstant
import com.laka.shoppingchat.mvp.advertbanner.model.bean.AdvertBannerBean
import com.laka.shoppingchat.mvp.advertbanner.presenter.AdvertBannerPresenter
import com.laka.shoppingchat.mvp.login.LoginModuleNavigator
import com.laka.shoppingchat.mvp.login.constant.LoginConstant
import com.laka.shoppingchat.mvp.login.model.bean.UserInfoBean
import com.laka.shoppingchat.mvp.shop.ShopDetailModuleNavigator
import com.laka.shoppingchat.mvp.shop.constant.ShopDetailConstant
import com.laka.shoppingchat.mvp.shop.contract.IShopDetailContract
import com.laka.shoppingchat.mvp.shop.model.bean.*
import com.laka.shoppingchat.mvp.shop.presenter.ShopDetailPresenter
import com.laka.shoppingchat.mvp.shop.utils.AliPageUtils
import com.laka.shoppingchat.mvp.shop.utils.BigDecimalUtils
import com.laka.shoppingchat.mvp.shop.view.adapter.ShopDetailListAdapter
import com.laka.shoppingchat.mvp.shop.weight.ProductDetailSwitchTitleTabView
import com.laka.shoppingchat.mvp.shopping.center.model.bean.ProductWithCoupon
import com.laka.shoppingchat.mvp.user.utils.UserUtils


/**
 * @Author:summer
 * @Date:2018/12/20
 * @Description:商品详情页面
 */
class ShopDetailActivity : BaseMvpActivity<CustomProductDetail>(), OnRequestListener<OnResultListener>,
    IShopDetailContract.IShopDetailView, View.OnClickListener {

    companion object {
        //淘宝授权类型，1：普通授权，2：绑定渠道ID授权，授权成功后，继续进行渠道ID的绑定
        const val TYPE_TAOBAO_AUTHOR_COMMON = 1
        const val TYPE_TAOBAO_AUTHOR_COUPON = 2
    }

    private lateinit var mRvList: FrogRefreshRecyclerView
    private lateinit var mFlBack: FrameLayout
    private lateinit var mIvBack: ImageView
    private lateinit var mIvToTop: ImageView
    private lateinit var mLlBottom: LinearLayout
    private lateinit var mTvReceiveCoupon: TextView
    private lateinit var mTvCouponTxt: TextView
    private lateinit var mClRootView: ConstraintLayout
    private lateinit var mLlCoupon: LinearLayout
    private lateinit var mTabTypeView: ProductDetailSwitchTitleTabView
    private lateinit var mAllProductDetail: CustomProductDetail
    private lateinit var mProductDetailVideo: ProductDetailVideos
    private lateinit var mBannerImageList: ProductBannerList
    private lateinit var mProductMore: TitleTypeBean
    private lateinit var mRecommendTitle: TitleTypeBean
    private lateinit var mShopDetailFirstListBean: ShopDetailListBean
    private lateinit var mShopAdapter: ShopDetailListAdapter
    private lateinit var mSellerDetail: SellerBean
    private var mAdvertBannerData: AdvertBannerListBean = AdvertBannerListBean()
    private var mRecommendDataList: ArrayList<ProductWithCoupon> = ArrayList()
    private var mResultListener: OnResultListener? = null
    private var mProductId: String = ""
    private var mEntrance: Int = -1
    private val mDataList: ArrayList<MultiItemEntity> = ArrayList()
    private var mImageDetailList: ArrayList<ImageDetail> = ArrayList()
    //“滑动到顶部”按钮控制参数
    private var mTotalDy = 0F
    private val mListScrollControlDis = ScreenUtils.dp2px(360F)
    private var mHostMap: HashMap<String, String> = HashMap()
    private lateinit var mShopPresenter: ShopDetailPresenter
    private lateinit var mBannerPresenter: AdvertBannerPresenter
    private lateinit var mCouponInfo: CouponInfo

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            super.attachBaseContext(newBase)
        } else {
            super.attachBaseContext(object : ContextWrapper(newBase) {
                override fun getSystemService(name: String?): Any {
                    if (Context.AUDIO_SERVICE == name) {
                        //创建AudioService的时候，使用全局的上下文
                        return ApplicationUtils.getApplication().getSystemService(name)
                    }
                    return super.getSystemService(name)
                }
            })
        }
    }

    private var isActive: Boolean = false

    /**activity任务栈中调用，用来清除前面打开的activvity*/
    fun finish(active: Boolean) {
        isActive = active
        finish()
    }

    override fun finish() {
        if (!isActive) {
            //移除最后一个activity
            ShopDetailModuleNavigator.removeElementForActivityStack()
        } else {
            //移除第一个
            isActive = false
            ShopDetailModuleNavigator.removeElementForActivityStack(0)
        }
        super.finish()
    }

    override fun onStart() {
        super.onStart()
        mShopAdapter.onStart()
    }

    override fun onPause() {
        mShopAdapter.onPause()
        super.onPause()
        Jzvd.releaseAllVideos()
    }

    override fun onBackPressed() {
        Jzvd.backPress()
        super.onBackPressed()
    }

    override fun onDestroy() {
        mShopAdapter.release()
        super.onDestroy()
    }

    override fun showData(data: CustomProductDetail) {
        dismissLoading()
    }

    override fun showErrorMsg(msg: String?) {
        dismissLoading()
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setTranslucentForImageView(this)
        } else {
            super.setStatusBarColor(color)
        }
    }

    override fun createPresenter(): IBasePresenter<*> {
        mBannerPresenter = AdvertBannerPresenter()
        mBannerPresenter.setView(advertBannerView)
        mShopPresenter = ShopDetailPresenter()
        return mShopPresenter
    }

    override fun setContentView(): Int {
        return R.layout.activity_main_shop
    }

    override fun initIntent() {
        intent?.extras?.let {
            mEntrance = it.getInt(ShopDetailConstant.ENTRANCE, 1)
            mProductId = it.getString(ShopDetailConstant.PRODUCT_ID)
        }
    }

    override fun initViews() {
        //添加到任务管理栈中
        ShopDetailModuleNavigator.addElementForActivityStack(this)
        //禁用侧滑，防止Activity出现全透明状态
        //swipeBackLayout.setEnableGesture(false)
        Jzvd.SAVE_PROGRESS = false//视频播放器设置不保存播放进度
        mRvList = findViewById(R.id.rv_list)
        mFlBack = findViewById(R.id.fl_back)
        mIvBack = findViewById(R.id.iv_back)
        mIvToTop = findViewById(R.id.iv_to_top)
        mLlBottom = findViewById(R.id.ll_bottom)
        mLlCoupon = findViewById(R.id.ll_coupon)
        mTvReceiveCoupon = findViewById(R.id.tv_coupon_value)
        mTvCouponTxt = findViewById(R.id.tv_coupon)
        mClRootView = findViewById(R.id.cl_root_view)
        mTabTypeView = findViewById(R.id.tab_type_view)
        mShopAdapter = ShopDetailListAdapter(mDataList)
        mRvList.addItemDecoration(SpacesStaggeredDecoration(object : SpacesStaggeredDecoration.ItemDecorationCallBack {
            override fun getItemType(position: Int?): Int? {
                return mShopAdapter.getItemViewType(position!!)
            }
        }, true, ResourceUtils.getDimen(R.dimen.dp_10), ResourceUtils.getDimen(R.dimen.dp_6)))
        mRvList.setOnRequestListener(this)
        mRvList.onItemClickListener = mOnItemClickListener
        mRvList.enableRefresh(false)
        mRvList.enableLoadMore(false)
        mRvList.setLayoutManager(StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL))
        mRvList.adapter = mShopAdapter
        mRvList.refresh(false) //加载详情
        //设置statusBar高度
        mTabTypeView.setStatusBarHeight(StatusBarUtil.getStatusBarHeight(this))
    }

    private lateinit var mSkeleton: ViewSkeletonScreen

    override fun initData() {

    }

    override fun initEvent() {
        mFlBack.setOnClickListener(this)
        mIvToTop.setOnClickListener(this)
        mLlCoupon.setOnClickListener(this)
        //显示骨架框
        mSkeleton = Skeleton.bind(mClRootView)
            .load(R.layout.skeleton_activity_prodect_detail)
            .duration(1000)
            .color(R.color.shimmer_color)
            .angle(0)
            .show()
        mRvList.onScrollListener =
            RefreshRecycleView.OnScrollListener { recyclerView, dx, dy, _, lastVisibleItemViewType ->
                mTotalDy += dy
                // 纯粹累加 dy，最后得出的总和可能为负数，这样是不合理的，所以当 mTotalDy<0 时，将其置为 0
                mTotalDy = if (mTotalDy < 0) 0f else mTotalDy
                if (mTotalDy >= mListScrollControlDis) {
                    mIvToTop.visibility = View.VISIBLE
                } else {
                    mIvToTop.visibility = View.GONE
                }
                //使用当前可显示区域内最后一个显示 item 的 viewType
                mTabTypeView.onScroll(recyclerView, dx, dy, lastVisibleItemViewType) {
                    if (it >= 0.5) {
                        StatusBarUtil.setLightMode(this)
                    } else {
                        StatusBarUtil.setDarkMode(this)
                    }
                }
            }
        mTabTypeView.bindRecyclerView(mRvList)
    }

    override fun onRequest(page: Int, resultListener: OnResultListener): String {
        mResultListener = resultListener
        mShopPresenter.onLoadProductDetail(mProductId)
        return ""
    }

    /**获取产品详情成功*/
    private fun updateProductDetail() {
        mProductDetailVideo = mAllProductDetail.productVideo
        //保存商店详情
        mSellerDetail = mAllProductDetail.seller
        mShopDetailFirstListBean = ShopDetailListBean()
        mShopDetailFirstListBean.setDataList(mDataList)
        if (mAllProductDetail.small_images != null && mAllProductDetail.small_images.imageList.size > 0) {
            val imageList = ArrayList<ProductBannerBean>()
            mAllProductDetail.small_images?.imageList?.forEach {
                imageList.add(ProductBannerBean(picture = it))
            }
            mBannerImageList = ProductBannerList(imageList, ShopDetailConstant.SHOP_DETAIL_BANNER)
            //加上第一张商品图片作为第一张banner图片
            mBannerImageList.imageList.add(0, ProductBannerBean(picture = mAllProductDetail?.pict_url))
        } else {
            mBannerImageList = ProductBannerList(arrayListOf(), ShopDetailConstant.SHOP_DETAIL_BANNER)
            mBannerImageList.imageList.add(0, ProductBannerBean(picture = mAllProductDetail?.pict_url))
        }
        if (!TextUtils.isEmpty(mAllProductDetail?.productVideo?.videoId)) {
            mBannerImageList.imageList.add(
                0,
                ProductBannerBean(
                    mAllProductDetail?.productVideo?.videoId,
                    mAllProductDetail?.productVideo?.videoUrl,
                    mAllProductDetail?.productVideo?.videoThumbnailURL
                )
            )
        }
        mAllProductDetail.uiType = ShopDetailConstant.SHOP_DETAIL_BASIC
        mProductMore =
            TitleTypeBean(ShopDetailConstant.SHOP_DETAIL_MORE, getString(R.string.shop_detail_more_item_title))
        mImageDetailList = mAllProductDetail.image_detail
        mImageDetailList.forEach { it.uiType = ShopDetailConstant.SHOP_DETAIL_IMAGE_DETAIL }
        mRecommendTitle =
            TitleTypeBean(ShopDetailConstant.SHOP_DETAIL_RECOMMEND_TITLE, getString(R.string.shop_recommend_title))
        mTvReceiveCoupon.text = "预省¥${BigDecimalUtils.add(mAllProductDetail?.coupon_money, mAllProductDetail.fanli)}"
        mTvReceiveCoupon.visibility = View.VISIBLE
        mTvCouponTxt.text = "领券购买"
        setListData()
    }

    private fun setListData() {
        mDataList.clear()
        //隐藏骨架框
        if (::mSkeleton.isInitialized) {
            mSkeleton.hide()
        }
        if (::mBannerImageList.isInitialized) {
            mDataList.add(mBannerImageList)
        }
        if (::mAllProductDetail.isInitialized) {
            mDataList.add(mAllProductDetail)
        }
        if (mAdvertBannerData?.data != null
            && mAdvertBannerData?.data?.size > 0
        ) {
            mDataList.add(mAdvertBannerData)
        }
        if (::mSellerDetail.isInitialized) {
            mDataList.add(mSellerDetail)
        }
        if (::mProductMore.isInitialized) {
            mDataList.add(mProductMore)
        }
        if (mImageDetailList?.size > 0) {
            mProductMore.open = 1
            mDataList.addAll(mImageDetailList)
        }
        if (mRecommendDataList?.size > 0) {
            mDataList.add(mRecommendTitle)
            mDataList.addAll(mRecommendDataList)
        }
        // 刷新数据后，隐藏加载控件和加载错误控件，
        // 防止遮挡到RecyclerView
        mRvList.hideLoadingView()
        mRvList.hideErrorView()
        mShopAdapter.replaceData(mDataList)
        mRvList.notifyDataSetChanged()
    }

    /**加载列表数据失败，显示相应加载错误或者网络链接失败的UI*/
    override fun onLoadListDataFail() {
        if (mShopAdapter.data == null || mShopAdapter.data.size == 0) {
            mResultListener?.onFailure(-1, "")
        }
    }

    override fun onInternetChange(isLostInternet: Boolean) {
        if (!isLostInternet) { //切换为有网络状态，刷新数据
            LogUtils.info("网络状态切换：$isLostInternet")
            mShopPresenter.onLoadProductDetail(mProductId)
        }
    }

    private val mOnItemClickListener = object : RefreshRecycleView.OnItemClickListener<MultiItemEntity> {
        override fun onItemClick(data: MultiItemEntity?, position: Int) {
            onItemClick(position)
        }

        override fun onChildClick(id: Int, data: MultiItemEntity?, position: Int) {

        }
    }

    private fun onItemClick(position: Int) {
        when (mShopAdapter.getItemViewType(position)) {
            ShopDetailConstant.SHOP_DETAIL_RECOMMEND_ITEM -> {
                val entity = mShopAdapter.getItem(position) as? ProductWithCoupon
                entity?.let {
                    ShopDetailModuleNavigator.startShopDetailActivity(this, "${entity.productId}")
                }
            }
            ShopDetailConstant.SHOP_DETAIL_MORE -> {
                //如果没有商品详情图片，则点击“查看商品详情”item，走领券的流程
                if (mImageDetailList.isEmpty()) {
                    onReceiveCoupons()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                finish()//后退
            }
            R.id.ll_coupon -> { // 领券
                onReceiveCoupons()
            }
            R.id.iv_to_top -> { // 滑动到顶部
                mRvList.scrollToTop()
            }
        }
    }

    private fun onReceiveCoupons() {
        if (!::mAllProductDetail.isInitialized) {
            mShopPresenter.onLoadProductDetail(mProductId)
            return
        }
        if (!isTaoBaoAuthor()) return

        if (MultiClickUtil.checkClickValid(R.id.ll_coupon)) {
            if (PackageUtils.isAppInstalled(this, PackageUtils.TAO_BAO)) {
                if (!::mCouponInfo.isInitialized) {
                    //获取领券地址的连接
                    mShopPresenter.onGetCouponInfo(mProductId)
                } else {
                    jumpToCoupons("${mCouponInfo.item_info.coupon_click_url}", false)
                }
            } else {
                ToastHelper.showToast("未检测到手机淘宝！")
            }
        } else {
            if (BuildConfig.DEBUG) {
                ToastHelper.showToast("您点太快了哟！")
            }
        }
    }

    //打开淘宝领券页面
    private fun jumpToCoupons(couponUrl: String, isH5: Boolean) {
        val userInfoBean = SPHelper.getObject(
            LoginConstant.USER_INFO_FILENAME,
            LoginConstant.USER_LOGIN_INFO,
            UserInfoBean::class.java
        )
        val tbkParams = AlibcTaokeParams("", "", "")
        tbkParams.setPid("${userInfoBean?.userBean?.adzone?.adzonepid}")
        tbkParams.setAdzoneid("${userInfoBean?.userBean?.adzone?.adzone_id}")
        if (isH5) {
            AliPageUtils.openAliPageForAuto(this, couponUrl, tbkParams)
        } else {
            AliPageUtils.openAliPage(this, couponUrl, tbkParams)
        }
    }

    //======================================= login ===============================================
    fun isLogin(): Boolean {
        if (!UserUtils.isLogin()) {
            LoginModuleNavigator.startLoginActivity(this)
            return false
        }
        return true
    }

    //是否已淘宝授权
    private fun isTaoBaoAuthor(): Boolean {
        if (!AlibcLogin.getInstance().isLogin) {
            val authorConfirm = TaoBaoAuthorDialog(this)
            authorConfirm.setOnSureClickListener {
                mShopPresenter.onTaoBaoAuthor()
            }
            authorConfirm.show()
            return false
        }
        return true
    }

    //========================================= V层接口实现 =======================================

    /**获取产品基本详情、banner图等信息*/
    override fun onLoadProductDetailSuccess(result: CustomProductDetail) {
        if (!::mAllProductDetail.isInitialized) {
            mAllProductDetail = result
        }
        mShopPresenter.onGetProductDetailH5ServiceUrl(mProductId)
    }

    /**获取产品详情失败，并已显示失败页面*/
    override fun onLoadProductDetailFail() {

    }

    override fun onLoadRecommendDataSuccess(list: ArrayList<ProductWithCoupon>) {
        mRecommendDataList.clear()
        list.forEach {
            it.uiType = ShopDetailConstant.SHOP_DETAIL_RECOMMEND_ITEM
        }
        mRecommendDataList.addAll(list)
        setListData()
    }

    override fun onTaoBaoAuthorSuccess() {

    }

    /**获取 h5 服务地址成功*/
    override fun onGetProductDetailH5ServiceUrlSuccess(resultMap: HashMap<String, String>) {
        mHostMap = resultMap
        val resultUrl = resultMap["url"]
        mShopPresenter.onGetProductDetailForH5Url("$resultUrl")
    }

    /**获取H5 url 失败，已进行失败UI处理*/
    override fun onGetProductDetailH5ServiceUrlFail(msg: String) {

    }

    /**
     * 请求H5 url 获取详情成功，则result 中含有商家、视频、banner图等数据
     * 先请求我们自己服务器的商品详情接口，然后再去请求h5 链接的商品详情
     * 统一保存在 mAllProductDetail 中
     * */
    override fun onGetProductDetailForH5UrlSuccess(result: CustomProductDetail) {
        LogUtils.info("detail-------${result.small_images}")
        if (::mAllProductDetail.isInitialized) {
            mAllProductDetail.seller = result.seller
            mAllProductDetail.productVideo = result.productVideo
            mAllProductDetail.freeShipping = result.freeShipping
        } else {
            mAllProductDetail = result
        }
        updateProductDetail()
        if (!TextUtils.isEmpty(result.productImageDetailUrl)) {
            mShopPresenter.onGetProductDetailImageList(result.productImageDetailUrl)
        } else {
            mShopPresenter.onGetProductDetailImageList2("${mHostMap["get_desc"]}")
        }
        //获取广告banner
        mBannerPresenter.onLoadAdvertBannerData(AdvertBannerConstant.TYPE_BANNER_CLASSID_PRODUCT_DETAIL)
    }

    /**通过h5 url获取详情失败，已进行失败UI处理*/
    override fun onGetProductDetailForH5UrlFail(msg: String) {

    }

    /**获取产品详情图片成功*/
    override fun onGetProcutDetailImageListSuccess(imageDetailList: ArrayList<ImageDetail>) {
        mAllProductDetail.image_detail = imageDetailList
        mImageDetailList = mAllProductDetail.image_detail
        mImageDetailList.forEach { it.uiType = ShopDetailConstant.SHOP_DETAIL_IMAGE_DETAIL }
        setListData()
        onLoadRecommendData()
    }

    override fun onGetProcutDetailImageListFail(msg: String) {
        onLoadRecommendData()
    }

    /**相关推荐*/
    private fun onLoadRecommendData() {
        if (::mAllProductDetail.isInitialized) {
            mShopPresenter.onLoadRecommendData(this, mProductId)
        }
    }

    /**广告banner*/
    private val advertBannerView = object : AdvertBannerViewAdapter() {
        override fun onLoadAdvertBannerDataSuccess(bannerList: ArrayList<AdvertBannerBean>) {
            mAdvertBannerData.data = bannerList
            setListData()
        }

        override fun onLoadAdvertBannerDataFail(msg: String) {

        }
    }

    override fun onCouponInfoSuccess(result: CouponInfo) {
        mCouponInfo = result
        //获取成功，则进入淘宝领券
        jumpToCoupons("${mCouponInfo.item_info.coupon_click_url}", false)
    }

}