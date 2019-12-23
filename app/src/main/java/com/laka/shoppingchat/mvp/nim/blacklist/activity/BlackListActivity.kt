package com.laka.shoppingchat.mvp.nim.blacklist.activity

import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.util.StatusBarUtil
import com.laka.shoppingchat.R
import com.laka.shoppingchat.mvp.nim.activity.UserInfoActivity
import com.laka.shoppingchat.mvp.nim.blacklist.adapter.BlackListAdapter
import com.laka.shoppingchat.mvp.nim.event.BlackEvent
import com.netease.nim.uikit.business.ChatRouteManager
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.friend.FriendService
import kotlinx.android.synthetic.main.activity_black_list.*
import kotlinx.android.synthetic.main.activity_black_list.title_bar
import kotlinx.android.synthetic.main.activty_user_info.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.HashMap


class BlackListActivity : BaseActivity() {
    lateinit var mAdapter: BlackListAdapter
    override fun setContentView(): Int = R.layout.activity_black_list

    override fun initIntent() {
    }

    override fun initViews() {
        title_bar.setLeftIcon(R.drawable.selector_nav_btn_back)
            .setBackGroundColor(R.color.color_ededed)
            .showDivider(false)
            .setOnLeftClickListener {
                finish()
            }
    }

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(
                this,
                ContextCompat.getColor(this, R.color.color_ededed),
                0
            )
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun initData() {
        val accounts = NIMClient.getService(FriendService::class.java).blackList
        mAdapter = BlackListAdapter(accounts)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            UserInfoActivity.start(this@BlackListActivity, mAdapter.data[position])
        }
        mRvList.adapter = mAdapter
        mRvList.layoutManager = LinearLayoutManager(this)
    }

    override fun initEvent() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: BlackEvent) {
        if (!event.isAddBlack) {
            mAdapter.data.remove(event.account)
            mAdapter.notifyDataSetChanged()
        } else {
            mAdapter.addData(event.account)
            mAdapter.notifyDataSetChanged()
        }
    }
}