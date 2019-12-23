package com.laka.shoppingchat.mvp.nim.blacklist.adapter


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.laka.shoppingchat.R
import com.netease.nim.uikit.business.uinfo.UserInfoHelper
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum


class BlackListAdapter(list: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_black_list, list) {
    override fun convert(helper: BaseViewHolder, item: String) {
        var headImageView = helper.getView<HeadImageView>(R.id.iv_avatar)
        headImageView.loadBuddyAvatar(item)
        helper.setText(
            R.id.tv_name,
            UserInfoHelper.getUserTitleName(item, SessionTypeEnum.P2P)
        )
    }
}