package com.laka.shoppingchat.mvp.nim.session.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.laka.androidlib.eventbus.Event;
import com.laka.androidlib.util.ApplicationUtils;
import com.laka.androidlib.widget.dialog.CommonConfirmDialog;
import com.laka.androidlib.widget.dialog.LoadingDialog;
import com.laka.androidlib.widget.titlebar.TitleBarView;
import com.laka.shoppingchat.R;
import com.laka.shoppingchat.mvp.nim.team.TeamCreateHelper;
import com.laka.shoppingchat.mvp.user.constant.UserConstant;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.session.helper.MessageListPanelHelper;
import com.netease.nim.uikit.business.team.activity.FlockDetailActivity;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.widget.SwitchButton;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.model.CreateTeamResult;

import java.util.ArrayList;

import ch.ielse.view.SwitchView;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.netease.nim.uikit.business.session.constant.Extras.EXTRA_CUSTOMIZATION;

/**
 * Created by hzxuwen on 2015/10/13.
 */
public class MessageInfoActivity extends UI {
    private final static String EXTRA_ACCOUNT = "EXTRA_ACCOUNT";
    private static final String EXTRA_CUSTOMIZATION = "EXTRA_CUSTOMIZATION";
    private static final int REQUEST_CODE_NORMAL = 1;
    // data
    private String account;
    // view
    private SwitchView svNotifyType;
    private SwitchButton switchButton;
    private SessionCustomization customization;
    private LoadingDialog mLoadingDialog;
    private ImageView mIvLoading;

    public static void startActivity(Context context, String account, SessionCustomization customization) {
        Intent intent = new Intent();
        intent.setClass(context, MessageInfoActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, account);
        intent.putExtra(EXTRA_CUSTOMIZATION, customization);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_info_activity);
        mLoadingDialog = new LoadingDialog(this);
//
//        ToolBarOptions options = new NimToolBarOptions();
//        options.titleId = R.string.message_info;
//        options.navigateId = R.mipmap.ic_back;
//        setToolBar(R.id.toolbar, options);
        TitleBarView titleBarView = findViewById(R.id.title_bar);
        titleBarView.setLeftIcon(R.drawable.selector_nav_btn_back)
                .setTitleTextSize(18)
                .setTitleTextColor(R.color.color_2d2d2d)
                .setBackGroundColor(R.color.color_ededed)
                .showDivider(false)
                .setTitle("私聊详情");
        account = getIntent().getStringExtra(EXTRA_ACCOUNT);
        customization = (SessionCustomization) getIntent().getSerializableExtra(EXTRA_CUSTOMIZATION);
        findViews();
        initData();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSwitchBtn();
    }

    private HeadImageView userHead;
    private TextView userName;

    private void findViews() {
        userHead = (HeadImageView) findViewById(R.id.user_layout).findViewById(R.id.imageViewHeader);
        userName = (TextView) findViewById(R.id.user_layout).findViewById(R.id.textViewName);
        userHead.loadBuddyAvatar(account);
        userName.setText(UserInfoHelper.getUserDisplayName(account));
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserProfile(v);
            }
        });

//        ((TextView) findViewById(R.id.create_team_layout).findViewById(R.id.textViewName)).setText(R.string.create_normal_team);
        ((TextView) findViewById(R.id.create_team_layout).findViewById(R.id.textViewName)).setVisibility(View.GONE);
        HeadImageView addImage = (HeadImageView) findViewById(R.id.create_team_layout).findViewById(R.id.imageViewHeader);
        addImage.setBackgroundResource(R.drawable.seletor_chat_group_add);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTeamMsg();
            }
        });

        mIvLoading = findViewById(R.id.iv_loading);
        svNotifyType = findViewById(R.id.sv_notify_type);
        ((TextView) findViewById(R.id.toggle_layout).findViewById(R.id.user_profile_title)).setText(R.string.msg_notice);
        switchButton = (SwitchButton) findViewById(R.id.toggle_layout).findViewById(R.id.user_profile_toggle);
        switchButton.setOnChangedListener(onChangedListener);


        findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearChattingHistory(account, SessionTypeEnum.P2P);
            }
        });
    }

    private void initData() {
        //消息提醒回显
        boolean needMessageNotify = NIMClient.getService(FriendService.class).isNeedMessageNotify(account);
        if (needMessageNotify) {
            svNotifyType.toggleSwitch(false);
        } else {
            svNotifyType.toggleSwitch(true);
        }
    }

    private void initEvent() {
        svNotifyType.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                mIvLoading.setVisibility(View.VISIBLE);
                ((AnimationDrawable) mIvLoading.getDrawable()).start();
                settingNotifyType(true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                mIvLoading.setVisibility(View.VISIBLE);
                ((AnimationDrawable) mIvLoading.getDrawable()).start();
                settingNotifyType(false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (event.getName().equals(UserConstant.EVENT_MODIFY_ALIAS)) {
            userName.setText(UserInfoHelper.getUserDisplayName(account));
        }
    }

    private void settingNotifyType(final boolean isChecked) {
        NIMClient.getService(FriendService.class).setMessageNotify(account, !isChecked).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mIvLoading.setVisibility(View.GONE);
                ((AnimationDrawable) mIvLoading.getDrawable()).stop();
                svNotifyType.toggleSwitch(isChecked);
                if (isChecked) {
                    ToastHelper.showToast(MessageInfoActivity.this, "已开启免打扰模式");
                } else {
                    ToastHelper.showToast(MessageInfoActivity.this, "已关闭免打扰模式");
                }
            }

            @Override
            public void onFailed(int i) {
                mIvLoading.setVisibility(View.GONE);
                ((AnimationDrawable) mIvLoading.getDrawable()).stop();
                svNotifyType.toggleSwitch(!isChecked);
                if (isChecked) {
                    ToastHelper.showToast(MessageInfoActivity.this, "开启失败");
                } else {
                    ToastHelper.showToast(MessageInfoActivity.this, "关闭失败");
                }
            }

            @Override
            public void onException(Throwable throwable) {
                mIvLoading.setVisibility(View.GONE);
                ((AnimationDrawable) mIvLoading.getDrawable()).stop();
                svNotifyType.toggleSwitch(!isChecked);
                if (isChecked) {
                    ToastHelper.showToast(MessageInfoActivity.this, "开启异常");
                } else {
                    ToastHelper.showToast(MessageInfoActivity.this, "关闭异常");
                }
            }
        });
    }


    /**
     * 清除与指定用户的所有消息记录
     *
     * @param account     聊天对象ID，群id 或者 用户account，与sessionType 一一对应
     * @param sessionType 聊天类型，群聊或者P2P
     */
    public void clearChattingHistory(final String account, final SessionTypeEnum sessionType) {
        CommonConfirmDialog confirmDialog = new CommonConfirmDialog(this);
        confirmDialog.setDefaultTitleTxt("您确定要清除聊天记录吗？");
        confirmDialog.setOnClickSureListener(new CommonConfirmDialog.OnClickSureListener() {
            @Override
            public void onClickSure(View view) {
                NIMClient.getService(MsgService.class).clearChattingHistory(account, sessionType);
                MessageListPanelHelper.getInstance().notifyClearMessages(account);
                ToastHelper.showToast(MessageInfoActivity.this, "清除成功");
            }
        });
        confirmDialog.show();
    }

    private void updateSwitchBtn() {
        boolean notice = NIMClient.getService(FriendService.class).isNeedMessageNotify(account);
        switchButton.setCheck(notice);
    }

    private SwitchButton.OnChangedListener onChangedListener = new SwitchButton.OnChangedListener() {
        @Override
        public void OnChanged(View v, final boolean checkState) {
            if (!NetworkUtil.isNetAvailable(MessageInfoActivity.this)) {
                ToastHelper.showToast(MessageInfoActivity.this, R.string.network_is_not_available);
                switchButton.setCheck(!checkState);
                return;
            }

            NIMClient.getService(FriendService.class).setMessageNotify(account, checkState).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    if (checkState) {
                        ToastHelper.showToast(MessageInfoActivity.this, "开启消息提醒成功");
                    } else {
                        ToastHelper.showToast(MessageInfoActivity.this, "关闭消息提醒成功");
                    }
                }

                @Override
                public void onFailed(int code) {
                    if (code == 408) {
                        ToastHelper.showToast(MessageInfoActivity.this, R.string.network_is_not_available);
                    } else {
                        ToastHelper.showToast(MessageInfoActivity.this, "on failed:" + code);
                    }
                    switchButton.setCheck(!checkState);
                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        }
    };

    private void openUserProfile(View v) {
        if (customization != null) {
            customization.buttons.get(0).onClick(this, v, account);
        }
//        UserProfileActivity.start(this, account);
    }

    /**
     * 创建群聊
     */
    private void createTeamMsg() {
        ArrayList<String> memberAccounts = new ArrayList<>();
        memberAccounts.add(account);
        ContactSelectActivity.Option option = TeamHelper.getCreateContactSelectOption(memberAccounts, 50);
        NimUIKit.startContactSelector(this, option, REQUEST_CODE_NORMAL);// 创建群
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_NORMAL) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                if (selected != null && !selected.isEmpty()) {
                    TeamCreateHelper.createAdvancedTeam(this, selected);
                } else {
                    ToastHelper.showToast(ApplicationUtils.getContext(), "请选择至少一个联系人！");
                }
            }
        }
    }
}
