package com.netease.nim.uikit.business.recent.holder;

import com.netease.nim.uikit.business.session.attachment.RedPackageAttachment;
import com.netease.nim.uikit.business.session.attachment.RobRedPackageAttachment;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

public class CommonRecentViewHolder extends RecentViewHolder {

    CommonRecentViewHolder(BaseQuickAdapter adapter) {
        super(adapter);
    }

    @Override
    protected String getContent(RecentContact recent) {
        return descOfMsg(recent);
    }

    @Override
    protected String getOnlineStateContent(RecentContact recent) {
        if (recent.getSessionType() == SessionTypeEnum.P2P && NimUIKitImpl.enableOnlineState()) {
            return NimUIKitImpl.getOnlineStateContentProvider().getSimpleDisplay(recent.getContactId());
        } else {
            return super.getOnlineStateContent(recent);
        }
    }

    String descOfMsg(RecentContact recent) {
        if (recent.getMsgType() == MsgTypeEnum.text) {
            return recent.getContent();
        } else if (recent.getMsgType() == MsgTypeEnum.custom) {
            MsgAttachment attachment = recent.getAttachment();
            if (attachment instanceof RedPackageAttachment) {
                return "[购聊红包]";
            } else if (attachment instanceof RobRedPackageAttachment) {
                //抢红包tip
                RobRedPackageAttachment robRedPackageAttachment = (RobRedPackageAttachment) attachment;
                if (robRedPackageAttachment.getmUserId().equals(NimUIKitImpl.getAccount()) &&
                        robRedPackageAttachment.getmOpUser().equals(NimUIKitImpl.getAccount())) {
                    return "你领取了自己发的红包";
                } else if (robRedPackageAttachment.getmOpUser().equals(NimUIKitImpl.getAccount())) {
                    return "你领取了" + UserInfoHelper.getUserName(robRedPackageAttachment.getmUserId()) + "的红包";
                } else if (robRedPackageAttachment.getmUserId().equals(NimUIKitImpl.getAccount())) {
                    return UserInfoHelper.getUserName(robRedPackageAttachment.getmOpUser()) + "领取了你的红包";
                }
            }
        } else if (recent.getMsgType() == MsgTypeEnum.tip) {
            String digest = null;
            if (getCallback() != null) {
                digest = getCallback().getDigestOfTipMsg(recent);
            }

            if (digest == null) {
                digest = NimUIKitImpl.getRecentCustomization().getDefaultDigest(recent);
            }

            return digest;
        } else if (recent.getAttachment() != null) {
            String digest = null;
            if (getCallback() != null) {
                digest = getCallback().getDigestOfAttachment(recent, recent.getAttachment());
            }

            if (digest == null) {
                digest = NimUIKitImpl.getRecentCustomization().getDefaultDigest(recent);
            }

            return digest;
        }

        return "[未知]";
    }
}
