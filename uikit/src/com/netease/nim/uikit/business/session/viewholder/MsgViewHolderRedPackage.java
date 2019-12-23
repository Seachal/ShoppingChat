package com.netease.nim.uikit.business.session.viewholder;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.laka.androidlib.util.LogUtils;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.attachment.RedPackageAttachment;
import com.netease.nim.uikit.business.session.constant.RedPackageConstant;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.Map;

/**
 * @Author:summer
 * @Date:2019/9/6
 * @Description:红包消息holder
 */
public class MsgViewHolderRedPackage extends MsgViewHolderBase {

    public MsgViewHolderRedPackage(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_radpackage;
    }

    private TextView tvTitleLeft;
    private TextView tvStatusDesLeft;
    private ConstraintLayout clBgLeft;
    private ConstraintLayout clLeft;
    private ConstraintLayout clRight;

    private TextView tvTitleRight;
    private TextView tvStatusDesRight;
    private ConstraintLayout clBgRight;

    @Override
    protected void inflateContentView() {
        clBgLeft = findViewById(R.id.cl_bg);
        tvTitleLeft = findViewById(R.id.tv_msg);
        tvStatusDesLeft = findViewById(R.id.tv_already_recent);

        clBgRight = findViewById(R.id.cl_bg_right);
        tvTitleRight = findViewById(R.id.tv_msg_right);
        tvStatusDesRight = findViewById(R.id.tv_already_recent_right);

        clLeft = findViewById(R.id.cl_left);
        clRight = findViewById(R.id.cl_right);
    }

    @Override
    protected void bindContentView() {
        LogUtils.info("repackage------" + message.getMsgType());
        LogUtils.info("repackage------" + message.getContent());
        LogUtils.info("repackage------" + message.getAttachStr());
        LogUtils.info("repackage------" + message.getFromAccount());
        LogUtils.info("repackage------" + message.getFromNick());
        LogUtils.info("repackage------" + message.getPushContent());
        LogUtils.info("repackage------" + message.getSessionId());
        LogUtils.info("repackage------" + message.getUuid());
        LogUtils.info("repackage------" + message.getAttachment());
        LogUtils.info("repackage------" + message.getAttachStatus());
        LogUtils.info("repackage------" + message.getStatus());
        LogUtils.info("repackage--=============================================================================");

        MsgAttachment attachment = message.getAttachment();
        if (attachment instanceof RedPackageAttachment) {
            RedPackageAttachment redPackageAttachment = (RedPackageAttachment) attachment;
            int clientStatus = getLocalExtension();
            if (isReceivedMessage()) {//对其左边
                clRight.setVisibility(View.GONE);
                clLeft.setVisibility(View.VISIBLE);
                tvTitleLeft.setText(redPackageAttachment.getmTitle());
                if (clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_NORMAL ||
                        clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_DEFAULT ||
                        clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_SEFT) {
                    clBgLeft.setBackgroundResource(R.drawable.chat_bg_hb_received_l);
                } else {
                    clBgLeft.setBackgroundResource(R.drawable.chat_bg_hb_get);
                }
                handleRedPackageStatus(tvStatusDesLeft);
            } else { //对其右边
                clRight.setVisibility(View.VISIBLE);
                clLeft.setVisibility(View.GONE);
                tvTitleRight.setText(redPackageAttachment.getmTitle());
                if (clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_NORMAL ||
                        clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_DEFAULT ||
                        clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_SEFT) {
                    clBgRight.setBackgroundResource(R.drawable.chat_bg_hb_received);
                } else {
                    clBgRight.setBackgroundResource(R.drawable.chat_bg_hb_get_r);
                }
                handleRedPackageStatus(tvStatusDesRight);
            }
        }
    }

    private void handleRedPackageStatus(TextView tvStatusDes) {
        int clientStatus = getLocalExtension();
        if (clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_NORMAL) {
            tvStatusDes.setVisibility(View.GONE);
        } else if (clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_ROBBED) {
            tvStatusDes.setVisibility(View.VISIBLE);
            tvStatusDes.setText("已被领完");
        } else if (clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_ALREADY_ROB) {
            tvStatusDes.setVisibility(View.VISIBLE);
            tvStatusDes.setText("已领取");
        } else if (clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_SEFT) {
            if (message.getSessionType() == SessionTypeEnum.P2P) {
                tvStatusDes.setVisibility(View.VISIBLE);
                tvStatusDes.setText("已被领完");
            } else if (message.getSessionType() == SessionTypeEnum.Team
                    || message.getSessionType() == SessionTypeEnum.SUPER_TEAM
                    || message.getSessionType() == SessionTypeEnum.ChatRoom) {
                tvStatusDes.setVisibility(View.GONE);
            }
        } else if (clientStatus == RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_OVERDUE) {
            tvStatusDes.setVisibility(View.VISIBLE);
            tvStatusDes.setText("已过期");
        } else {
            tvStatusDes.setVisibility(View.GONE);
        }
    }

    private int getLocalExtension() {
        try {
            Map<String, Object> params = message.getLocalExtension();
            int clientStatus = RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_DEFAULT;
            if (params != null) {
                clientStatus = Integer.parseInt(params.get(RedPackageConstant.KEY_RED_PACKAGE_CLIENT_STATUS).toString());
            }
            return clientStatus;
        } catch (Exception e) {
            e.printStackTrace();
            return RedPackageConstant.RED_PACKAGE_CLIENT_STATUS_DEFAULT;
        }
    }

    @Override
    protected void onItemClick() {
        getMsgAdapter().getEventListener().onItemClick(view, message);
    }
}
