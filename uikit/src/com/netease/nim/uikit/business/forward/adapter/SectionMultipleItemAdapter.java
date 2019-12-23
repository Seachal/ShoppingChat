package com.netease.nim.uikit.business.forward.adapter;

import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.model.IContact;
import com.netease.nim.uikit.business.forward.MultiContactItem;
import com.netease.nim.uikit.business.forward.SelectMultiItem;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;


public class SectionMultipleItemAdapter extends BaseSectionMultiItemQuickAdapter<SelectMultiItem, BaseViewHolder> {
    /**
     * init SectionMultipleItemAdapter
     * 1. add your header resource layout
     * 2. add some kind of items
     *
     * @param sectionHeadResId The section head layout id for each item
     */
    public SectionMultipleItemAdapter(int sectionHeadResId) {
        super(sectionHeadResId, null);
        addItemType(SelectMultiItem.Contact, R.layout.item_contact);
        addItemType(SelectMultiItem.MORE, R.layout.item_more);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, final SelectMultiItem item) {
        // deal with header viewHolder
        helper.setText(R.id.header, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, SelectMultiItem item) {
        // deal with multiple type items viewHolder
        switch (helper.getItemViewType()) {
            case SelectMultiItem.Contact:
                helper.addOnClickListener(R.id.card_view)
                        .addOnClickListener(R.id.isSelect)
                        .setGone(R.id.isSelect, item.isMultipleSelect());
                CheckBox checkBox = helper.getView(R.id.isSelect);
                HeadImageView headImageView = helper.getView(R.id.iv_avater);
                MultiContactItem multiContactItem = item.getContactItem();
                RecentContact recentContact = multiContactItem.getRecentContact();
                ContactItem contactItem = multiContactItem.getContactItem();
                checkBox.setChecked(item.isSelect());
                if (recentContact != null) {
                    if (recentContact.getSessionType() == SessionTypeEnum.P2P) {
                        headImageView.loadBuddyAvatar(recentContact.getContactId());
                    } else {
                        Team mTeam = NimUIKit.getTeamProvider().getTeamById(recentContact.getContactId());
                        headImageView.loadTeamIconByTeam(mTeam);
                    }
                    helper.setText(R.id.tv_name, UserInfoHelper.getUserTitleName(recentContact.getContactId(), recentContact.getSessionType()));
                } else if (contactItem != null) {
                    IContact contact = contactItem.getContact();
                    if (contact != null) {
                        if (contact.getContactType() == IContact.Type.Friend) {
                            headImageView.loadBuddyAvatar(contact.getContactId());
                            helper.setText(R.id.tv_name, UserInfoHelper.getUserTitleName(contact.getContactId(), SessionTypeEnum.P2P));
                        } else {
                            Team mTeam = NimUIKit.getTeamProvider().getTeamById(contact.getContactId());
                            headImageView.loadTeamIconByTeam(mTeam);
                            helper.setText(R.id.tv_name, UserInfoHelper.getUserTitleName(contact.getContactId(), SessionTypeEnum.Team));
                        }
                    }

                }
                break;
            default:
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
