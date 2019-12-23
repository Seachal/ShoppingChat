package com.laka.shoppingchat.mvp.chat.view.activity

import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.View
import com.laka.androidlib.base.activity.BaseActivity
import com.laka.androidlib.util.StatusBarUtil
import com.laka.androidlib.util.toast.ToastHelper
import com.laka.shoppingchat.R
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.model.Team
import kotlinx.android.synthetic.main.activity_add_team.*


/**
 * @Author:summer
 * @Date:2019/10/9
 * @Description:
 */
class AddTeamActivity : BaseActivity() {

    private var mTid: String = ""
    private lateinit var mTeam: Team

    override fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_ededed), 0)
            StatusBarUtil.setLightModeNotFullScreen(this, true)
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, color), 0)
        }
    }

    override fun setContentView(): Int {
        return R.layout.activity_add_team
    }

    override fun initIntent() {
        intent?.let {
            mTid = it.getStringExtra("tId")
        }
    }

    override fun initViews() {

    }

    override fun initData() {
        loadTeamInfo()
    }

    override fun initEvent() {
        sb_sure.setOnClickListener {
            if (::mTeam.isInitialized) {
                NIMClient.getService(TeamService::class.java).applyJoinTeam(mTid, null)
                    .setCallback(object : RequestCallback<Team> {
                        override fun onSuccess(p0: Team?) {
                            ToastHelper.showToast("申请成功, 等待验证入群")
                            finish()
                        }

                        override fun onFailed(p0: Int) {
                            when (p0) {
                                808 -> ToastHelper.showToast("申请已发出")
                                809 -> ToastHelper.showToast("已经在群里")
                                else -> ToastHelper.showToast("申请失败")
                            }
                            finish()
                        }

                        override fun onException(p0: Throwable?) {
                            ToastHelper.showToast("申请失败")
                            finish()
                        }
                    })
            } else {
                ToastHelper.showToast("获取群资料失败，请稍后重试")
            }
        }
        iv_delete.setOnClickListener { finish() }
    }

    private fun loadTeamInfo() {
        val team = NimUIKit.getTeamProvider().getTeamById(mTid)
        if (team != null) {
            updateTeamInfo(team)
        } else {
            NimUIKit.getTeamProvider().fetchTeamById(
                mTid
            ) { success, result, _ ->
                if (success && result != null) {
                    updateTeamInfo(result)
                } else {
                    ToastHelper.showToast(getString(R.string.team_not_exist))
                    finish()
                }
            }
        }
    }

    private fun updateTeamInfo(team: Team) {
        this.mTeam = team
        if (team == null) {
            ToastHelper.showToast(getString(R.string.team_not_exist))
            finish()
            return
        }
        tv_name.text = mTeam.name
        iv_icon.loadAvatar(mTeam.icon)
        tv_number.text = "（共${mTeam.memberCount}人）"
        if (mTeam.isMyTeam){
            sb_sure.text = "你已在群组内"
            sb_sure.isEnabled = false
        }
    }

}