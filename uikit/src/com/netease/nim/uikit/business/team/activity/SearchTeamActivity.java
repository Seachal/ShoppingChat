package com.netease.nim.uikit.business.team.activity;

import android.app.Activity;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.laka.androidlib.base.activity.BaseActivity;
import com.laka.androidlib.util.ImageViewUtils;
import com.laka.androidlib.util.LogUtils;
import com.laka.androidlib.util.StatusBarUtil;
import com.laka.androidlib.widget.SpaceItemDecoration;
import com.laka.androidlib.widget.dialog.CommonConfirmDialog;
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
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
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
public class SearchTeamActivity extends BaseActivity {

    private static final int REQUEST_CODE_TRANSFER = 101;

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
    private ImageView ivAvater;
    private EditText mEtSearch;
    private String teamId;
    private SessionCustomization customization;
    private TeamMemberCustomAdapter memberAdapter;
    private Team team;
    // state
    private boolean isSelfAdmin = false;
    private String creator;
    private List<TeamMemberBean> memberBeanList = new ArrayList<>();
    private List<TeamMemberBean> searchMemberList = new ArrayList<>();
    private List<String> memberAccounts = new ArrayList<>();
    private AbortableFuture<String> uploadFuture;
    private static final int ICON_TIME_OUT = 30000;

    public static void start(Context context, String tid, SessionCustomization customization) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, tid);
        intent.putExtra(EXTRA_CUSTOMIZATION, customization);
        intent.setClass(context, SearchTeamActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int setContentView() {
        return R.layout.activity_search_team;
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
        ivAvater = findViewById(R.id.iv_avater);
        mEtSearch = findViewById(R.id.et_search);

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
    }

    private void initRlAdpter() {
        memberAdapter = new TeamMemberCustomAdapter(memberBeanList);
        memberAdapter.setHasStableIds(true);
        rlList.addItemDecoration(new SpaceItemDecoration(mContext).setSpace(20).setSpaceColor(-0x131314));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        rlList.setLayoutManager(gridLayoutManager);
//        gridLayoutManager.setSmoothScrollbarEnabled(true);
//        gridLayoutManager.setAutoMeasureEnabled(true);
//        rlList.setHasFixedSize(true);
//        rlList.setNestedScrollingEnabled(false);
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
                    if (memberBean.getAccount().equals(NimUIKit.getAccount())) {
                        return;
                    }
                    if (customization != null) {
                        customization.buttons.get(0).onClick(SearchTeamActivity.this, view, memberBean.getAccount());
                    }
                }
            }
        });
        rlList.setAdapter(memberAdapter);
        rlList.setLayoutManager(new GridLayoutManager(this, 5));
        loadTeamInfo();
        requestMembers();
    }

    @Override
    protected void initEvent() {
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.equals("")) {
                    requestMembers();
                } else {
                    searchMemberList.clear();
                    for (TeamMemberBean memberBean : memberBeanList) {
                        if (memberBean.getAccount() == null) continue;
                        String name = TeamHelper.getTeamMemberDisplayName(teamId, memberBean.getAccount());
                        if (memberBean.getAccount().contains(text)) {
                            searchMemberList.add(memberBean);
                        } else if (name.contains(text)) {
                            searchMemberList.add(memberBean);
                        }
                    }
                    memberAdapter.getData().clear();
                    memberAdapter.addData(searchMemberList);
                    memberAdapter.notifyDataSetChanged();
                }
            }
        });
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

    }

    /**
     * *************************** 加载&变更数据源 ********************************
     */
    private void requestMembers() {
        NimUIKit.getTeamProvider().fetchTeamMemberList(teamId, new SimpleCallback<List<TeamMember>>() {

            @Override
            public void onResult(boolean success, List<TeamMember> members, int code) {
                memberAccounts.clear();
                memberBeanList.clear();
                if (success && members != null && !members.isEmpty()) {
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
                    }
                    titleBar.setTitle("聊天成员(" + memberBeanList.size() + ")");
                    TeamMemberBean add = new TeamMemberBean(true, false);
                    memberBeanList.add(add);
                    if (isSelfAdmin) {
                        TeamMemberBean subtract = new TeamMemberBean(false, true);
                        memberBeanList.add(subtract);
                    }
                    memberAdapter.notifyDataSetChanged();
                    LogUtils.debug("fetchTeamMemberList" + members.size());
                }
            }
        });
    }


    public void onAddMember() {
        ContactSelectActivity.Option option = TeamHelper.getContactSelectOption(memberAccounts);
        NimUIKit.startContactSelector(SearchTeamActivity.this, option, REQUEST_CODE_CONTACT_SELECT);
    }

    public void onRemoveMember() {

        List<String> strings = new ArrayList<>();
        strings.add(NimUIKit.getAccount());
        ContactSelectActivity.Option option = TeamHelper.getContactSelectOption(strings);
        option.type = ContactSelectActivity.ContactSelectType.TEAM_MEMBER;
        option.teamId = teamId;
        NimUIKit.startContactSelector(SearchTeamActivity.this, option, REQUEST_CODE_CONTACT_SELECT_REMOVE);
    }

    /**
     * 非群主退出群
     */
    private void quitTeam() {
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        NIMClient.getService(TeamService.class).quitTeam(teamId).setCallback(new RequestCallback<Void>() {

            @Override
            public void onSuccess(Void param) {
                DialogMaker.dismissProgressDialog();
                ToastHelper.showToast(SearchTeamActivity.this, R.string.quit_team_success);
                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_EXTRA_REASON, RESULT_EXTRA_REASON_QUIT));
                finish();
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog();
                ToastHelper.showToast(SearchTeamActivity.this, R.string.quit_team_failed);
            }

            @Override
            public void onException(Throwable exception) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    /**
     * 群主解散群(直接退出)
     */
    private void dismissTeam() {
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        NIMClient.getService(TeamService.class).dismissTeam(teamId).setCallback(new RequestCallback<Void>() {

            @Override
            public void onSuccess(Void param) {
                DialogMaker.dismissProgressDialog();
                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_EXTRA_REASON, RESULT_EXTRA_REASON_DISMISS));
                ToastHelper.showToast(SearchTeamActivity.this, R.string.dismiss_team_success);
                finish();
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog();
                ToastHelper.showToast(SearchTeamActivity.this, R.string.dismiss_team_failed);
            }

            @Override
            public void onException(Throwable exception) {
                DialogMaker.dismissProgressDialog();
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
                            ToastHelper.showToast(SearchTeamActivity.this, "添加群成员成功");
                            LogUtils.debug("memberAdapter.getData().size()" + memberAdapter.getData().size());
                            uploadImg();
                        } else {
                            TeamHelper.onMemberTeamNumOverrun(failedAccounts, SearchTeamActivity.this);
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == ResponseCode.RES_TEAM_INVITE_SUCCESS) {
                            ToastHelper.showToast(SearchTeamActivity.this, R.string.team_invite_members_success);
                        } else {
                            ToastHelper.showToast(SearchTeamActivity.this, "invite members failed, code=" + code);
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
        CombineBitmap.init(SearchTeamActivity.this)
                .setLayoutManager(new WechatLayoutManager())
                .setSize(180)
                .setGap(3)
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
                        String path = ImageViewUtils.saveBitmapToSdCard(SearchTeamActivity.this, bitmap1, String.valueOf(System.currentTimeMillis()), false);
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
                urls.add(userInfo.getAvatar());
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
     * @param b
     */
    private void removeMember(final String account, final int i, final boolean b) {
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
                if (b) {
                    uploadImg();
                }
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.showToastLong(SearchTeamActivity.this,
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
                    NIMClient.getService(TeamService.class).updateTeam(teamId, TeamFieldEnum.ICON, url).setCallback(
                            new RequestCallback<Void>() {

                                @Override
                                public void onSuccess(Void param) {
                                    DialogMaker.dismissProgressDialog();
//                                    ToastHelper.showToast(SearchTeamActivity.this, R.string.update_success);
                                    onUpdateDone();
                                    deleteSingleFile(path);
                                }

                                @Override
                                public void onFailed(int code) {
                                    DialogMaker.dismissProgressDialog();
//                                    ToastHelper.showToast(SearchTeamActivity.this,
//                                            String.format(getString(R.string.update_failed), code));
                                }

                                @Override
                                public void onException(Throwable exception) {
                                    DialogMaker.dismissProgressDialog();
                                }
                            }); // 更新资料
                } else {
                    ToastHelper.showToast(SearchTeamActivity.this, R.string.team_update_failed);
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
            ToastHelper.showToast(SearchTeamActivity.this, resId);
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