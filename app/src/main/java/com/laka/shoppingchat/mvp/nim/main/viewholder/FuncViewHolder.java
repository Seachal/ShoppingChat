package com.laka.shoppingchat.mvp.nim.main.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laka.shoppingchat.R;
import com.laka.shoppingchat.mvp.nim.activity.SystemMessageActivity;
import com.laka.shoppingchat.mvp.nim.activity.TeamListActivity;
import com.laka.shoppingchat.mvp.nim.main.helper.SystemMessageUnreadManager;
import com.laka.shoppingchat.mvp.nim.main.reminder.ReminderId;
import com.laka.shoppingchat.mvp.nim.main.reminder.ReminderItem;
import com.laka.shoppingchat.mvp.nim.main.reminder.ReminderManager;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.business.contact.core.viewholder.AbsContactViewHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FuncViewHolder extends AbsContactViewHolder<FuncViewHolder.FuncItem> implements ReminderManager.UnreadNumChangedCallback {

    private static ArrayList<WeakReference<ReminderManager.UnreadNumChangedCallback>> sUnreadCallbackRefs = new ArrayList<>();

    private View divder;
    private ImageView image;
    private TextView funcName;
    private TextView unreadNum;
    private Set<ReminderManager.UnreadNumChangedCallback> callbacks = new HashSet<>();

    @Override
    public View inflate(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.func_contacts_item, null);
        this.image = view.findViewById(R.id.img_head);
        this.funcName = view.findViewById(R.id.tv_func_name);
        this.unreadNum = view.findViewById(R.id.tab_new_msg_label);
        this.divder = view.findViewById(R.id.line);
        return view;
    }

    @Override
    public void refresh(ContactDataAdapter contactAdapter, int position, FuncItem item) {

        //功能item放在列表头部，所以只有首个item要隐藏头部分割线
        if (position == 0) {
            divder.setVisibility(View.GONE);
        } else {
            divder.setVisibility(View.VISIBLE);
        }

        if (item == FuncItem.VERIFY) {
            funcName.setText("新的朋友");
            image.setImageResource(R.drawable.select_friend_btn_newfriend);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            int unreadCount = SystemMessageUnreadManager.getInstance().getSysMsgUnreadCount();
            updateUnreadNum(unreadCount);
            ReminderManager.getInstance().registerUnreadNumChangedCallback(this);
            sUnreadCallbackRefs.add(new WeakReference<ReminderManager.UnreadNumChangedCallback>(this));
        } else if (item == FuncItem.NORMAL_TEAM) {
            funcName.setText("群聊");
            image.setImageResource(R.drawable.selector_friend_btn_groupchat);
        }

        if (item != FuncItem.VERIFY) {
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            unreadNum.setVisibility(View.GONE);
        }
    }


    private void updateUnreadNum(int unreadCount) {
        // 2.*版本viewholder复用问题
        if (unreadCount > 0 && funcName.getText().toString().equals("新的朋友")) {
            unreadNum.setVisibility(View.VISIBLE);
//            unreadNum.setText("" + unreadCount);
        } else {
            unreadNum.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUnreadNumChanged(ReminderItem item) {
        if (item.getId() != ReminderId.CONTACT) {
            return;
        }
        updateUnreadNum(item.getUnread());
    }

    public static void unRegisterUnreadNumChangedCallback() {
        Iterator<WeakReference<ReminderManager.UnreadNumChangedCallback>> iter = sUnreadCallbackRefs.iterator();
        while (iter.hasNext()) {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(iter.next().get());
            iter.remove();
        }
    }


    public final static class FuncItem extends AbsContactItem {
        static final FuncItem VERIFY = new FuncItem();
        static final FuncItem ROBOT = new FuncItem();
        static final FuncItem NORMAL_TEAM = new FuncItem();
        static final FuncItem ADVANCED_TEAM = new FuncItem();
        static final FuncItem BLACK_LIST = new FuncItem();
        static final FuncItem MY_COMPUTER = new FuncItem();

        @Override
        public int getItemType() {
            return ItemTypes.FUNC;
        }

        @Override
        public String belongsGroup() {
            return null;
        }


        public static List<AbsContactItem> provide() {
            List<AbsContactItem> items = new ArrayList<>();
            items.add(VERIFY);
            //items.add(ROBOT);
            items.add(NORMAL_TEAM);
//            items.add(ADVANCED_TEAM);
//            items.add(BLACK_LIST);
//            items.add(MY_COMPUTER);

            return items;
        }

        public static void handle(Context context, AbsContactItem item) {
            if (item == VERIFY) {
                SystemMessageActivity.start(context);
            } else if (item == NORMAL_TEAM) {
                TeamListActivity.start(context, ItemTypes.TEAMS.ADVANCED_TEAM);
            }
//            else if (item == ROBOT) {
//                RobotListActivity.start(context);
//            }  else if (item == ADVANCED_TEAM) {
//                TeamListActivity.start(context, ItemTypes.TEAMS.ADVANCED_TEAM);
//            } else if (item == MY_COMPUTER) {
//                SessionHelper.startP2PSession(context, DemoCache.getAccount());
//            } else if (item == BLACK_LIST) {
//                BlackListActivity.start(context);
//            }
        }
    }
}
