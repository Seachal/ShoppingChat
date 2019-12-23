package com.netease.nim.uikit.business.team.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.business.team.model.TeamMemberBean;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.List;


public class TeamMemberCustomAdapter extends BaseQuickAdapter<TeamMemberBean, BaseViewHolder> {
    public TeamMemberCustomAdapter(@Nullable List<TeamMemberBean> data) {
        super(R.layout.item_team_member, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TeamMemberBean item) {
        helper.addOnClickListener(R.id.iv_operate)
                .addOnClickListener(R.id.team_head_image);
        if (item.isAdd() == false && item.isSubtract() == false) {
            helper.setText(R.id.tv_user_name, TeamHelper.getDisplayNameWithoutMe(item.getTid(), item.getAccount()))
                    .setVisible(R.id.tv_user_name, true)
                    .setGone(R.id.team_head_image, true)
                    .setGone(R.id.iv_operate, false);
            HeadImageView teamHeadImage = helper.getView(R.id.team_head_image);
            teamHeadImage.loadBuddyAvatar(item.getAccount());
        } else if (item.isAdd() == true && item.isSubtract() == false) {
            helper.setVisible(R.id.tv_user_name, false)
                    .setGone(R.id.team_head_image, false)
                    .setGone(R.id.iv_operate, true);
            ImageView ivOperate = helper.getView(R.id.iv_operate);
            ivOperate.setImageResource(R.drawable.seletor_chat_group_add);
        } else if (item.isAdd() == false && item.isSubtract() == true) {
            helper.setVisible(R.id.tv_user_name, false)
                    .setGone(R.id.team_head_image, false)
                    .setGone(R.id.iv_operate, true);
            ImageView ivOperate = helper.getView(R.id.iv_operate);
            ivOperate.setImageResource(R.drawable.selector_chat_btn_group_minus);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
