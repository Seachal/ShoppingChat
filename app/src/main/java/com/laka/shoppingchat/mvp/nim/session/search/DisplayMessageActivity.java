package com.laka.shoppingchat.mvp.nim.session.search;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.laka.androidlib.util.StatusBarUtil;
import com.laka.androidlib.widget.titlebar.TitleBarView;
import com.laka.shoppingchat.R;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.business.session.module.Container;
import com.netease.nim.uikit.business.session.module.ModuleProxy;
import com.netease.nim.uikit.business.session.module.list.MessageListPanelEx;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * 搜索结果消息列表界面
 * <p>
 * Created by huangjun on 2017/1/11.
 */
public class DisplayMessageActivity extends UI implements ModuleProxy {

    private static String EXTRA_ANCHOR = "anchor";
    private TitleBarView titleBar;

    public static void start(Context context, IMMessage anchor) {
        Intent intent = new Intent();
        intent.setClass(context, DisplayMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //search extra
        intent.putExtra(EXTRA_ANCHOR, anchor);

        context.startActivity(intent);
    }

    // context
    private SessionTypeEnum sessionType;
    private String account; // 对方帐号
    private IMMessage anchor;

    private MessageListPanelEx messageListPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = LayoutInflater.from(this).inflate(R.layout.message_history_activity, null);
        setContentView(rootView);
        titleBar = rootView.findViewById(R.id.title_bar);
//        ToolBarOptions options = new NimToolBarOptions();
//        setToolBar(R.id.toolbar, options);
        titleBar.setLeftIcon(R.drawable.seletor_nav_btn_back)
                .setBackGroundColor(R.color.color_gray_bg)
                .setTitleTextColor(R.color.color_2d2d2d)
                .setTitleTextSize(18);
        onParseIntent();

        Container container = new Container(this, account, sessionType, this);
        messageListPanel = new MessageListPanelEx(container, rootView, anchor, true, false);
    }
    @Override
    protected void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, com.netease.nim.uikit.R.color.color_ededed), 0);
            StatusBarUtil.setLightMode(this);
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, com.netease.nim.uikit.R.color.black), 0);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        messageListPanel.onDestroy();
    }

    protected void onParseIntent() {
        anchor = (IMMessage) getIntent().getSerializableExtra(EXTRA_ANCHOR);
        account = anchor.getSessionId();
        sessionType = anchor.getSessionType();

//        setTitle(UserInfoHelper.getUserTitleName(account, sessionType));
        titleBar.setTitle(UserInfoHelper.getUserTitleName(account, sessionType));
    }

    @Override
    public boolean sendMessage(IMMessage msg) {
        return false;
    }

    @Override
    public void onInputPanelExpand() {

    }

    @Override
    public void onItemFooterClick(IMMessage message) {

    }

    @Override
    public void shouldCollapseInputPanel() {

    }

    @Override
    public boolean isLongClickEnabled() {
        return true;
    }

}
