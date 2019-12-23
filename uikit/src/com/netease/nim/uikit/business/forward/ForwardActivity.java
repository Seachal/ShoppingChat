package com.netease.nim.uikit.business.forward;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laka.androidlib.base.activity.BaseActivity;
import com.laka.androidlib.util.StatusBarUtil;
import com.laka.androidlib.util.screen.ScreenUtils;
import com.laka.androidlib.widget.dialog.JAlertDialog;
import com.laka.androidlib.widget.dialog.OnJAlertDialogClickListener;
import com.laka.androidlib.widget.titlebar.TitleBarView;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactCustomItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.IContact;
import com.netease.nim.uikit.business.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.business.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.business.contact.core.query.TextQuery;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.forward.adapter.SectionMultipleItemAdapter;
import com.netease.nim.uikit.business.forward.adapter.SelectItemAdapter;
import com.netease.nim.uikit.business.team.ForwardTeamCreateHelper;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.List;

public class ForwardActivity extends BaseActivity {
    public static final String FORWARD = "FORWARD";
    public static final int CONTACT_SELECT_CODE = 101;
    public static final String RESULT_DATA = "RESULT_DATA"; // 返回结果
    RecyclerView rlList;
    RecyclerView rlSelect;
    EditText etSearch;
    ImageView ivSearch;
    HorizontalScrollView scrollView;
    TextView createChat;
    TitleBarView titleBar;
    boolean isMultipleSelect;
    SectionMultipleItemAdapter mSectionAdapter;
    SelectItemAdapter mSelectItemAdapter;
    List<SelectMultiItem> selectMultiItems = new ArrayList<>();
    IContactDataProvider friendProvider;
    IContactDataProvider teamProvider;
    List<SelectMultiItem> recentContactsList;
    String context;

    public static void startActivityForResult(Context context, String content, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, ForwardActivity.class);
        intent.putExtra(ForwardActivity.FORWARD, content);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    public int setContentView() {
        return R.layout.activity_forward;
    }

    @Override
    public void initIntent() {
        context = getIntent().getStringExtra(FORWARD);
    }

    @Override
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.color_ededed));
        } else {
            super.setStatusBarColor(color);
        }
    }

    @Override
    protected void initViews() {
        scrollView = findViewById(R.id.scroll_view);
        ivSearch = findViewById(R.id.iv_search);
        etSearch = findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if (!TextUtils.isEmpty(string)) {
                    List<AbsContactItem> contactItems = teamProvider.provide(new TextQuery(string));
                    List<AbsContactItem> friendList = friendProvider.provide(new TextQuery(string));
                    List<SelectMultiItem> list = new ArrayList<>();
                    if (friendList.size() > 0) {
                        list.add(new SelectMultiItem(true, "好友", true));
                        for (AbsContactItem contactItem : friendList) {
                            if (contactItem instanceof ContactItem) {
                                SelectMultiItem multipleItem = new SelectMultiItem(SelectMultiItem.Contact, new MultiContactItem((ContactItem) contactItem), isMultipleSelect);
                                for (SelectMultiItem item : mSelectItemAdapter.getData()) {
                                    MultiContactItem contactItem1 = item.getContactItem();
                                    if (contactItem1 != null) {
                                        ContactCustomItem contactCustomItem = contactItem1.getContactCustomItem();
                                        ContactItem contactItem1ContactItem = contactItem1.getContactItem();
                                        RecentContact recentContact = contactItem1.getRecentContact();
                                        String contactId = "";
                                        if (contactCustomItem != null) {
                                            contactId = contactCustomItem.getContactId();
                                        } else if (contactItem1ContactItem != null) {
                                            contactId = contactItem1ContactItem.getContact().getContactId();
                                        } else if (recentContact != null) {
                                            contactId = recentContact.getContactId();
                                        }
                                        if (((ContactItem) contactItem).getContact().getContactId().equals(contactId)) {
                                            multipleItem.setSelect(true);
                                        }
                                    }
                                }
                                list.add(multipleItem);
                            }
                        }
                    }
                    if (contactItems.size() > 0) {
                        list.add(new SelectMultiItem(true, "群组", true));
                        for (AbsContactItem contactItem : contactItems) {
                            SelectMultiItem multipleItem = new SelectMultiItem(SelectMultiItem.Contact, new MultiContactItem((ContactItem) contactItem), isMultipleSelect);
                            list.add(multipleItem);
                        }
                    }
                    mSectionAdapter.getData().clear();
                    mSectionAdapter.addData(list);
                    mSectionAdapter.notifyDataSetChanged();
                } else {
                    mSectionAdapter.getData().clear();
                    mSectionAdapter.addData(recentContactsList);
                    mSectionAdapter.notifyDataSetChanged();
                }

            }
        });
        initTitle();
        initList();
        initSelectList();
        teamProvider = new ContactDataProvider(ItemTypes.TEAM);
        friendProvider = new ContactDataProvider(ItemTypes.FRIEND);
//        notifySelectAreaDataSetChanged();
    }

    private void initSelectList() {
        rlSelect = findViewById(R.id.rlSelect);
        mSelectItemAdapter = new SelectItemAdapter(selectMultiItems);
        mSelectItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MultiContactItem contactItem = selectMultiItems.get(position).getContactItem();
                if (contactItem != null) {
                    syncStatus(contactItem.getContactId(), false, contactItem.getContactType());
                }
                selectMultiItems.remove(position);
                mSectionAdapter.notifyDataSetChanged();
                notifySelectAreaDataSetChanged();
                setRightText();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlSelect.setLayoutManager(layoutManager);
        rlSelect.setAdapter(mSelectItemAdapter);
    }

    private void initList() {
        rlList = findViewById(R.id.rl_list);
        rlList.setLayoutManager(new LinearLayoutManager(this));
        recentContactsList = new ArrayList<>();
        recentContactsList.add(new SelectMultiItem(true, "最近聊天", true));
        List<RecentContact> recentContacts = getRecentContact();
        for (RecentContact contact : recentContacts) {
            if (contact.getSessionType() == SessionTypeEnum.P2P) {
                recentContactsList.add(new SelectMultiItem(SelectMultiItem.Contact, new MultiContactItem(contact), false));
            } else {
                Team mTeam = NimUIKit.getTeamProvider().getTeamById(contact.getContactId());
                if (mTeam.isMyTeam()) {
                    recentContactsList.add(new SelectMultiItem(SelectMultiItem.Contact, new MultiContactItem(contact), false));
                }
            }
        }
        mSectionAdapter = new SectionMultipleItemAdapter(R.layout.item_forward_head);
        mSectionAdapter.addData(recentContactsList);
        mSectionAdapter.setHasStableIds(true);
        createChat = (TextView) getLayoutInflater().inflate(R.layout.item_forward_create_chat, rlList, false);
        createChat.findViewById(R.id.create_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> contact = new ArrayList();
                for (SelectMultiItem selectMultiItem : selectMultiItems) {
                    MultiContactItem contactItem = selectMultiItem.getContactItem();
                    if (contactItem != null) {
                        contact.add(MultiContactItem.spliceContactId(contactItem.getContactType(), contactItem.getContactId()));
                    }
                }
                ContactSelectActivity.Option contactSelectOption = TeamHelper.getForwardSelectContactPeopleOption(contact);
                contactSelectOption.multiForward = isMultipleSelect;
                NimUIKit.startContactSelector(
                        ForwardActivity.this,
                        contactSelectOption,
                        CONTACT_SELECT_CODE
                );
            }
        });
        mSectionAdapter.addHeaderView(createChat);

        mSectionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                SelectMultiItem item = (SelectMultiItem) adapter.getData().get(position);
                MultiContactItem contactItem1 = item.getContactItem();

                String contactContactId = "";
                int contactType = -1;//0 好友，1 群组
                if (contactItem1 != null) {
                    contactContactId = contactItem1.getContactId();
                    contactType = contactItem1.getContactType();
                }
                if (view.getId() == R.id.isSelect) {
                    if (isMultipleSelect) {
                        CheckBox checkBox = (CheckBox) view;
                        item.setSelect(checkBox.isChecked());
                        int mPosition = -1;
                        for (int i = 0; i < selectMultiItems.size(); i++) {
                            SelectMultiItem selectMultiItem = selectMultiItems.get(i);
                            MultiContactItem contactItem = selectMultiItem.getContactItem();
                            String contactId = "";
                            if (contactItem != null) {
                                contactId = contactItem.getContactId();
                            }
                            if (contactId.equals(contactContactId)) {
                                mPosition = i;
                                break;
                            }
                        }
                        if (checkBox.isChecked()) {
                            if (mPosition == -1) {
                                selectMultiItems.add(item);
                            }
                        } else {
                            if (mPosition != -1) {
                                selectMultiItems.remove(mPosition);
                            }
                        }
                        syncStatus(contactContactId, checkBox.isChecked(), contactType);
                        notifySelectAreaDataSetChanged();
                        setRightText();
                    }
                } else if (view.getId() == R.id.card_view) {
                    if (isMultipleSelect) {
                        boolean select = item.isSelect();
                        item.setSelect(!select);
                        int mPosition = -1;
                        for (int i = 0; i < selectMultiItems.size(); i++) {
                            SelectMultiItem selectMultiItem = selectMultiItems.get(i);
                            MultiContactItem contactItem = selectMultiItem.getContactItem();
                            String contactId = "";
                            if (contactItem != null) {
                                contactId = contactItem.getContactId();
                            }
                            if (contactId.equals(contactContactId)) {
                                mPosition = i;
                                break;
                            }
                        }
                        if (item.isSelect()) {
                            if (mPosition == -1) {
                                selectMultiItems.add(item);
                            }
                        } else {
                            if (mPosition != -1) {
                                selectMultiItems.remove(mPosition);
                            }
                        }
                        syncStatus(contactContactId,item.isSelect(), contactType);
                        notifySelectAreaDataSetChanged();
                        setRightText();
                    } else {
                        SelectMultiItem selectMultiItem = mSectionAdapter.getData().get(position);
                        MultiContactItem multipleItemContactItem = selectMultiItem.getContactItem();
                        if (multipleItemContactItem != null) {
                            ArrayList<ContactCustomItem> arrayList = new ArrayList<>();
                            arrayList.add(multipleItemContactItem.ceateContact());
                            showSingleDialog(arrayList);
                        }
                    }
                }
            }
        });
        rlList.setAdapter(mSectionAdapter);
    }

    private void syncStatus(String contactId, boolean checked, int contactType) {
        if (recentContactsList == null) return;
        for (SelectMultiItem item : recentContactsList) {
            MultiContactItem contactItem = item.getContactItem();
            if (contactItem != null) {
                int type = contactItem.getContactType();
                if (type == contactType
                        && contactId.equals(contactItem.getContactId())) {
                    item.setSelect(checked);
                }
            }
        }
        mSectionAdapter.notifyDataSetChanged();
    }

//    private String initContact(final String contactId, final int contactType) {
//        if (contactType == IContact.Type.Team) {
//            return contactId + ContactSelectActivity.CHAT_TEAM;
//        } else {
//            return contactId + ContactSelectActivity.CHAT_P2P;
//        }
//    }

    private void initTitle() {
        titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("选择")
                .setBackGroundColor(R.color.color_ededed)
                .setTitleTextColor(R.color.color_2d2d2d)
                .setLeftIcon(R.drawable.seletor_nav_btn_back)
                .setRightText("多选")
                .setRightTextColor(R.color.white)
                .setRightTextBg(R.drawable.bg_send_friend_bg)
                .setOnRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isMultipleSelect) {
                            if (selectMultiItems.size() > 0) {
                                ArrayList<ContactCustomItem> arrayList = new ArrayList<>();
                                for (SelectMultiItem selectMultiItem : selectMultiItems) {
                                    MultiContactItem contactItem = selectMultiItem.getContactItem();
                                    arrayList.add(contactItem.ceateContact());
                                }
                                showSingleDialog(arrayList);
                            } else {
                                changeStatus();
                            }
                        } else {
                            changeStatus();
                        }
                    }
                });
    }

    private void changeStatus() {
        isMultipleSelect = !isMultipleSelect;
        for (SelectMultiItem selectMultiItem : mSectionAdapter.getData()) {
            selectMultiItem.setMultipleSelect(isMultipleSelect);
            selectMultiItem.setSelect(false);
        }
        for (SelectMultiItem selectMultiItem : recentContactsList) {
            selectMultiItem.setMultipleSelect(isMultipleSelect);
            selectMultiItem.setSelect(false);
        }
        mSectionAdapter.notifyDataSetChanged();
        if (isMultipleSelect) {
            titleBar.setRightText("单选");
            createChat.setText("更多联系人");
        } else {
            selectMultiItems.clear();
            notifySelectAreaDataSetChanged();
            titleBar.setRightText("多选");
            createChat.setText("创建新聊天");
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    public List<RecentContact> getRecentContact() {
        return NIMClient.getService(MsgService.class).queryRecentContactsBlock();
    }

    private void notifySelectAreaDataSetChanged() {
        int converViewWidth = ScreenUtils.dp2px(36);
        ViewGroup.LayoutParams layoutParams = rlSelect.getLayoutParams();
        layoutParams.width = converViewWidth * mSelectItemAdapter.getData().size();
        layoutParams.height = converViewWidth;
        rlSelect.setLayoutParams(layoutParams);
        if (mSelectItemAdapter.getData().size() > 0) {
            ivSearch.setVisibility(View.GONE);
        } else {
            ivSearch.setVisibility(View.VISIBLE);
        }
        try {
            final int x = layoutParams.width;
            final int y = layoutParams.height;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(x, y);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSelectItemAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_SELECT_CODE) {
            if (data == null) return;
            ArrayList<String> listExtra = data.getStringArrayListExtra(ContactSelectActivity.RESULT_TYPE_DATA);
            if (isMultipleSelect) {
                if (listExtra != null && listExtra.size() > 0) {
                    selectMultiItems.clear();
                    for (SelectMultiItem selectMultiItem : mSectionAdapter.getData()) {
                        selectMultiItem.setMultipleSelect(isMultipleSelect);
                        selectMultiItem.setSelect(false);
                    }
                    for (SelectMultiItem selectMultiItem : recentContactsList) {
                        selectMultiItem.setMultipleSelect(isMultipleSelect);
                        selectMultiItem.setSelect(false);
                    }
                    for (String contact : listExtra) {
                        //选择头像
                        MultiContactItem multiContactItem = MultiContactItem.multipleContactItem(contact);
                        if (MultiContactItem.isP2P(contact)) {
                            syncStatus(MultiContactItem.decodeContactId(contact), true, IContact.Type.Friend);
                        } else {
                            syncStatus(MultiContactItem.decodeContactId(contact), true, IContact.Type.Team);
                        }
                        SelectMultiItem selectMultiItem = new SelectMultiItem(SelectMultiItem.Contact, multiContactItem, isMultipleSelect);
                        selectMultiItems.add(selectMultiItem);
                        notifySelectAreaDataSetChanged();
                        //列表同步
                    }
                }
                setRightText();
            } else {
                if (listExtra.size() == 1) {
                    String contact = listExtra.get(0);
                    ArrayList<ContactCustomItem> arrayList = new ArrayList<>();
                    if (MultiContactItem.isP2P(contact)) {
                        arrayList.add(new ContactCustomItem(MultiContactItem.decodeContactId(contact), IContact.Type.Friend));
                    } else {
                        arrayList.add(new ContactCustomItem(MultiContactItem.decodeContactId(contact), IContact.Type.Team));
                    }
                    showSingleDialog(arrayList);
                } else if (listExtra.size() > 1) {
                    ForwardTeamCreateHelper forwardTeamCreateHelper = new ForwardTeamCreateHelper();
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (String account : listExtra) {
                        arrayList.add(MultiContactItem.decodeContactId(account));
                    }
                    forwardTeamCreateHelper.createAdvancedTeam(this, arrayList);
                    forwardTeamCreateHelper.setLisenter(new ForwardTeamCreateHelper.onCreateTeamSuccessLisenter() {
                        @Override
                        public void success(String teamId) {
                            ArrayList<ContactCustomItem> arrayList = new ArrayList<>();
                            arrayList.add(new ContactCustomItem(teamId, IContact.Type.Team));
                            showSingleDialog(arrayList);
                        }
                    });
                }
            }
        }
    }

    public void showSingleDialog(final ArrayList<ContactCustomItem> contactCustomItems) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_forward, null);
//        HeadImageView imageView = view.findViewById(R.id.iv_avater);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvContent = view.findViewById(R.id.tvContent);
        RecyclerView rlView = view.findViewById(R.id.rlAvater);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlView.setLayoutManager(layoutManager);
        rlView.setAdapter(new BaseQuickAdapter<ContactCustomItem, BaseViewHolder>(R.layout.item_dialog_avater, contactCustomItems) {

            @Override
            protected void convert(@NonNull BaseViewHolder helper, ContactCustomItem item) {
                HeadImageView imageView = helper.getView(R.id.iv_avater);
                if (item.getContactType() == IContact.Type.Friend) {
                    imageView.loadBuddyAvatar(item.getContactId());

                } else {
                    Team mTeam = NimUIKit.getTeamProvider().getTeamById(item.getContactId());
                    imageView.loadTeamIconByTeam(mTeam);
                }
            }
        });
        if (contactCustomItems.size() > 1) {
            tvName.setVisibility(View.GONE);
        } else {
            tvName.setVisibility(View.VISIBLE);
            if (contactCustomItems.get(0).getContactType() == IContact.Type.Friend) {
                tvName.setText(UserInfoHelper.getUserTitleName(contactCustomItems.get(0).getContactId(), SessionTypeEnum.P2P));
            } else {
                tvName.setText(UserInfoHelper.getUserTitleName(contactCustomItems.get(0).getContactId(), SessionTypeEnum.Team));
            }
        }
        tvContent.setText(context + "");
        new JAlertDialog.Builder(this)
                .setCancelable(true)
                .setAnimation(R.style.updateAnimation)
                .setContentView(view)
                .setOnClick(R.id.tv_send)
                .setOnClick(R.id.tv_cancel)
                .setWightPercent(0.8f)
                .setOnJAlertDialogCLickListener(new OnJAlertDialogClickListener() {

                    @Override
                    public void onClick(Dialog dialog, View view, int position) {
                        if (position == 0) {
                            sendForWardMsg(contactCustomItems);
                        }
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void sendForWardMsg(ArrayList<ContactCustomItem> contactCustomItems) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (ContactCustomItem contactCustomItem : contactCustomItems) {
            arrayList.add(MultiContactItem.spliceContactId(contactCustomItem.getContactType(), contactCustomItem.getContactId()));
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra(RESULT_DATA, arrayList);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void finish() {
        showKeyboard(false);
        super.finish();
    }

    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void setRightText() {
        if (isMultipleSelect) {
            if (selectMultiItems.size() == 0) {
                titleBar.setRightText("单选");
            } else {
                titleBar.setRightText("发送(" + selectMultiItems.size() + ")");
            }
        }
    }
}
