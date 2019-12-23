package com.netease.nim.uikit.business.recent;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laka.androidlib.eventbus.Event;
import com.laka.androidlib.eventbus.EventBusManager;
import com.laka.androidlib.util.ListUtils;
import com.laka.androidlib.util.SPHelper;
import com.laka.androidlib.util.StringUtils;
import com.laka.androidlib.widget.dialog.JAlertDialog;
import com.laka.androidlib.widget.dialog.OnJAlertDialogClickListener;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.contact.ContactChangedObserver;
import com.netease.nim.uikit.api.model.main.OnlineStateChangeObserver;
import com.netease.nim.uikit.api.model.team.TeamDataChangedObserver;
import com.netease.nim.uikit.api.model.team.TeamMemberDataChangedObserver;
import com.netease.nim.uikit.api.model.user.UserInfoObserver;
import com.netease.nim.uikit.business.recent.adapter.RecentContactAdapter;
import com.netease.nim.uikit.business.session.attachment.RedPackageAttachment;
import com.netease.nim.uikit.business.session.attachment.RobRedPackageAttachment;
import com.netease.nim.uikit.business.session.constant.SessionConstant;
import com.netease.nim.uikit.business.session.model.event.DraftEvent;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.badger.Badger;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.drop.DropCover;
import com.netease.nim.uikit.common.ui.drop.DropManager;
import com.netease.nim.uikit.common.ui.recyclerview.listener.SimpleClickListener;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 最近联系人列表(会话列表)
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class RecentContactsFragment extends TFragment {

    // 置顶功能可直接使用，也可作为思路，供开发者充分利用RecentContact的tag字段
    public static final long RECENT_TAG_STICKY = 0x0000000000000001; // 联系人置顶tag

    // itemView
    private RecyclerView recyclerView;

    private View emptyBg;

    private TextView emptyHint;

    // data
    private List<RecentContact> items;

    private Map<String, RecentContact> cached; // 暂缓刷上列表的数据（未读数红点拖拽动画运行时用）

    private RecentContactAdapter adapter;

    private boolean msgLoaded = false;

    private RecentContactsCallback callback;

    private UserInfoObserver userInfoObserver;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        initMessageList();
        requestMessages(true);
        registerObservers(true);
        registerDropCompletedListener(true);
        registerOnlineStateChangeListener(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nim_recent_contacts, container, false);
    }

    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
        boolean empty = items.isEmpty() && msgLoaded;
        emptyBg.setVisibility(empty ? View.VISIBLE : View.GONE);
        emptyHint.setHint("还没有会话，在通讯录中找个人聊聊吧！");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
        registerDropCompletedListener(false);
        registerOnlineStateChangeListener(false);
        DropManager.getInstance().setDropListener(null);
    }

    /**
     * 查找页面控件
     */
    private void findViews() {
        recyclerView = findView(R.id.recycler_view);
        emptyBg = findView(R.id.emptyBg);
        emptyHint = findView(R.id.message_list_empty_hint);
    }

    public void refreshList(DraftEvent draftEvent) {
        //设置扩展信息
        for (int i = 0; i < items.size(); i++) {
            RecentContact recentContact = items.get(i);
            if (draftEvent.account.equals(recentContact.getContactId())) {
                Map<String, Object> extension = recentContact.getExtension();
                if (extension == null) extension = new HashMap<>();
                if (!StringUtils.isEmpty(draftEvent.content)) {
                    extension.put(SessionConstant.RECENT_KEY_DRAFT_TIME, draftEvent.time);
                    extension.put(SessionConstant.RECENT_KEY_DRAFT, draftEvent.content);
                } else {
                    extension.remove(SessionConstant.RECENT_KEY_DRAFT);
                    if (recentContact.getTime() > draftEvent.time) {
                        extension.remove(SessionConstant.RECENT_KEY_DRAFT_TIME);
                        SPHelper.removeDraftString(recentContact.getContactId());
                    }
                }
                recentContact.setExtension(extension);
            }
        }

        //排序
        if (items.size() == 0) {
            return;
        }
        Collections.sort(items, comp);

        //更新列表
        notifyDataSetChanged();
        int unreadNum = 0;
        for (RecentContact r : items) {
            unreadNum += r.getUnreadCount();
        }
        if (callback != null) {
            callback.onUnreadCountChange(unreadNum);
        }
        Badger.updateBadgerCount(unreadNum);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(Event event) {
        if (event.getName() == SessionConstant.SESSION_PAGE_CLOSE_RESULT_EVENT) {
            DraftEvent draftEvent = (DraftEvent) event.getData();
            refreshList(draftEvent);
        }
    }

    /**
     * 初始化消息列表
     */
    private void initMessageList() {
        items = new ArrayList<>();
        cached = new HashMap<>(3);
        // adapter
        adapter = new RecentContactAdapter(recyclerView, items);
        adapter.setHasStableIds(true);
        initCallBack();
        adapter.setCallback(callback);
        // recyclerView
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(touchListener);
        // ios style
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        // drop listener
        DropManager.getInstance().setDropListener(new DropManager.IDropListener() {

            @Override
            public void onDropBegin() {
                touchListener.setShouldDetectGesture(false);
            }

            @Override
            public void onDropEnd() {
                touchListener.setShouldDetectGesture(true);
            }
        });
    }

    private void initCallBack() {
        if (callback != null) {
            return;
        }
        callback = new RecentContactsCallback() {

            @Override
            public void onRecentContactsLoaded() {
            }

            @Override
            public void onUnreadCountChange(int unreadCount) {
            }

            //会话item点击事件
            @Override
            public void onItemClick(RecentContact recent) {
                if (recent.getSessionType() == SessionTypeEnum.SUPER_TEAM) {
                    ToastHelper.showToast(getActivity(), "超大群开发者按需实现");
                } else if (recent.getSessionType() == SessionTypeEnum.Team) {
                    NimUIKit.startTeamSession(getActivity(), recent.getContactId());
                } else if (recent.getSessionType() == SessionTypeEnum.P2P) {
                    NimUIKit.startP2PSession(getActivity(), recent.getContactId());
                }
            }

            @Override
            public String getDigestOfAttachment(RecentContact recentContact, MsgAttachment attachment) {
                return null;
            }

            @Override
            public String getDigestOfTipMsg(RecentContact recent) {
                return null;
            }
        };
    }

    private SimpleClickListener<RecentContactAdapter> touchListener = new SimpleClickListener<RecentContactAdapter>() {

        @Override
        public void onItemClick(RecentContactAdapter adapter, View view, int position) {
            if (callback != null) {
                RecentContact recent = adapter.getItem(position);
                callback.onItemClick(recent);
            }
        }

        @Override
        public void onItemLongClick(RecentContactAdapter adapter, View view, int position) {
            showLongClickMenu(adapter.getItem(position), position);
        }

        @Override
        public void onItemChildClick(RecentContactAdapter adapter, View view, int position) {
        }

        @Override
        public void onItemChildLongClick(RecentContactAdapter adapter, View view, int position) {
        }
    };

    OnlineStateChangeObserver onlineStateChangeObserver = new OnlineStateChangeObserver() {

        @Override
        public void onlineStateChange(Set<String> accounts) {
            notifyDataSetChanged();
        }
    };

    private void registerOnlineStateChangeListener(boolean register) {
        if (!NimUIKitImpl.enableOnlineState()) {
            return;
        }
        NimUIKitImpl.getOnlineStateChangeObservable().registerOnlineStateChangeListeners(onlineStateChangeObserver,
                register);
    }

    private void showLongClickMenu(final RecentContact recent, final int mPosition) {
//        CustomAlertDialog alertDialog = new CustomAlertDialog(getActivity());
//        alertDialog.setTitle(UserInfoHelper.getUserTitleName(recent.getContactId(), recent.getSessionType()));
//        String title = getString(R.string.main_msg_list_delete_chatting);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_recent, null);

        TextView view1 = view.findViewById(R.id.tv_top);
        view1.setText((CommonUtil.isTagSet(recent, RECENT_TAG_STICKY) ? getString(
                R.string.main_msg_list_clear_sticky_on_top) : getString(R.string.main_msg_list_sticky_on_top)));
        new JAlertDialog.Builder(getContext())
                .setCancelable(true)
                .setAnimation(R.style.updateAnimation)
                .setContentView(view)
                .setOnClick(R.id.tv_del)
                .setOnClick(R.id.tv_top)
                .setWightPercent(0.7f)
                .setOnJAlertDialogCLickListener(new OnJAlertDialogClickListener() {

                    @Override
                    public void onClick(Dialog dialog, View view, int position) {
                        if (position == 0) {
                            // 删除会话，删除后，消息历史被一起删除
                            NIMClient.getService(MsgService.class).deleteRecentContact(recent);
                            NIMClient.getService(MsgService.class).clearChattingHistory(recent.getContactId(),
                                    recent.getSessionType());
                            //删除相应草稿文件
                            SPHelper.removeDraftString(recent.getContactId());
                            adapter.remove(mPosition);
                            postRunnable(new Runnable() {

                                @Override
                                public void run() {
                                    refreshMessages(true);
                                }
                            });
                        } else {
                            if (CommonUtil.isTagSet(recent, RECENT_TAG_STICKY)) {
                                CommonUtil.removeTag(recent, RECENT_TAG_STICKY);
                            } else {
                                CommonUtil.addTag(recent, RECENT_TAG_STICKY);
                            }
                            NIMClient.getService(MsgService.class).updateRecent(recent);
                            refreshMessages(false);
                        }
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

//        alertDialog.addItem(title, new onSeparateItemClickListener() {
//
//            @Override
//            public void onClick() {
//
//            }
//        });
//        title = (CommonUtil.isTagSet(recent, RECENT_TAG_STICKY) ? getString(
//                R.string.main_msg_list_clear_sticky_on_top) : getString(R.string.main_msg_list_sticky_on_top));
//        alertDialog.addItem(title, new onSeparateItemClickListener() {
//
//            @Override
//            public void onClick() {
//                if (CommonUtil.isTagSet(recent, RECENT_TAG_STICKY)) {
//                    CommonUtil.removeTag(recent, RECENT_TAG_STICKY);
//                } else {
//                    CommonUtil.addTag(recent, RECENT_TAG_STICKY);
//                }
//                NIMClient.getService(MsgService.class).updateRecent(recent);
//                refreshMessages(false);
//            }
//        });
//        alertDialog.addItem("删除该聊天（仅服务器）", new onSeparateItemClickListener() {
//
//            @Override
//            public void onClick() {
//                NIMClient.getService(MsgService.class).deleteRoamingRecentContact(recent.getContactId(),
//                        recent.getSessionType()).setCallback(
//                        new RequestCallback<Void>() {
//
//                            @Override
//                            public void onSuccess(Void param) {
//                                ToastHelper.showToast(getActivity(), "delete success");
//                            }
//
//                            @Override
//                            public void onFailed(int code) {
//                                ToastHelper.showToast(getActivity(), "delete failed, code:" + code);
//                            }
//
//                            @Override
//                            public void onException(Throwable exception) {
//                            }
//                        });
//            }
//        });
//        alertDialog.show();
    }

    private List<RecentContact> loadedRecents;

    public void setMsgLoaded(boolean msgLoaded) {
        this.msgLoaded = msgLoaded;
        requestMessages(true);
    }

    private void requestMessages(boolean delay) {
        if (msgLoaded) {
            return;
        }
        getHandler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (msgLoaded) {
                    return;
                }
                // 查询最近联系人列表数据
                NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(
                        new RequestCallbackWrapper<List<RecentContact>>() {

                            @Override
                            public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                                if (code != ResponseCode.RES_SUCCESS || recents == null) {
                                    return;
                                }
                                loadedRecents = recents;
                                // 初次加载，更新离线的消息中是否有@我的消息
                                for (RecentContact loadedRecent : loadedRecents) {
                                    if (loadedRecent.getSessionType() == SessionTypeEnum.Team) {
                                        updateOfflineContactAited(loadedRecent);
                                    }
                                }
                                // 此处如果是界面刚初始化，为了防止界面卡顿，可先在后台把需要显示的用户资料和群组资料在后台加载好，然后再刷新界面
                                msgLoaded = true;
                                if (isAdded()) {
                                    onRecentContactsLoaded();
                                }
                            }
                        });
            }
        }, delay ? 250 : 0);
    }

    private void onRecentContactsLoaded() {
        items.clear();
        if (loadedRecents != null) {
            items.addAll(loadedRecents);
            loadedRecents = null;
        }
        refreshMessages(true);
        if (callback != null) {
            callback.onRecentContactsLoaded();
        }
    }

    private void refreshMessages(boolean unreadChanged) {
        sortRecentContacts(items);
        notifyDataSetChanged();
        if (unreadChanged) {
            // 方式一：累加每个最近联系人的未读（快）
            int unreadNum = 0;
            for (RecentContact r : items) {
                unreadNum += r.getUnreadCount();
            }
            // 方式二：直接从SDK读取（相对慢）
            //int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();
            if (callback != null) {
                callback.onUnreadCountChange(unreadNum);
            }
            Badger.updateBadgerCount(unreadNum);
        }
    }

    /**
     * **************************** 排序 ***********************************
     */
    private void sortRecentContacts(List<RecentContact> list) {
        if (list.size() == 0) {
            return;
        }

        //todo 读取本地SP文件，如果有草稿，则设置到extension中
        for (int i = 0; i < list.size(); i++) {
            RecentContact recentContact = list.get(i);
            if (recentContact.getExtension() == null) {
                String draftString = SPHelper.getDraftString(recentContact.getContactId(), "");
                if (!StringUtil.isEmpty(draftString)) {
                    String content = draftString.substring(0, draftString.lastIndexOf("_"));
                    String draftTime = draftString.substring(draftString.lastIndexOf("_") + 1);
                    long time;
                    try {
                        time = Long.valueOf(draftTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    Map<String, Object> extension = recentContact.getExtension();
                    if (extension == null) extension = new HashMap<>();
                    extension.put(SessionConstant.RECENT_KEY_DRAFT_TIME, time);
                    if (!StringUtils.isEmpty(content)) {
                        extension.put(SessionConstant.RECENT_KEY_DRAFT, content);
                    } else {
                        if (recentContact.getTime() > time) {
                            //不存在草稿，且存在更新的消息
                            SPHelper.removeDraftString(recentContact.getContactId());
                        }
                    }
                    recentContact.setExtension(extension);
                }
            }
        }

        Collections.sort(list, comp);
    }

    private static Comparator<RecentContact> comp = new Comparator<RecentContact>() {

        @Override
        public int compare(RecentContact o1, RecentContact o2) {
            // 扩展信息
            Long draftTime1 = 0l;
            Long draftTime2 = 0l;
            if (o1.getExtension() != null) {
                draftTime1 = (Long) o1.getExtension().get(SessionConstant.RECENT_KEY_DRAFT_TIME);
                if (draftTime1 == null) draftTime1 = 0l;
            }
            if (o2.getExtension() != null) {
                draftTime2 = (Long) o2.getExtension().get(SessionConstant.RECENT_KEY_DRAFT_TIME);
                if (draftTime2 == null) draftTime2 = 0l;
            }

            // 先比较置顶tag
            long sticky = (o1.getTag() & RECENT_TAG_STICKY) - (o2.getTag() & RECENT_TAG_STICKY);
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else if (draftTime1 != 0 || draftTime2 != 0) {
                long maxTime1 = Math.max(draftTime1, o1.getTime());
                long maxTime2 = Math.max(draftTime2, o2.getTime());
                long time = maxTime1 - maxTime2;
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            } else {
                long time = o1.getTime() - o2.getTime();
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
    };

    /**
     * ********************** 收消息，处理状态变化 ************************
     */
    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(messageReceiverObserver, register);
        service.observeRecentContact(messageObserver, register);
        service.observeMsgStatus(statusObserver, register);
        service.observeRecentContactDeleted(deleteObserver, register);
        registerTeamUpdateObserver(register);
        registerTeamMemberUpdateObserver(register);
        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register);
        if (register) {
            registerUserInfoObserver();
        } else {
            unregisterUserInfoObserver();
        }
    }

    /**
     * 注册群信息&群成员更新监听
     */
    private void registerTeamUpdateObserver(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamDataChangedObserver(teamDataChangedObserver, register);
    }

    private void registerTeamMemberUpdateObserver(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamMemberDataChangedObserver(teamMemberDataChangedObserver,
                register);
    }

    private void registerDropCompletedListener(boolean register) {
        if (register) {
            DropManager.getInstance().addDropCompletedListener(dropCompletedListener);
        } else {
            DropManager.getInstance().removeDropCompletedListener(dropCompletedListener);
        }
    }

    // 暂存消息，当RecentContact 监听回来时使用，结束后清掉
    private Map<String, Set<IMMessage>> cacheMessages = new HashMap<>();

    //监听在线消息中是否有@我
    private Observer<List<IMMessage>> messageReceiverObserver = new Observer<List<IMMessage>>() {

        @Override
        public void onEvent(List<IMMessage> imMessages) {
            if (imMessages != null && !imMessages.isEmpty()) {
                for (IMMessage imMessage : imMessages) {
                    if (!TeamMemberAitHelper.isAitMessage(imMessage)) {
                        continue;
                    }
                    Set<IMMessage> cacheMessageSet = cacheMessages.get(imMessage.getSessionId());
                    if (cacheMessageSet == null) {
                        cacheMessageSet = new HashSet<>();
                        cacheMessages.put(imMessage.getSessionId(), cacheMessageSet);
                    }
                    cacheMessageSet.add(imMessage);
                }
            }
        }
    };

    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> recentContacts) {

            if (!DropManager.getInstance().isTouchable()) {
                // 正在拖拽红点，缓存数据
                for (RecentContact r : recentContacts) {
                    cached.put(r.getContactId(), r);
                }
                return;
            }

            Iterator<RecentContact> iterator = recentContacts.iterator();
            while (iterator.hasNext()) {
                RecentContact recentContact = iterator.next();
                if (recentContact.getAttachment() instanceof NotificationAttachment) {
                    NotificationAttachment notificationAttachment = (NotificationAttachment) recentContact.getAttachment();
                    if (notificationAttachment.getType() == NotificationType.DismissTeam) {
                        Team team = NimUIKit.getTeamProvider().getTeamById(recentContact.getContactId());
                        if (team.getCreator().equals(NimUIKit.getAccount())) {
                            //群解散
                            for (int i = 0; i < adapter.getData().size(); i++) {
                                if (adapter.getData().get(i).getContactId().equals(recentContact.getContactId())) {
                                    NIMClient.getService(MsgService.class).deleteRecentContact(recentContact);
                                    NIMClient.getService(MsgService.class).clearChattingHistory(recentContact.getContactId(),
                                            recentContact.getSessionType());
                                    adapter.remove(i);
                                    adapter.notifyDataSetChanged();
                                    setMsgLoaded(false);
                                }
                            }
                        }
                    }
                }

                //todo 测试
                if (recentContact.getAttachment() instanceof RobRedPackageAttachment
                        && !(recentContact.getAttachment() instanceof RedPackageAttachment)) {
                    RobRedPackageAttachment attachment = (RobRedPackageAttachment) recentContact.getAttachment();
                    if (!NimUIKitImpl.getAccount().equals(attachment.getmUserId())
                            && !NimUIKitImpl.getAccount().equals(attachment.getmOpUser())) {
                        iterator.remove();
                    }
                }
            }

            if (ListUtils.isEmpty(recentContacts)) return;
            onRecentContactChanged(recentContacts);
        }
    };

    private void onRecentContactChanged(List<RecentContact> recentContacts) {
        int index;
        for (RecentContact r : recentContacts) {
            index = -1;
            for (int i = 0; i < items.size(); i++) {
                if (r.getContactId().equals(items.get(i).getContactId()) && r.getSessionType() == (items.get(i)
                        .getSessionType())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                items.remove(index);
            }
            items.add(r);
            if (r.getSessionType() == SessionTypeEnum.Team && cacheMessages.get(r.getContactId()) != null) {
                TeamMemberAitHelper.setRecentContactAited(r, cacheMessages.get(r.getContactId()));
            }
        }
        cacheMessages.clear();
        refreshMessages(true);
    }

    DropCover.IDropCompletedListener dropCompletedListener = new DropCover.IDropCompletedListener() {

        @Override
        public void onCompleted(Object id, boolean explosive) {
            if (cached != null && !cached.isEmpty()) {
                // 红点爆裂，已经要清除未读，不需要再刷cached
                if (explosive) {
                    if (id instanceof RecentContact) {
                        RecentContact r = (RecentContact) id;
                        cached.remove(r.getContactId());
                    } else if (id instanceof String && ((String) id).contentEquals("0")) {
                        cached.clear();
                    }
                }
                // 刷cached
                if (!cached.isEmpty()) {
                    List<RecentContact> recentContacts = new ArrayList<>(cached.size());
                    recentContacts.addAll(cached.values());
                    cached.clear();
                    onRecentContactChanged(recentContacts);
                }
            }
        }
    };

    Observer<IMMessage> statusObserver = new Observer<IMMessage>() {

        @Override
        public void onEvent(IMMessage message) {
            int index = getItemIndex(message.getUuid());
            if (index >= 0 && index < items.size()) {
                RecentContact item = items.get(index);
                item.setMsgStatus(message.getStatus());
                refreshViewHolderByIndex(index);
            }
        }
    };

    Observer<RecentContact> deleteObserver = new Observer<RecentContact>() {

        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact != null) {
                for (RecentContact item : items) {
                    if (TextUtils.equals(item.getContactId(), recentContact.getContactId()) &&
                            item.getSessionType() == recentContact.getSessionType()) {
                        items.remove(item);
                        refreshMessages(true);
                        break;
                    }
                }
            } else {
                items.clear();
                refreshMessages(true);
            }
        }
    };

    TeamDataChangedObserver teamDataChangedObserver = new TeamDataChangedObserver() {

        @Override
        public void onUpdateTeams(List<Team> teams) {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onRemoveTeam(Team team) {
        }
    };

    TeamMemberDataChangedObserver teamMemberDataChangedObserver = new TeamMemberDataChangedObserver() {

        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onRemoveTeamMember(List<TeamMember> member) {
        }
    };

    private int getItemIndex(String uuid) {
        for (int i = 0; i < items.size(); i++) {
            RecentContact item = items.get(i);
            if (TextUtils.equals(item.getRecentMessageId(), uuid)) {
                return i;
            }
        }
        return -1;
    }

    protected void refreshViewHolderByIndex(final int index) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyItemChanged(index);
            }
        });
    }

    public void setCallback(RecentContactsCallback callback) {
        this.callback = callback;
    }

    private void registerUserInfoObserver() {
        if (userInfoObserver == null) {
            userInfoObserver = new UserInfoObserver() {

                @Override
                public void onUserInfoChanged(List<String> accounts) {
                    refreshMessages(false);
                }
            };
        }
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, true);
    }

    private void unregisterUserInfoObserver() {
        if (userInfoObserver != null) {
            NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, false);
        }
    }

    ContactChangedObserver friendDataChangedObserver = new ContactChangedObserver() {

        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            refreshMessages(false);
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            for (String acc : accounts) {
                for (int i = 0; i < items.size(); i++) {
                    RecentContact recent = items.get(i);
                    if (acc.contains(recent.getContactId())) {
                        NIMClient.getService(MsgService.class).deleteRecentContact(recent);
                        NIMClient.getService(MsgService.class).clearChattingHistory(recent.getContactId(),
                                recent.getSessionType());
                        adapter.remove(i);
                        break;
                    }
                }
            }
            postRunnable(new Runnable() {

                @Override
                public void run() {
                    refreshMessages(true);
                }
            });
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
            refreshMessages(false);
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
            refreshMessages(false);
        }
    };

    private void updateOfflineContactAited(final RecentContact recentContact) {
        if (recentContact == null || recentContact.getSessionType() != SessionTypeEnum.Team ||
                recentContact.getUnreadCount() <= 0) {
            return;
        }
        // 锚点
        List<String> uuid = new ArrayList<>(1);
        uuid.add(recentContact.getRecentMessageId());
        List<IMMessage> messages = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuid);
        if (messages == null || messages.size() < 1) {
            return;
        }
        final IMMessage anchor = messages.get(0);
        // 查未读消息
        NIMClient.getService(MsgService.class).queryMessageListEx(anchor, QueryDirectionEnum.QUERY_OLD,
                recentContact.getUnreadCount() - 1, false)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {

                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS && result != null) {
                            result.add(0, anchor);
                            Set<IMMessage> messages = null;
                            // 过滤存在的@我的消息
                            for (IMMessage msg : result) {
                                if (TeamMemberAitHelper.isAitMessage(msg)) {
                                    if (messages == null) {
                                        messages = new HashSet<>();
                                    }
                                    messages.add(msg);
                                }
                            }
                            // 更新并展示
                            if (messages != null) {
                                TeamMemberAitHelper.setRecentContactAited(recentContact, messages);
                                notifyDataSetChanged();
                            }
                        }
                    }
                });

    }
}
