package com.laka.shoppingchat.mvp.shop.weight

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.jzvd.JZDataSource
import cn.jzvd.JzvdStd
import com.laka.androidlib.util.LogUtils
import com.laka.androidlib.util.imageload.GlideLoader
import com.laka.androidlib.util.screen.ScreenUtils
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.shop.model.bean.ProductBannerBean
import kotlinx.android.synthetic.main.banner_shop_detail.view.*

/**
 * @Author:summer
 * @Date:2018/12/21
 * @Description:商品详情banner图片
 */
class ShopDetailBannerView : ConstraintLayout {

    private lateinit var mRootView: View
    private var mCurrentPosition: Int = 0
    var mWheelTime: Long = 5000
    private lateinit var mPagerAdapter: BannerPagerAdapter
    var mCurrentProgross: Long = 0L

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        mRootView = LayoutInflater.from(context).inflate(R.layout.banner_shop_detail, this, false)
        addView(mRootView)
        initEvent()
    }

    private fun initEvent() {
        view_pager_banner.addOnPageChangeListener(mPageChangeListener)
    }

    private val mPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            rl_spot.setScrollData(position, positionOffset, positionOffsetPixels)
            LogUtils.info("invitationPlaybillActivity-------：position=$position,,,positionOffset=$positionOffset,,,positionOffsetPixels=$positionOffsetPixels")
        }

        override fun onPageSelected(position: Int) {
            mOnPageChangeListener?.invoke(position)
            text_view_indicator.text = (position % mPagerAdapter.getDataSize() + 1).toString() + "/" + mPagerAdapter.getDataSize().toString()
            if (::mPagerAdapter.isInitialized) {
                LogUtils.info("banner------切换页面")
                onHandleVideoForPageChange(position % (mPagerAdapter.mData.size))
            }
        }
    }

    /**
     * 页面切换时，身为Video 的banner页面的暂停和播放处理
     * @param position 为转换后的，可以直接使用
     * */
    private fun onHandleVideoForPageChange(position: Int) {
        for (key in mVideoMap.keys) {
            val dis = Math.abs(key - position)
            //item 个数大于3，并且当前 position 为最后一个数据，则第一个item如果是视频播放器，暂停
            if (mPagerAdapter.mData.size > 3 && position == mPagerAdapter.mData.size - 1 && key == 0) {
                if (mVideoMap[key] != null && mVideoMap[key]?.currentState == JzvdStd.CURRENT_STATE_PLAYING) {
                    mVideoMap[key]?.startButton?.performClick()
                }
                continue
            }
            //item 个数大于3，并且当前 position 为第一个数据，则最后一个item 如果是视频播放，暂停
            if (mPagerAdapter.mData.size > 3 && position == 0 && key == mPagerAdapter.mData.size - 1) {
                if (mVideoMap[key] != null && mVideoMap[key]?.currentState == JzvdStd.CURRENT_STATE_PLAYING) {
                    mVideoMap[key]?.startButton?.performClick()
                }
                continue
            }
            //不跟当前position相邻，则从map中移除
            if (dis >= 2) {
                mVideoMap.remove(dis)
                continue
            }
            //相邻的item，如果是视频banner，则暂停
            if (key != position && dis == 1) {
                if (mVideoMap[key] != null && mVideoMap[key]?.currentState == JzvdStd.CURRENT_STATE_PLAYING) {
                    mVideoMap[key]?.startButton?.performClick() //暂停
                }
                continue
            }
            //当前position，如果是视频item，则调用播放
            if (key == position) {
                if (mVideoMap[position] != null && mVideoMap[position]?.currentState == JzvdStd.CURRENT_STATE_PAUSE) {
                    mVideoMap[position]?.startButton?.performClick()//播放
                }
                continue
            }
        }
    }

    fun setPageData(data: ArrayList<ProductBannerBean>) {
        if (data == null || data.isEmpty()) {
            image_view_place.visibility = View.VISIBLE
            text_view_indicator.visibility = View.GONE
            rl_spot.initIndicator(1)
            return
        }
        rl_spot.initIndicator(data.size)
        text_view_indicator.visibility = View.GONE
        image_view_place.visibility = View.GONE

        if (!(::mPagerAdapter.isInitialized)) {
            mPagerAdapter = BannerPagerAdapter(context, data)
            view_pager_banner.adapter = mPagerAdapter
        } else {
            mPagerAdapter?.mData = data
            mPagerAdapter?.notifyDataSetChanged()
        }
        mCurrentPosition = data.size * 10000
        view_pager_banner.currentItem = mCurrentPosition

        text_view_indicator.text = (mCurrentPosition % mPagerAdapter?.getDataSize()!! + 1).toString() + "/" + mPagerAdapter?.getDataSize().toString()
    }

    private var mOnPageChangeListener: ((position: Int) -> Unit)? = null
    private var mOnItemBannerClick: ((item: String, position: Int) -> Unit)? = null

    fun setOnPageListener(onPageChangeListener: (position: Int) -> Unit,
                          onItemBannerClick: (item: String, position: Int) -> Unit) {
        this.mOnPageChangeListener = onPageChangeListener
        this.mOnItemBannerClick = onItemBannerClick
    }

    private var mVideoMap = HashMap<Int, JzvdStd>()

    inner class BannerPagerAdapter : PagerAdapter {

        var mData: ArrayList<ProductBannerBean> = ArrayList()
        private var mContext: Context? = null

        constructor(context: Context, data: ArrayList<ProductBannerBean>) : super() {
            mData.addAll(data)
            mContext = context
        }

        fun getDataSize(): Int {
            return if (mData != null) {
                mData.size
            } else {
                0
            }
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val pos = position % mData.size
            if (!TextUtils.isEmpty(mData[pos].id) && !TextUtils.isEmpty(mData[pos].videoUrl)) {
                //视频bannner
                var videoView = JzvdStd(mContext)
                val videoGroup = videoView.parent as? ViewGroup
                videoGroup?.removeView(videoView)
                videoView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val jzDataSource = JZDataSource(mData[pos].videoUrl)
                jzDataSource.looping = true
                videoView.setUp(jzDataSource, JzvdStd.SCREEN_WINDOW_NORMAL)
                videoView.widthRatio = ScreenUtils.getScreenWidth()
                videoView.heightRatio = ScreenUtils.getScreenWidth()
                videoView.fullscreenButton.visibility = View.GONE
                videoView.backButton.visibility = View.GONE
                videoView.seekToInAdvance = mCurrentProgross
                videoView.startButton.performClick()
                container?.addView(videoView)
                mVideoMap[pos] = videoView
                LogUtils.info("banner--------：播放视频,pos:$pos")
                return videoView!!
            } else {
                val imageView = ImageView(mContext)
                imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                // 加载 600x600 像素的图片
                GlideLoader.loadImage(mContext, "${mData[pos].picture}_600x600", R.drawable.default_img,imageView)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                container?.addView(imageView)
                imageView.setOnClickListener {
                    mOnItemBannerClick?.invoke(mData[pos].picture, position)
                }
                LogUtils.info("banner--------：播放图片,pos:$pos")
                return imageView
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container?.removeView(`object` as View?)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return if (mData != null && mData.size > 1) { // 自动轮播
                Int.MAX_VALUE / 2
            } else if (mData.size == 1) { // 一张图片不轮播
                mData.size
            } else { // 没有图片
                0
            }
        }
    }

    fun onStart(){

    }

    fun onPause(){

    }

    fun onRelease() {
        if (mPageChangeListener != null) {
            view_pager_banner?.removeOnPageChangeListener(mPageChangeListener)
        }
        //释放VideoView
        val keysIterator = mVideoMap.keys.iterator()
        if (keysIterator.hasNext()) {
            val key = keysIterator.next() as? Int
            key?.let {
                mVideoMap[key]?.release()
                mVideoMap[key]?.cancelProgressTimer()
            }
        }
        mVideoMap.clear()
    }

}