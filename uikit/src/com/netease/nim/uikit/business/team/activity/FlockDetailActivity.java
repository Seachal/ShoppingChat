package com.netease.nim.uikit.business.team.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.laka.androidlib.base.activity.BaseActivity;
import com.laka.androidlib.util.ImageViewUtils;
import com.laka.androidlib.util.LogUtils;
import com.laka.androidlib.util.StatusBarUtil;
import com.laka.androidlib.util.screen.ScreenUtils;
import com.laka.androidlib.widget.SpaceItemDecoration;
import com.laka.androidlib.widget.dialog.CommonConfirmDialog;
import com.laka.androidlib.widget.dialog.JAlertDialog;
import com.laka.androidlib.widget.dialog.OnJAlertDialogClickListener;
import com.laka.androidlib.widget.titlebar.TitleBarView;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.business.ChatRouteManager;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.session.actions.PickImageAction;
import com.netease.nim.uikit.business.session.helper.MessageListPanelHelper;
import com.netease.nim.uikit.business.team.adapter.TeamMemberCustomAdapter;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.business.team.model.TeamMemberBean;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamInviteModeEnum;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.constant.TeamMessageNotifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.othershe.combinebitmap.CombineBitmap;
import com.othershe.combinebitmap.layout.WechatLayoutManager;
import com.othershe.combinebitmap.listener.OnProgressListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ch.ielse.view.SwitchView;

/**
 * 高级群群资料页
 * Created by huangjun on 2015/3/17.
 */
public class FlockDetailActivity extends BaseActivity {

    private static final int REQUEST_CODE_TEAM_NICK = 101;

    private static final int REQUEST_CODE_MEMBER_LIST = 102;

    private static final int REQUEST_CODE_CONTACT_SELECT = 103;

    private static final int REQUEST_CODE_CONTACT_SELECT_REMOVE = 105;

    private static final int REQUEST_PICK_ICON = 104;
    private static final String EXTRA_ID = "EXTRA_ID";
    private static final String EXTRA_CUSTOMIZATION = "EXTRA_CUSTOMIZATION";
    public static final String RESULT_EXTRA_REASON = "RESULT_EXTRA_REASON";

    public static final String RESULT_EXTRA_REASON_QUIT = "RESULT_EXTRA_REASON_QUIT";

    public static final String RESULT_EXTRA_REASON_DISMISS = "RESULT_EXTRA_REASON_DISMISS";
    private TitleBarView titleBar;
    private RecyclerView rlList;
    private TextView tvDelTeam;
    private TextView tvClearChat;
    private TextView tvTeamName;
    private LinearLayout llGroupName;
    private LinearLayout llTeamNick;
    private TextView tvAllTeam;
    private TextView tvTeamNick;
    private ImageView ivAvater;
    private SwitchView svNotifyType;
    private SwitchView svShowName;
    private ImageView ivLoading;
    private View mQrCode;
    private String teamId;
    private SessionCustomization customization;
    private TeamMemberCustomAdapter memberAdapter;
    private Team team;
    // state
    private boolean isSelfAdmin = false;
    private String creator;
    private List<TeamMemberBean> memberBeanList = new ArrayList<>();
    private List<String> memberAccounts = new ArrayList<>();
    private AbortableFuture<String> uploadFuture;
    private static final int ICON_TIME_OUT = 30000;
    private List<TeamMember> mMembers = new ArrayList<>();

    public static void start(Context context, String tid, SessionCustomization customization) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, tid);
        intent.putExtra(EXTRA_CUSTOMIZATION, customization);
        intent.setClass(context, FlockDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int setContentView() {
        return R.layout.activity_flock_detail;
    }

    @Override
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_ededed), 0);
            StatusBarUtil.setLightMode(this);
        } else {
            super.setStatusBarColor(color);
        }
    }

    @Override
    public void initIntent() {
        teamId = getIntent().getStringExtra(EXTRA_ID);
        customization = (SessionCustomization) getIntent().getSerializableExtra(EXTRA_CUSTOMIZATION);
    }

    @Override
    protected void initViews() {
        titleBar = findViewById(R.id.title_bar);
        rlList = findViewById(R.id.rl_list);
        tvDelTeam = findViewById(R.id.tv_del);
        tvClearChat = findViewById(R.id.tv_clear_chat);
        tvTeamName = findViewById(R.id.tv_team_name);
        llGroupName = findViewById(R.id.ll_group_name);
        llTeamNick = findViewById(R.id.ll_my_name);
        ivAvater = findViewById(R.id.iv_avater);
        svNotifyType = findViewById(R.id.sv_notify_type);
        svShowName = findViewById(R.id.sv_show_name);
        ivLoading = findViewById(R.id.iv_loading);
        mQrCode = findViewById(R.id.ll_qrcode);
        tvAllTeam = findViewById(R.id.tv_all_team);
        tvTeamNick = findViewById(R.id.tv_team_nick);
    }

    @Override
    protected void initData() {
        //消息免打扰
        titleBar.setTitle("群聊详情")
                .setBackGroundColor(R.color.color_ededed)
                .setTitleTextColor(R.color.color_2d2d2d)
                .setLeftIcon(R.drawable.seletor_nav_btn_back);
        initRlAdpter();
        loadTeamInfo();
        requestMembers();
        initNotifyType();
        initShowName();
    }

    private void initShowName() {
        String showName = TeamHelper.getShowName(teamId);
        if (showName.equals(TeamHelper.OPEN_SHOW_NAME)) {
            svShowName.setOpened(true);
        } else {
            svShowName.setOpened(false);
        }
    }

    private static int defaultColumns = 5;
    private static int defaultSpace = 20;

    private void initRlAdpter() {
        memberAdapter = new TeamMemberCustomAdapter(memberBeanList);
        memberAdapter.setHasStableIds(true);
        rlList.addItemDecoration(new SpaceItemDecoration(mContext).setSpace(defaultSpace).setSpaceColor(-0x131314));

        int itemViewWidth = (int) ((ScreenUtils.getFontSizeScale() * ScreenUtils.dp2px(40)));
        int displayWidth = ScreenUtils.getScreenWidth() - ((defaultColumns + 1) * ScreenUtils.dp2px(defaultSpace));
        int columns = displayWidth / itemViewWidth;
        if (columns > defaultColumns) columns = defaultColumns;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);

        rlList.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        gridLayoutManager.setAutoMeasureEnabled(true);
        rlList.setHasFixedSize(true);
        rlList.setNestedScrollingEnabled(false);
        memberAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                TeamMemberBean memberBean = memberAdapter.getData().get(position);
                if (view.getId() == R.id.iv_operate) {
                    boolean isAdd = memberBean.isAdd();
                    boolean isSubtract = memberBean.isSubtract();
                    if (isAdd && !isSubtract) {
                        onAddMember();
                    } else if (!isAdd && isSubtract) {
                        onRemoveMember();
                    }
                } else {
//                    当前用户则return
//                    if (memberBean.getAccount().equals(NimUIKit.getAccount())) {
//                        return;
//                    }
                    if (customization != null) {
                        customization.buttons.get(0).onClick(FlockDetailActivity.this, view, memberBean.getAccount());
                    }
                }
            }
        });
        rlList.setAdapter(memberAdapter);
//        rlList.setLayoutManager(new GridLayoutManager(this, 5));
        loadTeamInfo();
        requestMembers();
    }

    @Override
    protected void initEvent() {
        llGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMembers.size() > 50) {
                    showHintDialog();
                } else {
                    TeamPropertySettingActivity.start(FlockDetailActivity.this, teamId, TeamFieldEnum.Name,
                            team.getName());
                }
            }
        });
        llTeamNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTeamNickActivity.start(FlockDetailActivity.this, teamId);
            }
        });
        tvClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearChattingHistory(teamId, SessionTypeEnum.Team);
            }
        });
        tvDelTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitHint();
            }
        });
        svNotifyType.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                ivLoading.setVisibility(View.VISIBLE);
                ((AnimationDrawable) ivLoading.getDrawable()).start();
                settingNotifyType(true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                ivLoading.setVisibility(View.VISIBLE);
                ((AnimationDrawable) ivLoading.getDrawable()).start();
                settingNotifyType(false);
            }
        });
        svShowName.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                TeamHelper.setShowName(teamId, TeamHelper.OPEN_SHOW_NAME);
                svShowName.setOpened(true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                TeamHelper.setShowName(teamId, TeamHelper.CLOSE_SHOW_NAME);
                svShowName.setOpened(false);
            }
        });
        mQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> params = new HashMap();
                params.put("account", teamId);
                ChatRouteManager.get(ChatRouteManager.ROUTE_TEAM_QRCODE_ACTIVITY).onJump(FlockDetailActivity.this, params);
            }
        });
        tvAllTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchTeamActivity.start(FlockDetailActivity.this, teamId, customization);
            }
        });
    }

    private void showHintDialog() {

        CommonConfirmDialog confirmDialog = new CommonConfirmDialog(this);
        confirmDialog.setOnClickSureListener(new CommonConfirmDialog.OnClickSureListener() {
            @Override
            public void onClickSure(View view) {

            }
        });
        confirmDialog.show();
        confirmDialog.setDefaultTitleTxt("当前群聊人数较多，只有群主及管理员才能修改群名");
        confirmDialog.setCancelVisibility(View.GONE);
    }

    private void settingNotifyType(final boolean isChecked) {
        TeamMessageNotifyTypeEnum type;
        if (isChecked) {
            type = TeamMessageNotifyTypeEnum.Mute;
        } else {
            type = TeamMessageNotifyTypeEnum.All;
        }
        NIMClient.getService(TeamService.class).muteTeam(teamId, type).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                if (isChecked) {
                    ToastHelper.showToast(FlockDetailActivity.this, "已开启免打扰模式");
                } else {
                    ToastHelper.showToast(FlockDetailActivity.this, "已关闭免打扰模式");
                }
                ivLoading.setVisibility(View.GONE);
                ((AnimationDrawable) ivLoading.getDrawable()).stop();
                svNotifyType.toggleSwitch(isChecked);
            }

            @Override
            public void onFailed(int code) {
                ivLoading.setVisibility(View.GONE);
                ((AnimationDrawable) ivLoading.getDrawable()).stop();
                svNotifyType.toggleSwitch(!isChecked);
                if (isChecked) {
                    ToastHelper.showToast(FlockDetailActivity.this, "开启失败");
                } else {
                    ToastHelper.showToast(FlockDetailActivity.this, "关闭失败");
                }
            }

            @Override
            public void onException(Throwable exception) {
                ivLoading.setVisibility(View.GONE);
                ((AnimationDrawable) ivLoading.getDrawable()).stop();
                svNotifyType.toggleSwitch(!isChecked);
                if (isChecked) {
                    ToastHelper.showToast(FlockDetailActivity.this, "开启异常");
                } else {
                    ToastHelper.showToast(FlockDetailActivity.this, "关闭异常");
                }
            }
        });
    }

    private void initNotifyType() {
        Team team = NimUIKit.getTeamProvider().getTeamById(teamId);
        if (team != null) {
            updateTeamNotifyType(team);
        } else {
            NimUIKit.getTeamProvider().fetchTeamById(teamId, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team team, int code) {
                    if (success && team != null) {
                        updateTeamNotifyType(team);
                    } else {
                        onGetTeamInfoFailed();
                    }
                }
            });
        }
    }

    private void updateTeamNotifyType(Team team) {
        if (team == null) return;
        TeamMessageNotifyTypeEnum messageNotifyType = team.getMessageNotifyType();
        if (messageNotifyType == TeamMessageNotifyTypeEnum.Mute) {
            svNotifyType.toggleSwitch(true);
        } else if (messageNotifyType == TeamMessageNotifyTypeEnum.All) {
            svNotifyType.toggleSwitch(false);
        }
    }

    /**
     * 初始化群组基本信息
     */
    private void loadTeamInfo() {
        Team t = NimUIKit.getTeamProvider().getTeamById(teamId);
        if (t != null) {
            updateTeamInfo(t);
        } else {
            NimUIKit.getTeamProvider().fetchTeamById(teamId, new SimpleCallback<Team>() {

                @Override
                public void onResult(boolean success, Team result, int code) {
                    if (success && result != null) {
                        updateTeamInfo(result);
                    } else {
                        onGetTeamInfoFailed();
                    }
                }
            });
        }
    }

    public void clearChattingHistory(final String account, final SessionTypeEnum sessionType) {
        CommonConfirmDialog confirmDialog = new CommonConfirmDialog(this);
        confirmDialog.setDefaultTitleTxt("您确定要清除聊天记录吗？");
        confirmDialog.setOnClickSureListener(new CommonConfirmDialog.OnClickSureListener() {
            @Override
            public void onClickSure(View view) {
                NIMClient.getService(MsgService.class).clearChattingHistory(account, sessionType);
                MessageListPanelHelper.getInstance().notifyClearMessages(account);
                ToastHelper.showToast(FlockDetailActivity.this, "清除成功");
            }
        });
        confirmDialog.show();
    }

    private void showExitHint() {
        String hintContext = "是否删除并退出该群";
        if (isSelfAdmin) {
            hintContext = "是否解散该群";
        }

        CommonConfirmDialog confirmDialog = new CommonConfirmDialog(this);
        confirmDialog.setDefaultTitleTxt(hintContext);
        confirmDialog.setOnClickSureListener(new CommonConfirmDialog.OnClickSureListener() {
            @Override
            public void onClickSure(View view) {
                if (isSelfAdmin) {
                    dismissTeam();
                } else {
                    quitTeam();
                }
            }
        });
        confirmDialog.show();
    }

    private void onGetTeamInfoFailed() {
        ToastHelper.showToast(this, getString(R.string.team_not_exist));
        finish();
    }

    /**
     * 更新群信息
     *
     * @param t
     */
    private void updateTeamInfo(final Team t) {
        this.team = t;
        if (team == null) {
            ToastHelper.showToast(this, getString(R.string.team_not_exist));
            finish();
            return;
        } else {
            creator = team.getCreator();
            if (creator.equals(NimUIKit.getAccount())) {
                isSelfAdmin = true;
            }
            setTitle(team.getName());
        }
        if (isSelfAdmin) {
            tvDelTeam.setText("解散该群");
        } else {
            tvDelTeam.setText("删除并退出");
        }
        if (t.getName() != null) {
            tvTeamName.setText(t.getName());
        }
        String nameWithoutMe = TeamHelper.getDisplayNameWithoutMe(teamId, NimUIKit.getAccount());
        tvTeamNick.setText(nameWithoutMe != null ? nameWithoutMe : "");
    }

    /**
     * *************************** 加载&变更数据源 ********************************
     */
    private void requestMembers() {
        showLoading();
        NimUIKit.getTeamProvider().fetchTeamMemberList(teamId, new SimpleCallback<List<TeamMember>>() {

            @Override
            public void onResult(boolean success, List<TeamMember> members, int code) {
                dismissLoading();
                memberAccounts.clear();
                memberBeanList.clear();
                if (success && members != null && !members.isEmpty()) {
                    mMembers = members;
                    Collections.sort(members, TeamHelper.teamMemberComparator);
                    for (TeamMember teamMember : members) {
                        TeamMemberBean memberBean = new TeamMemberBean(teamMember.getAccount(), teamMember.getTid());
                        if (teamMember.getAccount().equals(NimUIKit.getAccount())) {
                            if (teamMember.getType() == TeamMemberType.Owner) {
                                isSelfAdmin = true;
                                creator = NimUIKit.getAccount();
                            }
                        }
                        memberAccounts.add(teamMember.getAccount());
                        memberBeanList.add(memberBean);
                        if (isSelfAdmin) {
                            if (memberBeanList.size() >= 43) {
                                tvAllTeam.setVisibility(View.VISIBLE);
                                break;
                            }
                        } else {
                            if (memberBeanList.size() >= 44) {
                                tvAllTeam.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
//                        if (teamMember != null && teamMember.getAccount().equals(NimUIKit.getAccount())) {
//                            tvTeamNick.setText(teamMember.getTeamNick() != null ? teamMember.getTeamNick() : "");
//                        }
                    }
                    TeamInviteModeEnum teamInviteMode = team.getTeamInviteMode();
                    if (isSelfAdmin) {
                        TeamMemberBean add = new TeamMemberBean(true, false);
                        memberBeanList.add(add);
                        TeamMemberBean subtract = new TeamMemberBean(false, true);
                        memberBeanList.add(subtract);
                    } else {
                        if (teamInviteMode == TeamInviteModeEnum.All) {
                            TeamMemberBean add = new TeamMemberBean(true, false);
                            memberBeanList.add(add);
                        }
                    }
                    memberAdapter.notifyDataSetChanged();
                    LogUtils.debug("fetchTeamMemberList" + members.size());
                }


            }
        });
    }


    public void onAddMember() {
        ContactSelectActivity.Option option = TeamHelper.getContactSelectOption(memberAccounts);
        NimUIKit.startContactSelector(FlockDetailActivity.this, option, REQUEST_CODE_CONTACT_SELECT);
    }


    public void onRemoveMember() {

        List<String> strings = new ArrayList<>();
        strings.add(NimUIKit.getAccount());
        ContactSelectActivity.Option option = TeamHelper.getContactSelectOption(strings);
        option.type = ContactSelectActivity.ContactSelectType.TEAM_MEMBER;
        option.teamId = teamId;
        NimUIKit.startContactSelector(FlockDetailActivity.this, option, REQUEST_CODE_CONTACT_SELECT_REMOVE);
    }

    /**
     * 非群主退出群
     */
    private void quitTeam() {
        showLoading();
        NIMClient.getService(TeamService.class).quitTeam(teamId).setCallback(new RequestCallback<Void>() {

            @Override
            public void onSuccess(Void param) {
                dismissLoading();
                ToastHelper.showToast(FlockDetailActivity.this, R.string.quit_team_success);
                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_EXTRA_REASON, RESULT_EXTRA_REASON_QUIT));
                finish();
            }

            @Override
            public void onFailed(int code) {
                dismissLoading();
                ToastHelper.showToast(FlockDetailActivity.this, R.string.quit_team_failed);
            }

            @Override
            public void onException(Throwable exception) {
                dismissLoading();
            }
        });
    }

    /**
     * 群主解散群(直接退出)
     */
    private void dismissTeam() {
        showLoading();
        NIMClient.getService(TeamService.class).dismissTeam(teamId).setCallback(new RequestCallback<Void>() {

            @Override
            public void onSuccess(Void param) {
                dismissLoading();
                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_EXTRA_REASON, RESULT_EXTRA_REASON_DISMISS));
                ToastHelper.showToast(FlockDetailActivity.this, R.string.dismiss_team_success);
                finish();
            }

            @Override
            public void onFailed(int code) {
                dismissLoading();
                ToastHelper.showToast(FlockDetailActivity.this, R.string.dismiss_team_failed);
            }

            @Override
            public void onException(Throwable exception) {
                dismissLoading();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Team t = NimUIKit.getTeamProvider().getTeamById(teamId);
        updateTeamInfo(t);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        final ArrayList<String> selecteRemove = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
        if (requestCode == REQUEST_CODE_CONTACT_SELECT_REMOVE) {
            if (selecteRemove != null && !selecteRemove.isEmpty()) {
                int time = 0;
                for (String select : selecteRemove) {
                    for (int i = 0; i < memberAdapter.getData().size(); i++) {
                        String acc = memberAdapter.getData().get(i).getAccount();
                        if (select.equals(acc)) {
                            time++;
                            if (time == selecteRemove.size()) {
                                removeMember(select, i, true);
                            } else {
                                removeMember(select, i, false);
                            }
                        }
                    }
                }
                uploadImg();
            }
        } else if (requestCode == REQUEST_CODE_CONTACT_SELECT) {
            if (selecteRemove != null && !selecteRemove.isEmpty()) {
                inviteMembers(selecteRemove);
            }
        }
    }

    /**
     * 邀请群成员
     *
     * @param accounts 邀请帐号
     */
    private void inviteMembers(final ArrayList<String> accounts) {
        //        NIMClient.getService(TeamService.class).addMembers(teamId, accounts).setCallback(new RequestCallback<List<String>>() {
        NIMClient.getService(TeamService.class).addMembersEx(teamId, accounts, "邀请附言", "邀请扩展字段").setCallback(
                new RequestCallback<List<String>>() {

                    @Override
                    public void onSuccess(List<String> failedAccounts) {
                        if (failedAccounts == null || failedAccounts.isEmpty()) {
                            int size = memberAdapter.getData().size();
                            for (String select : accounts) {
//                                memberAdapter.addData(size - 2, new TeamMemberBean(select, teamId));
                                if (isSelfAdmin) {
                                    memberAdapter.addData(size - 2, new TeamMemberBean(select, teamId));
                                } else {
                                    memberAdapter.addData(size - 1, new TeamMemberBean(select, teamId));
                                }
                                memberAccounts.add(select);
                            }
                            memberAdapter.notifyDataSetChanged();
                            ToastHelper.showToast(FlockDetailActivity.this, "添加群成员成功");
                            LogUtils.debug("memberAdapter.getData().size()" + memberAdapter.getData().size());
                            uploadImg();
                        } else {
                            TeamHelper.onMemberTeamNumOverrun(failedAccounts, FlockDetailActivity.this);
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == ResponseCode.RES_TEAM_INVITE_SUCCESS) {
                            ToastHelper.showToast(FlockDetailActivity.this, R.string.team_invite_members_success);
                        } else {
                            ToastHelper.showToast(FlockDetailActivity.this, "invite members failed, code=" + code);
//                            Log.e(TAG, "invite members failed, code=" + code);
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                    }
                });
    }

    private void uploadImg() {
        int count = 11;
        if (!isSelfAdmin) {
            count = 10;
        }
        if (memberAdapter.getData().size() > count) {
            return;
        }
        CombineBitmap.init(FlockDetailActivity.this)
                .setLayoutManager(new WechatLayoutManager())
                .setSize(180)
                .setGap(3)
                .setPlaceholder(R.drawable.default_bg_hp)
                .setGapColor(Color.parseColor("#E8E8E8"))
                .setUrls(getUrls())
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(Bitmap bitmap) {
                        ivAvater.setImageBitmap(bitmap);
                        Bitmap bitmap1 = ImageViewUtils.getCacheBitmapFromView(ivAvater);
                        String path = ImageViewUtils.saveBitmapToSdCard(FlockDetailActivity.this, bitmap1, String.valueOf(System.currentTimeMillis()), false);
                        updateTeamIcon(path);
                    }
                })
                .build();
    }

    private String[] getUrls() {
        int count = memberAdapter.getData().size();
        int countSize = 11;
        if (!isSelfAdmin) {
            countSize = 10;
        }
        if (count >= countSize) {
            count = 9;
        }
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TeamMemberBean memberBean = memberAdapter.getData().get(i);
            if (memberBean.getAccount() != null && !memberBean.getAccount().equals("")) {
                final UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(memberBean.getAccount());
                String avatar = userInfo.getAvatar();
                if (StringUtil.isEmpty(avatar)) {
                    urls.add(".......");
                } else {
                    urls.add(avatar);
                }
                LogUtils.debug("userInfo.getAvatar-->" + userInfo.getAvatar());
            }

        }
        return urls.toArray(new String[urls.size()]);
    }

    /**
     * 移除群成员成功后，删除列表中的群成员
     *
     * @param account 被删除成员帐号
     * @param i
     * @param isUpload
     */
    private void removeMember(final String account, final int i, final boolean isUpload) {
        if (TextUtils.isEmpty(account)) {
            return;
        }
        NIMClient.getService(TeamService.class).removeMember(teamId, account).setCallback(new RequestCallback<Void>() {

            @Override
            public void onSuccess(Void param) {
                Iterator<TeamMemberBean> iterator = memberAdapter.getData().iterator();
                while (iterator.hasNext()) {
                    TeamMemberBean memberBean = iterator.next();
                    if (memberBean != null) {
                        if (memberBean.getAccount() != null && !memberBean.getAccount().equals("")) {
                            if (memberBean.getAccount().equals(account)) {
                                iterator.remove();
                            }
                        }
                    }
                }
                memberAccounts.remove(account);
                memberAdapter.notifyDataSetChanged();
                if (isUpload) {
                    uploadImg();
                }
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.showToastLong(FlockDetailActivity.this,
                        String.format(getString(R.string.update_failed), code));
            }

            @Override
            public void onException(Throwable exception) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    /**
     * 更新头像
     */
    private void updateTeamIcon(final String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (file == null) {
            return;
        }
        new Handler().postDelayed(outimeTask, ICON_TIME_OUT);
        uploadFuture = NIMClient.getService(NosService.class).upload(file, PickImageAction.MIME_JPEG);
        uploadFuture.setCallback(new RequestCallbackWrapper<String>() {

            @Override
            public void onResult(int code, String url, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && !TextUtils.isEmpty(url)) {
                    showLoading();
                    NIMClient.getService(TeamService.class).updateTeam(teamId, TeamFieldEnum.ICON, url).setCallback(
                            new RequestCallback<Void>() {

                                @Override
                                public void onSuccess(Void param) {
                                    dismissLoading();
//                                    ToastHelper.showToast(FlockDetailActivity.this, R.string.update_success);
                                    onUpdateDone();
                                    deleteSingleFile(path);
                                }

                                @Override
                                public void onFailed(int code) {
                                    dismissLoading();
                                    DialogMaker.dismissProgressDialog();
//                                    ToastHelper.showToast(FlockDetailActivity.this,
//                                            String.format(getString(R.string.update_failed), code));
                                }

                                @Override
                                public void onException(Throwable exception) {
                                    dismissLoading();
                                    DialogMaker.dismissProgressDialog();
                                }
                            }); // 更新资料
                } else {
                    dismissLoading();
                    ToastHelper.showToast(FlockDetailActivity.this, R.string.team_update_failed);
                    onUpdateDone();
                }
            }
        });
    }

    private Runnable outimeTask = new Runnable() {

        @Override
        public void run() {
            cancelUpload(R.string.team_update_failed);
        }
    };

    private void cancelUpload(int resId) {
        if (uploadFuture != null) {
            uploadFuture.abort();
            ToastHelper.showToast(FlockDetailActivity.this, resId);
            onUpdateDone();
        }
    }

    private void onUpdateDone() {
        uploadFuture = null;
        DialogMaker.dismissProgressDialog();
    }

    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
//                Toast.makeText(getApplicationContext(), "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
//            Toast.makeText(getApplicationContext(), "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}