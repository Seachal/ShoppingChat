package com.laka.shoppingchat.mvp.nim.team;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.laka.androidlib.util.ApplicationUtils;
import com.laka.androidlib.util.ImageViewUtils;
import com.laka.androidlib.util.LogUtils;
import com.laka.androidlib.util.screen.ScreenUtils;
import com.laka.androidlib.widget.dialog.LoadingDialog;
import com.laka.shoppingchat.R;
import com.laka.shoppingchat.mvp.main.view.activity.MainActivity;
import com.laka.shoppingchat.mvp.nim.session.SessionHelper;
import com.laka.shoppingchat.mvp.user.utils.UserUtils;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.actions.PickImageAction;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamBeInviteModeEnum;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamInviteModeEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.constant.TeamUpdateModeEnum;
import com.netease.nimlib.sdk.team.model.CreateTeamResult;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.othershe.combinebitmap.CombineBitmap;
import com.othershe.combinebitmap.layout.WechatLayoutManager;
import com.othershe.combinebitmap.listener.OnProgressListener;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzxuwen on 2015/9/25.
 */
public class TeamCreateHelper {
    private static final String TAG = TeamCreateHelper.class.getSimpleName();
    private static final int DEFAULT_TEAM_CAPACITY = 200;

    /**
     * 创建讨论组
     */
    public static void createNormalTeam(final Context context, List<String> memberAccounts, final boolean isNeedBack, final RequestCallback<CreateTeamResult> callback) {

        String teamName = "讨论组";

        DialogMaker.showProgressDialog(context, context.getString(com.netease.nim.uikit.R.string.empty), true);
        // 创建群
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<TeamFieldEnum, Serializable>();
        fields.put(TeamFieldEnum.Name, teamName);
        NIMClient.getService(TeamService.class).createTeam(fields, TeamTypeEnum.Normal, "",
                memberAccounts).setCallback(
                new RequestCallback<CreateTeamResult>() {
                    @Override
                    public void onSuccess(CreateTeamResult result) {
                        DialogMaker.dismissProgressDialog();

                        ArrayList<String> failedAccounts = result.getFailedInviteAccounts();
                        if (failedAccounts != null && !failedAccounts.isEmpty()) {
                            TeamHelper.onMemberTeamNumOverrun(failedAccounts, context);
                        } else {
                            ToastHelper.showToast(ApplicationUtils.getContext(), com.netease.nim.uikit.R.string.create_team_success);
                        }

                        if (isNeedBack) {
                            // SessionHelper.startTeamSession(context, result.getTeam().getId(), HomeActivity.class, null); // 进入创建的群
                            SessionHelper.startTeamSession(context, result.getTeam().getId(), MainActivity.class, null); // 进入创建的群
                        } else {
                            SessionHelper.startTeamSession(context, result.getTeam().getId());
                        }
                        if (callback != null) {
                            callback.onSuccess(result);
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        DialogMaker.dismissProgressDialog();
                        if (code == ResponseCode.RES_TEAM_ECOUNT_LIMIT) {
                            String tip = context.getString(com.netease.nim.uikit.R.string.over_team_member_capacity, DEFAULT_TEAM_CAPACITY);
                            ToastHelper.showToast(ApplicationUtils.getContext(), tip);
                        } else {
                            ToastHelper.showToast(ApplicationUtils.getContext(), com.netease.nim.uikit.R.string.create_team_failed);
                        }

                        Log.e(TAG, "create team error: " + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                }
        );
    }

    /**
     * 创建高级群
     */
    public static void createAdvancedTeam(final Context context, final List<String> memberAccounts) {

        String teamName = "群聊";

//        DialogMaker.showProgressDialog(context, context.getString(com.netease.nim.uikit.R.string.empty), true);
        // 创建群
        TeamTypeEnum type = TeamTypeEnum.Advanced;
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<>();
        fields.put(TeamFieldEnum.Name, teamName);
        fields.put(TeamFieldEnum.BeInviteMode, TeamBeInviteModeEnum.NoAuth);
        fields.put(TeamFieldEnum.InviteMode, TeamInviteModeEnum.All);
        fields.put(TeamFieldEnum.TeamUpdateMode, TeamUpdateModeEnum.All);

        final LoadingDialog mLoadingDialog = new LoadingDialog(context);
        mLoadingDialog.show();

        NIMClient.getService(TeamService.class).createTeam(fields, type, "",
                memberAccounts).setCallback(
                new RequestCallback<CreateTeamResult>() {
                    @Override
                    public void onSuccess(CreateTeamResult result) {
                        mLoadingDialog.dismiss();
                        Log.i(TAG, "create team success, team id =" + result.getTeam().getId() + ", now begin to update property...");
                        memberAccounts.add(0, UserUtils.getImAccount());
                        createAvater(context, result.getTeam().getId(), memberAccounts);
                        onCreateSuccess(context, result);
                    }

                    @Override
                    public void onFailed(int code) {
                        mLoadingDialog.dismiss();
                        String tip;
                        if (code == ResponseCode.RES_TEAM_ECOUNT_LIMIT) {
                            tip = context.getString(com.netease.nim.uikit.R.string.over_team_member_capacity,
                                    DEFAULT_TEAM_CAPACITY);
                        } else if (code == ResponseCode.RES_TEAM_LIMIT) {
                            tip = context.getString(com.netease.nim.uikit.R.string.over_team_capacity);
                        } else {
                            tip = context.getString(com.netease.nim.uikit.R.string.create_team_failed) + ", code=" + code;
                        }

                        ToastHelper.showToast(context, tip);

                        Log.e(TAG, "create team error: " + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        mLoadingDialog.dismiss();
                    }
                }
        );
    }

    /**
     * 群创建成功回调
     */
    private static void onCreateSuccess(final Context context, CreateTeamResult result) {
        if (result == null) {
            Log.e(TAG, "onCreateSuccess exception: team is null");
            return;
        }
        final Team team = result.getTeam();
        if (team == null) {
            Log.e(TAG, "onCreateSuccess exception: team is null");
            return;
        }

        Log.i(TAG, "create and update team success");

        DialogMaker.dismissProgressDialog();
        // 检查有没有邀请失败的成员
        ArrayList<String> failedAccounts = result.getFailedInviteAccounts();
        if (failedAccounts != null && !failedAccounts.isEmpty()) {
            TeamHelper.onMemberTeamNumOverrun(failedAccounts, context);
        } else {
            ToastHelper.showToast(ApplicationUtils.getContext(), com.netease.nim.uikit.R.string.create_team_success);
        }

        // 演示：向群里插入一条Tip消息，使得该群能立即出现在最近联系人列表（会话列表）中，满足部分开发者需求
//        Map<String, Object> content = new HashMap<>(1);
//        content.put("content", "成功创建高级群");
//        IMMessage msg = MessageBuilder.createTipMessage(team.getId(), SessionTypeEnum.Team);
//        msg.setRemoteExtension(content);
//        CustomMessageConfig config = new CustomMessageConfig();
//        config.enableUnreadCount = false;
//        msg.setConfig(config);
//        msg.setStatus(MsgStatusEnum.success);
//        NIMClient.getService(MsgService.class).saveMessageToLocal(msg, true);

        // 发送后，稍作延时后跳转
        new Handler(context.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionHelper.startTeamSession(context, team.getId()); // 进入创建的群
            }
        }, 50);
    }

    public static void createAvater(final Context context, final String tId, final List<String> url) {

        if (url == null && url.size() == 0 && tId == null && tId.equals("")) {
            return;
        }
        int count = url.size();
        if (count >= 9) {
            count = 9;
        }
        LogUtils.debug("onComplete url" + url.size());
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String account = url.get(i);
            if (!StringUtil.isEmpty(account)) {
                final UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(account);
                String avatarUrl = userInfo.getAvatar();
                if (!StringUtil.isEmpty(avatarUrl)) {
                    urls.add(avatarUrl);
                    LogUtils.debug("userInfo.getAvatar-->" + userInfo.getAvatar());
                }else{
                    //url为空时，随意添加字符串，防止内部空指针异常
                    urls.add("----");
                }
            }
        }
        final View view = LayoutInflater.from(context).inflate(R.layout.item_create_avater, null, false);
        final ImageView ivAvater = view.findViewById(R.id.iv_avater);
        LogUtils.debug("onComplete urls" + urls.size());
        CombineBitmap.init(context)
                .setLayoutManager(new WechatLayoutManager())
                .setSize(180)
                .setGap(3)
                .setPlaceholder(R.drawable.default_bg_hp)
                .setGapColor(Color.parseColor("#E8E8E8"))
                .setUrls(urls.toArray(new String[urls.size()]))
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(Bitmap bitmap) {
                        if (bitmap == null) {
                            return;
                        }
                        LogUtils.debug("onComplete bitmap" + bitmap.toString());
                        ivAvater.setImageBitmap(bitmap);
                        int measuredWidth = View.MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(), View.MeasureSpec.EXACTLY);
                        int measuredHeight = View.MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(), View.MeasureSpec.EXACTLY);
                        view.measure(measuredWidth, measuredHeight);
                        //调用layout方法布局后，可以得到view的尺寸大小
                        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                        Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(bmp);
                        view.draw(c);


                        LogUtils.debug("onComplete" + bmp.toString());
                        String path = ImageViewUtils.saveBitmapToSdCard(context, bmp, String.valueOf(System.currentTimeMillis()), false);
                        updateTeamIcon(context, tId, path);
                    }
                })
                .build();
    }


    /**
     * 更新头像
     */
    private static void updateTeamIcon(final Context context, final String teamId, final String path) {
        final AbortableFuture<String> uploadFuture;
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (file == null) {
            return;
        }
        uploadFuture = NIMClient.getService(NosService.class).upload(file, PickImageAction.MIME_JPEG);
        uploadFuture.setCallback(new RequestCallbackWrapper<String>() {

            @Override
            public void onResult(int code, String url, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && !TextUtils.isEmpty(url)) {
                    LogUtil.i("HHH", "upload icon success, url =" + url);
                    NIMClient.getService(TeamService.class).updateTeam(teamId, TeamFieldEnum.ICON, url).setCallback(
                            new RequestCallback<Void>() {

                                @Override
                                public void onSuccess(Void param) {
                                    DialogMaker.dismissProgressDialog();
                                    ToastHelper.showToast(context, com.netease.nim.uikit.R.string.update_success);
                                    onUpdateDone(uploadFuture);
                                    deleteSingleFile(path);
                                }

                                @Override
                                public void onFailed(int code) {
                                    DialogMaker.dismissProgressDialog();
                                    ToastHelper.showToast(context,
                                            String.format(context.getString(com.netease.nim.uikit.R.string.update_failed), code));
                                }

                                @Override
                                public void onException(Throwable exception) {
                                    DialogMaker.dismissProgressDialog();
                                }
                            }); // 更新资料
                } else {
                    ToastHelper.showToast(context, com.netease.nim.uikit.R.string.team_update_failed);
                    onUpdateDone(uploadFuture);
                }
            }
        });
    }

    private static boolean deleteSingleFile(String filePath$Name) {
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

    private static void onUpdateDone(AbortableFuture<String> uploadFuture) {
        uploadFuture = null;
        DialogMaker.dismissProgressDialog();
    }

    public static List<String> requestMembers(String teamId) {
        List<TeamMember> members = NimUIKit.getTeamProvider().getTeamMemberList(teamId);
        List<String> teamMemberBeans = new ArrayList<>();
        if (members != null && !members.isEmpty()) {
            Collections.sort(members, TeamHelper.teamMemberComparator);
            for (TeamMember teamMember : members) {
                teamMemberBeans.add(teamMember.getAccount());
            }
        }
        return teamMemberBeans;
    }

}
