package com.netease.nim.uikit.business.session.fragment;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laka.androidlib.util.LogUtils;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.api.model.main.CustomPushContentProvider;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.business.ait.AitManager;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.session.actions.ImageAction;
import com.netease.nim.uikit.business.session.actions.RedPacketAction;
import com.netease.nim.uikit.business.session.actions.VideoAction;
import com.netease.nim.uikit.business.session.attachment.CustomAttachParser;
import com.netease.nim.uikit.business.session.attachment.CustomAttachmentType;
import com.netease.nim.uikit.business.session.constant.Extras;
import com.netease.nim.uikit.business.session.module.Container;
import com.netease.nim.uikit.business.session.module.ModuleProxy;
import com.netease.nim.uikit.business.session.module.input.InputPanel;
import com.netease.nim.uikit.business.session.module.list.MessageListPanelEx;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MemberPushOption;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.robot.model.NimRobotInfo;
import com.netease.nimlib.sdk.robot.model.RobotAttachment;
import com.netease.nimlib.sdk.robot.model.RobotMsgType;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.TeamMember;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author:summer
 * @Date:2019/9/2
 * @Description:聊天界面基类（单聊聊天界面）
 */
public class MessageFragment extends TFragment implements ModuleProxy {

    private View rootView;
    //聊天界面定制化参数
    private SessionCustomization customization;

    protected static final String TAG = "MessageActivity";

    // p2p对方Account或者群id
    protected String sessionId;

    protected SessionTypeEnum sessionType;

    //输入面板与聊天消息列表
    protected InputPanel inputPanel;
    protected MessageListPanelEx messageListPanel;
    // @ 管理器
    protected AitManager aitManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parseIntent();
        messageListPanel.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nim_message_fragment, container, false);
        return rootView;
    }


    /**
     * ***************************** life cycle *******************************
     */

    @Override
    public void onPause() {
        super.onPause();

        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        inputPanel.onPause();
        messageListPanel.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        messageListPanel.onResume();
        String showName = TeamHelper.getShowName(sessionId);
        NIMClient.getService(MsgService.class).setChattingAccount(sessionId, sessionType);
        getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL); // 默认使用听筒播放
        showNameState(showName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageListPanel.onDestroy();
        registerObservers(false);
        if (inputPanel != null) {
            inputPanel.onDestroy();
        }
        if (aitManager != null) {
            aitManager.reset();
        }
    }

    public boolean onBackPressed() {
        return inputPanel.collapse(true) || messageListPanel.onBackPressed();
    }

    public void refreshMessageList() {
        messageListPanel.refreshMessageList();
    }

    private void parseIntent() {
        Bundle arguments = getArguments();
        sessionId = arguments.getString(Extras.EXTRA_ACCOUNT);
        sessionType = (SessionTypeEnum) arguments.getSerializable(Extras.EXTRA_TYPE);
        IMMessage anchor = (IMMessage) arguments.getSerializable(Extras.EXTRA_ANCHOR);

        customization = (SessionCustomization) arguments.getSerializable(Extras.EXTRA_CUSTOMIZATION);
        Container container = new Container(getActivity(), sessionId, sessionType, this, true);

        if (messageListPanel == null) {
            messageListPanel = new MessageListPanelEx(container, rootView, anchor, false, false);
            if (sessionType == SessionTypeEnum.Team) {
                String showName = TeamHelper.getShowName(sessionId);
                if (TextUtils.isEmpty(showName)) {
                    NimUIKit.getTeamProvider().fetchTeamMemberList(sessionId, new SimpleCallback<List<TeamMember>>() {
                        @Override
                        public void onResult(boolean success, List<TeamMember> result, int code) {
                            if (result != null && result.size() >= 20) {
                                messageListPanel.setShowName(true);
                                TeamHelper.setShowName(sessionId, TeamHelper.OPEN_SHOW_NAME);
                            } else {
                                messageListPanel.setShowName(false);
                                TeamHelper.setShowName(sessionId, TeamHelper.CLOSE_SHOW_NAME);
                            }
                        }
                    });
                } else {
                    showNameState(showName);
                }
            }
            messageListPanel.setAvatarLongClickedListener(new MessageListPanelEx.AvatarLongClickedListener() {
                @Override
                public void onAvatarLongClicked(View view, IMMessage message) {
                    NIMClient.getService(TeamService.class).queryTeamMember(sessionId, message.getFromAccount()).setCallback(new RequestCallbackWrapper<TeamMember>() {
                        @Override
                        public void onResult(int code, TeamMember member, Throwable exception) {
                            if (SessionTypeEnum.Team == sessionType && member != null) {
                                aitManager.insertAitMemberInner(member);
                            } else {
                                LogUtils.info("非群组聊天会话或者获取群组信息为null");
                            }
                        }
                    });

                }
            });
        } else {
            messageListPanel.reload(container, anchor);
        }

        if (inputPanel == null) {
            inputPanel = new InputPanel(container, rootView, getActionList());
            inputPanel.setCustomization(customization);
        } else {
            inputPanel.reload(container, customization);
        }

        initAitManager();

        inputPanel.switchRobotMode(NimUIKitImpl.getRobotInfoProvider().getRobotByAccount(sessionId) != null);

        registerObservers(true);

        if (customization != null) {
            messageListPanel.setChattingBackground(customization.backgroundUri, customization.backgroundColor);
        }
    }

    private void showNameState(String showName) {
        if (showName.equals(TeamHelper.CLOSE_SHOW_NAME)) {
            messageListPanel.setShowName(false);
        } else {
            messageListPanel.setShowName(true);
        }
    }

    protected String getNameText(IMMessage message) {
        if (message.getSessionType() == SessionTypeEnum.Team) {
            return TeamHelper.getTeamMemberDisplayName(message.getSessionId(), message.getFromAccount());
        }
        return "";
    }

    private void initAitManager() {
        UIKitOptions options = NimUIKitImpl.getOptions();
        if (options.aitEnable) {
            aitManager = new AitManager(getContext(), options.aitTeamMember && sessionType == SessionTypeEnum.Team ? sessionId : null, options.aitIMRobot);
            inputPanel.addAitTextWatcher(aitManager);
            aitManager.setTextChangeListener(inputPanel);
        }
    }

    /**
     * ************************* 消息收发 **********************************
     */
    /**
     * 是否允许发送消息
     */
    protected boolean isAllowSendMessage(final IMMessage message) {

        if (sessionType == SessionTypeEnum.P2P) {
            return NimUIKit.getContactProvider().isMyFriend(sessionId);
        }
        return customization.isAllowSendMessage(message);
    }


    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
        // 已读回执监听
        if (NimUIKitImpl.getOptions().shouldHandleReceipt) {
            //不需要已读回执
            //service.observeMessageReceipt(messageReceiptObserver, register);
        }
    }

    /**
     * 消息接收观察者
     */
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            onMessageIncoming(messages);
        }
    };

    private void onMessageIncoming(List<IMMessage> messages) {
        if (CommonUtil.isEmpty(messages)) {
            //消息过滤器中把拆红包tip删除了，但是由于会话监听器没能正确删除，
            // 所以数据库中已经存放了相应的消息，重新加载消息还是会出现
            return;
        }

        //todo 在会话列表也可以过滤相应会话了，但是目前从会话列表点击进入聊天页面后，还是会出现不符合的消息，不知道是什么时候入库还是其他原因
        //todo 原因：应该是监听中的列表是在之前的监听器中已经把相应消息移除了，但是数据库中的却没有移除成功，所以解决方法是在进入聊天列表的时候，在加载聊天记录列表中
        //todo 将不需要的拆红包消息过滤掉，如果过滤掉后的消息数量太小，可以再次加载再刷新列表
        //removeRedPackageTipMessage(messages);

        //更新消息列表数据
        messageListPanel.onIncomingMessage(messages);
        // 发送已读回执
        messageListPanel.sendReceipt();
    }

    /**
     * 删除不是本人发也不是本人收的抢红包tip消息
     */
    private void removeRedPackageTipMessage(List<IMMessage> messages) {
        //判断是否是抢红包通知
        for (int i = 0; i < messages.size(); i++) {
            IMMessage message = messages.get(i);
            if (message.getMsgType() == MsgTypeEnum.custom) {
                try {
                    JSONObject json = new JSONObject(message.getAttachStr());
                    int type = json.getInt(CustomAttachParser.KEY_TYPE);
                    if (type == CustomAttachmentType.robRedPackage) { //抢红包通知
                        JSONObject data = json.getJSONObject(CustomAttachParser.KEY_DATA);
                        String userId = data.getString("user_id");
                        String opeUserId = data.getString("op_user");
                        if (!NimUIKitImpl.getAccount().equals(userId)
                                && !NimUIKitImpl.getAccount().equals(opeUserId)) {
                            messages.remove(message);
                            NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                            i--;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 已读回执观察者
     */
    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            messageListPanel.receiveReceipt();
        }
    };


    /**
     * ********************** implements ModuleProxy *********************
     */
    @Override
    public boolean sendMessage(IMMessage message) {
        if (isAllowSendMessage(message)) {
            appendTeamMemberPush(message);
            message = changeToRobotMsg(message);
            final IMMessage msg = message;
            appendPushConfig(message);
            Map<String, Object> remoteExtension = message.getRemoteExtension();
            boolean forward = false;
            if (remoteExtension != null && remoteExtension.containsKey("forward")) {
                forward = (boolean) remoteExtension.get("forward");
            }
            //将消息发送到服务器并保存到数据库
            final boolean finalForward = forward;
            NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    if (finalForward) {
                        ToastHelper.showToast(getContext(), "转发成功");
                    }

                    LogUtils.info("发送成功");
                }

                @Override
                public void onFailed(int code) {
                    sendFailWithBlackList(code, msg);
                    if (finalForward) {
                        ToastHelper.showToast(getContext(), "转发失败");
                    }
                }

                @Override
                public void onException(Throwable exception) {
                    exception.printStackTrace();
                    if (finalForward) {
                        ToastHelper.showToast(getContext(), "转发异常");
                    }
                }
            });

        } else {
            // 当设置该用户不允许发送消息时，则替换成tip，可以以此来实现禁言
            message = MessageBuilder.createTipMessage(message.getSessionId(), message.getSessionType());

            if (sessionType == SessionTypeEnum.P2P) {
                message.setContent("对方不是你的好友，请添加好友");
            } else {
                message.setContent("该消息无法发送");
            }

            message.setStatus(MsgStatusEnum.success);
            NIMClient.getService(MsgService.class).saveMessageToLocal(message, false);
        }
        //更新聊天列表
        messageListPanel.onMsgSend(message);
        if (aitManager != null) {
            aitManager.reset();
        }
        return true;
    }

    /**
     * 被对方拉入黑名单后，发消息失败的交互处理
     */
    private void sendFailWithBlackList(int code, IMMessage msg) {
        if (code == ResponseCode.RES_IN_BLACK_LIST) {
            // 如果被对方拉入黑名单，发送的消息前不显示重发红点
            msg.setStatus(MsgStatusEnum.success);
            NIMClient.getService(MsgService.class).updateIMMessageStatus(msg);
            messageListPanel.refreshMessageList();
            // 同时，本地插入被对方拒收的tip消息
            IMMessage tip = MessageBuilder.createTipMessage(msg.getSessionId(), msg.getSessionType());
            tip.setContent(getActivity().getString(R.string.black_list_send_tip));
            tip.setStatus(MsgStatusEnum.success);
            CustomMessageConfig config = new CustomMessageConfig();
            config.enableUnreadCount = false;
            tip.setConfig(config);
            NIMClient.getService(MsgService.class).saveMessageToLocal(tip, true);
        }
    }

    /**
     * 如果是群聊，则添加群成员消息推送
     */
    private void appendTeamMemberPush(IMMessage message) {
        if (aitManager == null) {
            return;
        }
        if (sessionType == SessionTypeEnum.Team) {
            List<String> pushList = aitManager.getAitTeamMember();
            if (pushList == null || pushList.isEmpty()) {
                return;
            }
            MemberPushOption memberPushOption = new MemberPushOption();
            memberPushOption.setForcePush(true);
            memberPushOption.setForcePushContent(message.getContent());
            memberPushOption.setForcePushList(pushList);
            message.setMemberPushOption(memberPushOption);
        }
    }

    /**
     * 机器人消息？自动回复？
     */
    private IMMessage changeToRobotMsg(IMMessage message) {
        if (aitManager == null) {
            return message;
        }
        if (message.getMsgType() == MsgTypeEnum.robot) {
            return message;
        }
        if (isChatWithRobot()) {
            if (message.getMsgType() == MsgTypeEnum.text && message.getContent() != null) {
                String content = message.getContent().equals("") ? " " : message.getContent();
                message = MessageBuilder.createRobotMessage(message.getSessionId(), message.getSessionType(), message.getSessionId(), content, RobotMsgType.TEXT, content, null, null);
            }
        } else {
            String robotAccount = aitManager.getAitRobot();
            if (TextUtils.isEmpty(robotAccount)) {
                return message;
            }
            String text = message.getContent();
            String content = aitManager.removeRobotAitString(text, robotAccount);
            content = content.equals("") ? " " : content;
            message = MessageBuilder.createRobotMessage(message.getSessionId(), message.getSessionType(), robotAccount, text, RobotMsgType.TEXT, content, null, null);
        }
        return message;
    }

    private boolean isChatWithRobot() {
        return NimUIKitImpl.getRobotInfoProvider().getRobotByAccount(sessionId) != null;
    }

    /**
     * 追加推送配置
     */
    private void appendPushConfig(IMMessage message) {
        CustomPushContentProvider customConfig = NimUIKitImpl.getCustomPushContentProvider();
        if (customConfig == null) {
            return;
        }
        String content = customConfig.getPushContent(message);
        Map<String, Object> payload = customConfig.getPushPayload(message);
        if (!TextUtils.isEmpty(content)) {
            message.setPushContent(content);
        }
        if (payload != null) {
            message.setPushPayload(payload);
        }
    }

    @Override
    public void onInputPanelExpand() {
        messageListPanel.scrollToBottom();
    }

    @Override
    public void shouldCollapseInputPanel() {
        inputPanel.collapse(false);
    }

    @Override
    public boolean isLongClickEnabled() {
        return !inputPanel.isRecording();
    }

    @Override
    public void onItemFooterClick(IMMessage message) {
        if (aitManager == null) {
            return;
        }
        if (messageListPanel.isSessionMode()) {
            RobotAttachment attachment = (RobotAttachment) message.getAttachment();
            NimRobotInfo robot = NimUIKitImpl.getRobotInfoProvider().getRobotByAccount(attachment.getFromRobotAccount());
            aitManager.insertAitRobot(robot.getAccount(), robot.getName(), inputPanel.getEditSelectionStart());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (aitManager != null) {
            aitManager.onActivityResult(requestCode, resultCode, data);
        }
        inputPanel.onActivityResult(requestCode, resultCode, data);
        messageListPanel.onActivityResult(requestCode, resultCode, data);
    }

    // 操作面板集合
    protected List<BaseAction> getActionList() {
        List<BaseAction> actions = new ArrayList<>();
        actions.add(new ImageAction());
        actions.add(new VideoAction());
        actions.add(new RedPacketAction());
        /*if (customization != null && customization.actions != null) {
            actions.addAll(customization.actions);
        }*/
        return actions;
    }

}
