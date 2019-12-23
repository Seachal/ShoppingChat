package com.laka.shoppingchat.mvp.shopping.center.view.fragment

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.LinearLayout
import com.laka.androidlib.base.adapter.SimpleFragmentPagerAdapter
import com.laka.androidlib.eventbus.Event
import com.laka.androidlib.mvp.IBasePresenter
import com.laka.androidlib.util.ApplicationUtils
import com.laka.androidlib.util.ListUtils
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.network.NetworkUtils
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.laka.shoppingchat.common.util.update.UpdateManager
import com.laka.shoppingchat.mvp.main.constant.HomeEventConstant
import com.laka.shoppingchat.mvp.main.view.fragment.HomeFragment
import com.laka.shoppingchat.mvp.shopping.center.constant.ShoppingCenterConstant
import com.laka.shoppingchat.mvp.shopping.center.contract.IShoppingHomeContract
import com.laka.shoppingchat.mvp.shopping.center.helper.ActivitsPopupHelper
import com.laka.shoppingchat.mvp.shopping.center.helper.HomeTabLayoutHelper
import com.laka.shoppingchat.mvp.shopping.center.helper.SelectTabHelper
import com.laka.shoppingchat.mvp.shopping.center.model.bean.*
import com.laka.shoppingchat.mvp.shopping.center.presenter.ShoppingHomePresenter
import com.laka.shoppingchat.mvp.test.view.fragment.TestFragment
import kotlinx.android.synthetic.main.fragment_shopping.*
import net.lucode.hackware.magicindicator.MagicIndicator

/**
 * @Author:Rayman
 * @Date:2018/12/21
 * @Description:商品主页HOME,Fragment
 */
class ShoppingHomeFragment : HomeFragment(), IShoppingHomeContract.IShoppingHomeView,
    View.OnClickListener {

    /**
     * description:Fragment中不建议使用Kotlin-ID注入的方式调用控件 .
     * 因为调用的时机必须要在onViewCreate里面，但是我们Base封装的都在onCreateView的时候执行
     **/
    private lateinit var mClRootView: ConstraintLayout
    private lateinit var mVpContainer: ViewPager
    private lateinit var magicIndicatorCollapsed: MagicIndicator

    /**
     * description:页面Fragment信息配置
     **/
    private var mCategoryList = ArrayList<CategoryBean>()
    private var fragmentList = ArrayList<Fragment>()
    private var mCurrentFragment: ShoppingListFragment? = null
    private var uiMode = ShoppingCenterConstant.LIST_UI_TYPE_COMMON

    /**
     * description:页面数据设置
     **/
    private lateinit var mAdapter: SimpleFragmentPagerAdapter
    private lateinit var titleList: Array<String>
    private lateinit var mShoppingHomePresenter: ShoppingHomePresenter

    /**
     * item helper
     * */
    private lateinit var mSelectTabHelper: SelectTabHelper
    private lateinit var mHomeTabLayoutHelper: HomeTabLayoutHelper
    private lateinit var mActivitsPopupHelper: ActivitsPopupHelper

    /**
     * home page data
     * */
    private lateinit var mHomePageData: HomePageResponse

    override fun setContentView(): Int {
        return R.layout.fragment_shopping
    }

    override fun initArgumentsData(arguments: Bundle?) {

    }

    override fun createPresenter(): IBasePresenter<*> {
        mShoppingHomePresenter = ShoppingHomePresenter()
        return mShoppingHomePresenter
    }

    override fun initView(rootView: View?, savedInstanceState: Bundle?) {
        mClRootView = cl_root_view
        mVpContainer = vp_shopping_list
        magicIndicatorCollapsed = magic_indicator_collapsed

        //todo Tab
        mSelectTabHelper = SelectTabHelper(context!!)
        mSelectTabHelper.bindView(ll_select, ll_tab)
        mSelectTabHelper.setMenuStateListener(object : SelectTabHelper.MenuStateListener {
            override fun menuOpen(position: Int, type: String, orderSort: String) {
                if (position != -1) {
                    mCurrentFragment?.mOrderField = type
                    mCurrentFragment?.mOrderSort = orderSort
                    mCurrentFragment?.refreshList()
                }
            }
        })
        mHomeTabLayoutHelper = HomeTabLayoutHelper()
        handleStatusBarOffset()
    }

    private fun handleStatusBarOffset() {
        val layoutParams = cl_category.layoutParams as? LinearLayout.LayoutParams
        layoutParams?.let {
            val statusBarHeight = StatusBarUtil.getStatusBarHeight(activity)
            it.height = ScreenUtils.dp2px(44f) + statusBarHeight
        }
    }


    override fun initDataLazy() {
        mShoppingHomePresenter.getHomePageDataFirst()
        mShoppingHomePresenter.getAdvert(context!!)
        //检查更新
        //checkUpdate()
    }

    private fun checkUpdate() {
        ApplicationUtils.setIsShowUpdateDialog(true)
        val updateManager = UpdateManager(activity!!)
        updateManager.checkUpdate(false)
    }

    override fun initEvent() {
        setClickView<View>(R.id.layout_no_network)
        setClickView<View>(R.id.layout_no_data)
        setClickView<View>(R.id.btn_no_data)
        setClickView<View>(R.id.btn_no_net_work)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_no_data,
            R.id.btn_no_net_work,
            R.id.layout_no_data,
            R.id.layout_no_network -> {
                onReload()
            }
        }
    }

    override fun onEvent(event: Event?) {
        super.onEvent(event)
        when (event?.name) {
            HomeEventConstant.EVENT_ON_NETWORK_ERROR -> {
                showNetWorkErrorView()
            }
            HomeEventConstant.EVENT_ON_NETWORK_RESUME -> {
                // 判断ParentType列表是否为空，为空则重新获取数据
                if (ListUtils.isEmpty(mCategoryList)) {
                    onReload()
                }
            }
        }
    }

    private fun onReload() {
        mShoppingHomePresenter.refreshHomePageData()
    }

    override fun showData(data: ArrayList<ProductParentType>) {

    }

    override fun showErrorMsg(msg: String?) {
        ToastHelper.showToast(msg)
    }

    override fun showNetWorkErrorView() {
        if (!ListUtils.isEmpty(mCategoryList)) return
        if (NetworkUtils.isNetworkAvailable()) {
            // 获取数据失败，显示错误的View
            layout_no_network.visibility = View.GONE
            layout_no_data.visibility = View.VISIBLE
        } else {
            layout_no_network.visibility = View.VISIBLE
            layout_no_data.visibility = View.GONE
        }
    }

    private fun initViewPager(data: ArrayList<CategoryBean>) {
        fragmentList.clear()
        mCategoryList.clear()
        mCategoryList.addAll(data)
        // 初始化数据
        titleList = Array(data.size) { String() }
        for ((index, category) in data.withIndex()) {
            fragmentList.add(ShoppingListFragment.newInstance(category))
//            fragmentList.add(TestFragment.getInstance())
            titleList[index] = category.title
        }
        mHomeTabLayoutHelper
            .bindCommonNavigator(
                context!!, titleList.toMutableList(), magicIndicatorCollapsed, mVpContainer,
                R.color.white, R.color.white, R.color.white
            )

        if (!::mAdapter.isInitialized) {
            mAdapter = SimpleFragmentPagerAdapter(fragmentManager, fragmentList, titleList.asList())
            mVpContainer?.adapter = mAdapter
            mVpContainer.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    if (position >= 0 && position < fragmentList.size) {
                        mCurrentFragment = fragmentList[position] as? ShoppingListFragment
                        mSelectTabHelper.setCurrentFragment(mCurrentFragment)
                    }
                }
            })
        } else {
            mAdapter.setFragments(fragmentList, titleList.asList())
            mVpContainer.setCurrentItem(0, false)
        }
        if (fragmentList.isNotEmpty()) {
            mCurrentFragment = fragmentList[0] as? ShoppingListFragment
            mSelectTabHelper.setCurrentFragment(mCurrentFragment)
        }
    }


    //=======================================  View 层接口实现  ====================================
    override fun onGetHomePageDataSuccess(response: HomePageResponse) {
        if (ApplicationUtils.isVaildActivity(activity)) {
            if (ListUtils.isNotEmpty(response.categoryList)) {
                layout_no_network.visibility = View.GONE
                layout_no_data.visibility = View.GONE
                mHomePageData = response
                handleCategory(response)
            } else {
                layout_no_network.visibility = View.GONE
                layout_no_data.visibility = View.VISIBLE
            }
        }
    }

    private fun handleCategory(response: HomePageResponse) {
        val data = response.categoryList
        if (data.size != mCategoryList.size) {
            initViewPager(data)
        } else {
            for (i in 0 until data.size) {
                if (data[i] != mCategoryList[i]) {
                    initViewPager(data)
                    break
                }
            }
        }
    }

    override fun onGetHomePageDataFail() {
        showNetWorkErrorView()
    }

    /**
     * 刷新 currentFragment
     * */
    override fun onRefreshFragmentData() {
        mCurrentFragment?.onRefresh()
    }

    override fun onGetH5UrlSuccess(response: HomeUrlBean) {

    }

    //获取弹窗数据成功
    override fun onLoadPopupDataSuccess(popupBean: HomePopupBean?) {
        if (!::mActivitsPopupHelper.isInitialized) {
            mActivitsPopupHelper = ActivitsPopupHelper()
        }
        mActivitsPopupHelper.downloadShopDetailImage(activity!!, popupBean)
    }


}