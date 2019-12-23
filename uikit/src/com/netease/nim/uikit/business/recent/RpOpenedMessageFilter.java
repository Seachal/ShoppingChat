package com.netease.nim.uikit.business.recent;

import com.laka.androidlib.util.LogUtils;
import com.netease.nim.uikit.business.session.attachment.CustomAttachParser;
import com.netease.nim.uikit.business.session.attachment.CustomAttachmentType;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hzchenkang on 2017/8/2.
 * 结合observeReceiveMessage、observeRecentContact 做一个应用层面的消息过滤，过滤掉与自己无关的拆红包消息
 * 前提是RpOpenedMessageFilter 必须先于其他ReceiveMessage、RecentContact 观察者之前注册
 * 该消息最好设置为不计未读数
 */
public class RpOpenedMessageFilter {

    private static Map<String, IMMessage> mDeleteMap = new HashMap<>();
    private static Map<String, IMMessage> mEmptryMsgContact = new HashMap<>();

    private static Observer<List<IMMessage>> messageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(final List<IMMessage> imMessages) { //消息集合中的所有消息都来自同一个会话，这是不是表示列表中会出现多条消息？
            if (imMessages != null) {
                Iterator<IMMessage> iterator = imMessages.iterator();
                while (iterator.hasNext()) {
                    final IMMessage imMessage = iterator.next();
                    if (shouldFilter(imMessage)) {
                        mDeleteMap.put(imMessage.getUuid(), imMessage);
                        iterator.remove();//后面的监听不到
                    }
                }
            }
        }
    };

    private static Observer<List<RecentContact>> recentContactObserver = new Observer<List<RecentContact>>() {

        /**
         * 根据测试发现，这里的回调确实把相应的消息删除了，在聊天页面中，由于并没有监听联系人变化，
         * 里面的消息监听者监听到的消息都是删除后的（上面的消息列表做了删除操作，导致后面的监听就监听不到相应的消息，但是数据库里面的消息这时还未删除）
         * 由此可知应该是会话列表监听回调中做了一操作把消息重新添加到数据库
         * */
        @Override
        public void onEvent(List<RecentContact> recentContacts) { //高并发的情况下，也是有问题的
            if (recentContacts != null) {
                Iterator<RecentContact> iterator = recentContacts.iterator();
                while (iterator.hasNext()) {
                    RecentContact recentContact = iterator.next();
                    String contactId = recentContact.getContactId();
                    String messageId = recentContact.getRecentMessageId();
                    if (mDeleteMap.containsKey(messageId)) {
                        LogUtils.info("filter--------contact1--------" + messageId);
                        mEmptryMsgContact.put(contactId, mDeleteMap.get(messageId));
                        //集合移除会话，后面的监听器就监听不到该条会话
                        iterator.remove();
                        //集合移除消息
                        IMMessage remove = mDeleteMap.remove(messageId);
                        //本地删除消息，会回调一次当前监听（如果操作太频繁，会出现删除不了消息的问题）
                        NIMClient.getService(MsgService.class).deleteChattingHistory(remove);
                    } else if (mEmptryMsgContact.containsKey(contactId)) {
                        LogUtils.info("filter--------contact2--------" + messageId);
                        iterator.remove();
                        mEmptryMsgContact.remove(contactId);
                    }
                }
            }
        }
    };

    private static boolean shouldFilter(IMMessage message) {
        if (message.getMsgType() == MsgTypeEnum.custom) {
            try {
                JSONObject json = new JSONObject(message.getAttachStr());
                int type = json.getInt(CustomAttachParser.KEY_TYPE);
                if (type == CustomAttachmentType.robRedPackage) { //抢红包通知
                    JSONObject data = json.getJSONObject(CustomAttachParser.KEY_DATA);
                    String userId = data.getString("user_id");
                    String opeUserId = data.getString("op_user");
                    if (!NimUIKitImpl.getAccount().equals(userId) && !NimUIKitImpl.getAccount().equals(opeUserId)) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void startFilter() {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(messageObserver, true);
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentContactObserver, true);
    }

    public static void stopFilter() {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(messageObserver, false);
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentContactObserver, false);
    }
}
