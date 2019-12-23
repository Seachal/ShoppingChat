package com.netease.nim.uikit.business.forward.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.core.item.ContactCustomItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.model.IContact;
import com.netease.nim.uikit.business.forward.MultiContactItem;
import com.netease.nim.uikit.business.forward.SelectMultiItem;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

public class SelectItemAdapter extends BaseQuickAdapter<SelectMultiItem, BaseViewHolder> {

    public SelectItemAdapter(@Nullable List<SelectMultiItem> data) {
        super(R.layout.item_select_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SelectMultiItem item) {
        HeadImageView headImageView = helper.getView(R.id.iv_select_avater);
        MultiContactItem contactItem = item.getContactItem();
        RecentContact recentContact = contactItem.getRecentContact();
        ContactItem itemContactItem = contactItem.getContactItem();
        ContactCustomItem contactCustomItem = contactItem.getContactCustomItem();

        if (recentContact != null) {
            if (recentContact.getSessionType() == SessionTypeEnum.P2P) {
                headImageView.loadBuddyAvatar(recentContact.getContactId());
            } else {
                Team mTeam = NimUIKit.getTeamProvider().getTeamById(recentContact.getContactId());
                headImageView.loadTeamIconByTeam(mTeam);
            }
        } else if (itemContactItem != null) {
            IContact contact = itemContactItem.getContact();
            if (contact.getContactType() == IContact.Type.Friend) {
                headImageView.loadBuddyAvatar(contact.getContactId());
            } else {
                Team mTeam = NimUIKit.getTeamProvider().getTeamById(contact.getContactId());
                headImageView.loadTeamIconByTeam(mTeam);
            }
        } else if (contactCustomItem != null) {
            int contactType = contactCustomItem.getContactType();
            if (contactType == IContact.Type.Friend) {
                headImageView.loadBuddyAvatar(contactCustomItem.getContactId());
            } else {
                Team mTeam = NimUIKit.getTeamProvider().getTeamById(contactCustomItem.getContactId());
                headImageView.loadTeamIconByTeam(mTeam);
            }
        }

    }
}
