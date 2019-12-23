package com.netease.nim.uikit.business.recent;

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
public class RpOpenedMessageFilter22 {

    private static Map<String, IMMessage> delete = new HashMap<>();

    private static Map<String, IMMessage> emptyCheck = new HashMap<>();

    private static Observer<List<IMMessage>> messageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(final List<IMMessage> imMessages) {
            if (imMessages != null) {
                Iterator<IMMessage> iterator = imMessages.iterator();
                while (iterator.hasNext()) {
                    final IMMessage imMessage = iterator.next();
                    if (shouldFilter(imMessage)) {
                        //在此删除消息，recentContactObserver 监听一样会走，
                        NIMClient.getService(MsgService.class).deleteChattingHistory(imMessage);
                        iterator.remove();
                    }
                }
            }
        }
    };

    private static Observer<List<RecentContact>> recentContactObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> recentContacts) {
            if (recentContacts != null) {
                Iterator<RecentContact> iterator = recentContacts.iterator();
                while (iterator.hasNext()) {
                    RecentContact recentContact = iterator.next();
                    if (delete.containsKey(recentContact.getRecentMessageId())) {
                        // 等待删除该消息之后再通知该 recentContact
                        iterator.remove();
                    } else if (delete.isEmpty() && emptyCheck.containsKey(recentContact.getContactId())) {
                        // deleteChattingHistory 之后再次回调，判断是否 remove
                        if (recentContact.getRecentMessageId().isEmpty()) {
                            iterator.remove();
                        }
                    }
                }
            }

            if (!emptyCheck.isEmpty()) {
                emptyCheck.clear();
            } else if (!delete.isEmpty()) {
                Iterator<String> iterator = delete.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    emptyCheck.put(delete.get(key).getSessionId(), delete.get(key));
                    NIMClient.getService(MsgService.class).deleteChattingHistory(delete.get(key));
                }
                delete.clear();
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
