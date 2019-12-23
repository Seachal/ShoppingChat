package com.netease.nim.uikit.business.session.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laka.androidlib.util.LogUtils;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.attachment.RobRedPackageAttachment;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.impl.NimUIKitImpl;

/**
 * @Author:summer
 * @Date:2019/9/6
 * @Description:自定义通知（抢红包通知）
 */
public class MsgViewHolderCustomNotification extends MsgViewHolderBase implements View.OnClickListener {

    private ImageView mIvIcon;
    private TextView mTvReceive;
    private TextView mTvRedPackage;

    public MsgViewHolderCustomNotification(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_redpackage_notice;
    }

    @Override
    protected void inflateContentView() {
        mIvIcon = findViewById(R.id.iv_icon);
        mTvReceive = findViewById(R.id.tv_receive);
        mTvRedPackage = findViewById(R.id.tv_redpackage);
        initEvent();
    }

    private void initEvent() {
        mTvRedPackage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_redpackage) {
            onItemClick();
        }
    }

    @Override
    protected void bindContentView() {
        RobRedPackageAttachment attachment = (RobRedPackageAttachment) message.getAttachment();
        if (attachment.getmUserId().equals(NimUIKitImpl.getAccount()) &&
                attachment.getmOpUser().equals(NimUIKitImpl.getAccount())) {
            //发红包和抢红包都是当前用户
            mTvReceive.setText("你领取了自己发的");
        } else if (attachment.getmOpUser().equals(NimUIKitImpl.getAccount())) {
            //抢红包的人为当前用户
            mTvReceive.setText("你领取了" + UserInfoHelper.getUserName(attachment.getmUserId()) + "的");
        } else if (attachment.getmUserId().equals(NimUIKitImpl.getAccount())) {
            //发红包的是当前用户
            mTvReceive.setText(UserInfoHelper.getUserName(attachment.getmOpUser()) + "领取了你的");
        } else {
            //当前用户既不是发红包的也不是抢红包的
            LogUtils.info("其他类型通知");
            LogUtils.info("--------" + attachment.toString());
            mTvReceive.setText("未匹配通知----" + UserInfoHelper.getUserName(attachment.getmOpUser()) + "领取了" + UserInfoHelper.getUserName(attachment.getmUserId()) + "的");
        }
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }

    @Override
    protected void onItemClick() {
        getMsgAdapter().getEventListener().onItemClick(view, message);
    }

}
