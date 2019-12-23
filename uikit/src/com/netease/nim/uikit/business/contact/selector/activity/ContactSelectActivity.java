package com.netease.nim.uikit.business.contact.selector.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.laka.androidlib.util.LogUtils;
import com.laka.androidlib.util.screen.ScreenUtils;
import com.laka.androidlib.widget.titlebar.TitleBarView;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItemFilter;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.AbsContactDataList;
import com.netease.nim.uikit.business.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.business.contact.core.model.ContactGroupStrategy;
import com.netease.nim.uikit.business.contact.core.model.IContact;
import com.netease.nim.uikit.business.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.business.contact.core.provider.TeamMemberDataProvider;
import com.netease.nim.uikit.business.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.business.contact.core.query.TextQuery;
import com.netease.nim.uikit.business.contact.core.viewholder.LabelHolder;
import com.netease.nim.uikit.business.contact.selector.adapter.ContactSelectAdapter;
import com.netease.nim.uikit.business.contact.selector.adapter.ContactSelectAvatarAdapter;
import com.netease.nim.uikit.business.contact.selector.viewholder.ContactsMultiSelectHolder;
import com.netease.nim.uikit.business.contact.selector.viewholder.ContactsSelectHolder;
import com.netease.nim.uikit.business.forward.MultiContactItem;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nim.uikit.impl.NimUIKitImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 联系人选择器
 * <p/>
 * Created by huangjun on 2015/3/3.
 */
public class ContactSelectActivity extends UI implements android.support.v7.widget.SearchView.OnQueryTextListener {

    public static final String EXTRA_DATA = "EXTRA_DATA"; // 请求数据：Option
    public static final String RESULT_DATA = "RESULT_DATA"; // 返回结果
    public static final String RESULT_TYPE_DATA = "RESULT_ClASS_DATA"; // 返回结果
    public static final String RESULT_REMOVE_DATA = "RESULT_REMOVE_DATA"; // 删除的team集合
//    public static final String CHAT_TEAM = "_chatTeam"; // 返回结果
//    public static final String CHAT_P2P = "_chatP2P"; // 返回结果

    // adapter

    private ContactSelectAdapter contactAdapter;

    private ContactSelectAvatarAdapter contactSelectedAdapter;

    // view

    private ListView listView;

    private LivIndex livIndex;
    private EditText etSearch;

    private RelativeLayout bottomPanel;

    private HorizontalScrollView scrollViewSelected;

    private GridView imageSelectedGridView;

    private LinearLayout linearRightBar;
    private TitleBarView titleBarView;
//    private Button btnSelect;

    private SearchView searchView;

    // 搜索关键字
    private String queryText;

    private Option option;
    ArrayList<String> removeList = new ArrayList<>();

    // class

    private static class ContactsSelectGroupStrategy extends ContactGroupStrategy {
        public ContactsSelectGroupStrategy() {
            add(ContactGroupStrategy.GROUP_NULL, -1, "");
            addABC(0);
        }
    }

    /**
     * 联系人选择器配置可选项
     */
    public enum ContactSelectType {
        BUDDY,
        TEAM_MEMBER,
        TEAM
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_contacts_select);
        etSearch = findViewById(R.id.et_search);
        titleBarView = findViewById(R.id.title_bar);
        linearRightBar = (LinearLayout) titleBarView.getRightView();
        titleBarView
                .setBackGroundColor(R.color.color_ededed)
                .setTitleTextColor(R.color.color_2d2d2d)
                .setTitleTextSize(18)
                .setRightText("完成")
                .setLeftText("取消")
                .setTitle("选择联系人")
                .showDivider(false)
                .setRightTextColor(R.color.white)
                .setRightTextBg(R.drawable.bg_send_friend_bg1)
                .setOnLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setOnRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<IContact> contacts = contactSelectedAdapter
                                .getSelectedContacts();
                        if (option.allowSelectEmpty || checkMinMaxSelection(contacts.size())) {
                            ArrayList<String> selectedAccounts = new ArrayList<>();
                            ArrayList<String> contactArrayList = new ArrayList<>();
                            for (IContact contact : contacts) {
                                String contactId = "";
                                contactId = MultiContactItem.spliceContactId(contact.getContactType(), contact.getContactId());
                                selectedAccounts.add(contact.getContactId());
                                contactArrayList.add(contactId);
                            }
                            onSelected(selectedAccounts, contactArrayList);
                        }
                    }
                });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                queryText = query;
                if (TextUtils.isEmpty(query)) {
                    contactAdapter.load(true);
                } else {
                    contactAdapter.query(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        parseIntentData();
        initAdapter();
        initListView();
        initContactSelectArea();
        loadData();
    }

    public static void startActivityForResult(Context context, Option option, int requestCode) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, option);
        intent.setClass(context, ContactSelectActivity.class);

        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    public void onBackPressed() {
        if (searchView != null) {
            searchView.setQuery("", true);
            searchView.setIconified(true);
        }
        showKeyboard(false);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // search view
        getMenuInflater().inflate(R.menu.nim_contacts_search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        if (!option.searchVisible) {
            item.setVisible(false);
            return true;
        }

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                finish();
                return false;
            }
        });
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        this.searchView = searchView;
        this.searchView.setVisibility(option.searchVisible ? View.VISIBLE : View.GONE);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void initAdapter() {
        IContactDataProvider dataProvider;
        if (option.type == ContactSelectType.TEAM_MEMBER && !TextUtils.isEmpty(this.option.teamId)) {
            dataProvider = new ContactDataProviderEx(this.option.teamId, ItemTypes.TEAM_MEMBER);
        } else if (option.type == ContactSelectType.TEAM) {
            option.showContactSelectArea = false;
            dataProvider = new ContactDataProvider(ItemTypes.TEAM);
        } else {
            dataProvider = new ContactDataProvider(ItemTypes.FRIEND);
        }

        // contact adapter
        contactAdapter = new ContactSelectAdapter(ContactSelectActivity.this, new ContactsSelectGroupStrategy(),
                dataProvider) {
            boolean isEmptyContacts = false;

            @Override
            protected List<AbsContactItem> onNonDataItems() {
                return null;
            }

            @Override
            protected void onPostLoad(boolean empty, String queryText, boolean all) {
                if (empty) {
                    if (TextUtils.isEmpty(queryText)) {
                        isEmptyContacts = true;
                    }
                    updateEmptyView(queryText);
                } else {
                    setSearchViewVisible(true);
                }
            }

            private void updateEmptyView(String queryText) {
                if (!isEmptyContacts && !TextUtils.isEmpty(queryText)) {
                    setSearchViewVisible(true);
                } else {
                    setSearchViewVisible(false);
                }
            }

            private void setSearchViewVisible(boolean visible) {
                option.searchVisible = visible;
                if (searchView != null) {
                    searchView.setVisibility(option.searchVisible ? View.VISIBLE : View.GONE);
                }
            }
        };
        contactAdapter.setLoadDataFinishLisenter(new ContactDataAdapter.onLoadDataFinishLisenter() {
            @Override
            public void loadDataFinish(AbsContactDataList datas) {
                if (datas == null) return;
                if (option.isForWard) {
                    ArrayList<String> alreadySelectedForward = option.alreadySelectedForward;

                    if (option.type == ContactSelectType.TEAM) {
                        for (int i = 0; i < datas.getCount(); i++) {
                            AbsContactItem item = datas.getItem(i);
                            if (item instanceof ContactItem) {
                                for (String id : alreadySelectedForward) {
                                    if (id.equals(((ContactItem) item).getContact().getContactId())) {
                                        contactAdapter.selectItem(i);
                                        contactSelectedAdapter.addContact(((ContactItem) item).getContact());
                                        arrangeSelected();
                                    }
                                }
                            }
                        }
                    } else {
                        for (String str : alreadySelectedForward) {
                            if (str.contains(MultiContactItem.CHAT_P2P)) {
                                for (int i = 0; i < datas.getCount(); i++) {
                                    AbsContactItem item = datas.getItem(i);
                                    if (item instanceof ContactItem) {
                                        String substring = MultiContactItem.decodeContactId(str);
                                        if (substring.equals(((ContactItem) item).getContact().getContactId())) {
                                            contactSelectedAdapter.addContact(((ContactItem) item).getContact());
                                            contactAdapter.selectItem(i);
                                        }
                                    }
                                }
                            } else {
                                final String substring = MultiContactItem.decodeContactId(str);
                                contactSelectedAdapter.addContact(new IContact() {
                                    @Override
                                    public String getContactId() {
                                        return substring;
                                    }

                                    @Override
                                    public int getContactType() {
                                        return Type.Team;
                                    }

                                    @Override
                                    public String getDisplayName() {
                                        return null;
                                    }
                                });
                            }
                            arrangeSelected();
                        }
                    }
                }
            }
        });
        Class c = option.multi ? ContactsMultiSelectHolder.class : ContactsSelectHolder.class;
        contactAdapter.addViewHolder(ItemTypes.LABEL, LabelHolder.class);
        contactAdapter.addViewHolder(ItemTypes.FRIEND, c);
        contactAdapter.addViewHolder(ItemTypes.TEAM_MEMBER, c);
        contactAdapter.addViewHolder(ItemTypes.TEAM, c);

        contactAdapter.setFilter(option.itemFilter);
        contactAdapter.setDisableFilter(option.itemDisableFilter);

        // contact select adapter
        contactSelectedAdapter = new ContactSelectAvatarAdapter(this);
    }

    private void parseIntentData() {
        this.option = (Option) getIntent().getSerializableExtra(EXTRA_DATA);
        if (TextUtils.isEmpty(option.maxSelectedTip)) {
            option.maxSelectedTip = "最多选择" + option.maxSelectNum + "人";
        }
        if (TextUtils.isEmpty(option.minSelectedTip)) {
            option.minSelectedTip = "至少选择" + option.minSelectNum + "人";
        }
        setTitle(option.title);
    }

    private class ContactDataProviderEx extends ContactDataProvider {
        private String teamId;

        private boolean loadedTeamMember = false;

        public ContactDataProviderEx(String teamId, int... itemTypes) {
            super(itemTypes);
            this.teamId = teamId;
        }

        @Override
        public List<AbsContactItem> provide(TextQuery query) {
            List<AbsContactItem> data = new ArrayList<>();
            // 异步加载
            if (!loadedTeamMember) {
                TeamMemberDataProvider.loadTeamMemberDataAsync(teamId, new TeamMemberDataProvider.LoadTeamMemberCallback() {
                    @Override
                    public void onResult(boolean success) {
                        if (success) {
                            loadedTeamMember = true;
                            // 列表重新加载数据
                            loadData();
                        }
                    }
                });
            } else {
                data = TeamMemberDataProvider.provide(query, teamId);
            }
            return data;
        }
    }

    private void initListView() {
        listView = findView(R.id.contact_list_view);


        if (option.isShowSelectTeam) {
            View countLayout = View.inflate(this, R.layout.nim_select_contacts_header, null);
            RelativeLayout rlSelectTeam = countLayout.findViewById(R.id.rl_select_team);
            rlSelectTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (option.isForWard) {
                        ContactSelectActivity.Option contactSelectOption = TeamHelper.getForwardSelectTeamOption();
                        contactSelectOption.multi = option.multiForward;
                        contactSelectOption.multiForward = option.multiForward;
                        List<IContact> selectedContacts = contactSelectedAdapter.getSelectedContacts();
                        ArrayList<String> selectedTeams = new ArrayList<>();
                        for (IContact contact : selectedContacts) {
                            if (contact.getContactType() != IContact.Type.Friend) {
                                selectedTeams.add(contact.getContactId());
                            }
                        }
                        contactSelectOption.alreadySelectedForward = selectedTeams;
                        NimUIKit.startContactSelector(
                                ContactSelectActivity.this,
                                contactSelectOption,
                                101
                        );
                    } else {
                        SessionCustomization customization = NimUIKitImpl.getCommonP2PSessionCustomization();
                        if (customization != null) {
                            if (customization.buttons.get(1) != null) {
                                customization.buttons.get(1).onClick(ContactSelectActivity.this, v, "");
                            }
                        }
                    }

                }
            });
            listView.addHeaderView(countLayout);
        }

        listView.setAdapter(contactAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                showKeyboard(false);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                AbsContactItem item = (AbsContactItem) contactAdapter.getItem(position);

                if (item == null) {
                    return;
                }

                if (option.multi) {
                    if (!contactAdapter.isEnabled(position)) {
                        return;
                    }
                    IContact contact = null;
                    if (item instanceof ContactItem) {
                        contact = ((ContactItem) item).getContact();
                    }
                    if (contactAdapter.isSelected(position)) {
                        contactAdapter.cancelItem(position);
                        if (contact != null) {
                            contactSelectedAdapter.removeContact(contact);
                            removeList.add(contact.getContactId());
                        }
                    } else {
                        if (contactSelectedAdapter.getCount() <= option.maxSelectNum) {
                            contactAdapter.selectItem(position);
                            if (contact != null) {
                                if (removeList.contains(contact.getContactId())) {
                                    removeList.remove(contact.getContactId());
                                }
                                contactSelectedAdapter.addContact(contact);
                            }
                        } else {
                            ToastHelper.showToast(ContactSelectActivity.this, option.maxSelectedTip);
                        }

                        if (!TextUtils.isEmpty(queryText) && searchView != null) {
                            searchView.setQuery("", true);
                            searchView.setIconified(true);
                            showKeyboard(false);
                        }
                    }
                    arrangeSelected();
                } else {
                    if (item instanceof ContactItem) {
                        final IContact contact = ((ContactItem) item).getContact();
                        ArrayList<String> selectedIds = new ArrayList<>();
                        selectedIds.add(contact.getContactId());
                        ArrayList<String> contactList = new ArrayList<>();
                        if (contact.getContactType() == IContact.Type.Friend) {
                            contactList.add(contact.getContactId() + MultiContactItem.CHAT_P2P);
                        } else {
                            contactList.add(contact.getContactId() + MultiContactItem.CHAT_TEAM);
                        }
                        onSelected(selectedIds, contactList);
                    }

                    arrangeSelected();
                }
            }
        });

        // 字母导航
        TextView letterHit = (TextView) findViewById(R.id.tv_hit_letter);
        LetterIndexView idxView = (LetterIndexView) findViewById(R.id.liv_index);
        idxView.setLetters(getResources().getStringArray(R.array.letter_list2));
        ImageView imgBackLetter = (ImageView) findViewById(R.id.img_hit_letter);
        if (option.type != ContactSelectType.TEAM) {
            livIndex = contactAdapter.createLivIndex(listView, idxView, letterHit, imgBackLetter);
            livIndex.show();
        } else {
            idxView.setVisibility(View.GONE);
        }
    }

    private void initContactSelectArea() {
//        btnSelect = (Button) findViewById(R.id.btnSelect);
        if (!option.allowSelectEmpty) {
            linearRightBar.setEnabled(false);
        } else {
            linearRightBar.setEnabled(true);
        }
//        btnSelect.setOnClickListener(this);
        bottomPanel = (RelativeLayout) findViewById(R.id.rlCtrl);
        scrollViewSelected = (HorizontalScrollView) findViewById(R.id.contact_select_area);
        if (option.multi) {
            if (option.type != ContactSelectActivity.ContactSelectType.TEAM) {
                bottomPanel.setVisibility(View.VISIBLE);
            }
            if (option.showContactSelectArea) {
                scrollViewSelected.setVisibility(View.VISIBLE);
//                btnSelect.setVisibility(View.VISIBLE);
            } else {
                scrollViewSelected.setVisibility(View.GONE);
//                btnSelect.setVisibility(View.GONE);
            }
            titleBarView.setRightText("确定(0)");
        } else {
            bottomPanel.setVisibility(View.GONE);
        }

        // selected contact image banner
        imageSelectedGridView = findViewById(R.id.contact_select_area_grid);
        imageSelectedGridView.setAdapter(contactSelectedAdapter);
        notifySelectAreaDataSetChanged();
        imageSelectedGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (contactSelectedAdapter.getItem(position) == null) {
                        return;
                    }
                    IContact iContact = contactSelectedAdapter.remove(position);
                    if (iContact != null) {
                        contactAdapter.cancelItem(iContact);
                    }
                    arrangeSelected();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // init already selected items
        List<String> selectedUids = option.alreadySelectedAccounts;
        if (selectedUids != null && !selectedUids.isEmpty()) {
            contactAdapter.setAlreadySelectedAccounts(selectedUids);
            List<ContactItem> selectedItems = contactAdapter.getSelectedItem();
            for (ContactItem item : selectedItems) {
                contactSelectedAdapter.addContact(item.getContact());
            }
            arrangeSelected();
        }
    }

    public void onSelected(ArrayList<String> selects, ArrayList<String> contacts) {
        LogUtils.debug("onSelected-->" + removeList.size());
        Intent intent = new Intent();
        intent.putStringArrayListExtra(RESULT_DATA, selects);
        intent.putStringArrayListExtra(RESULT_TYPE_DATA, contacts);
        intent.putStringArrayListExtra(RESULT_REMOVE_DATA, removeList);
        setResult(Activity.RESULT_OK, intent);
        this.finish();
    }

    private void loadData() {
        contactAdapter.load(true);
    }

    private void arrangeSelected() {
        this.contactAdapter.notifyDataSetChanged();
        if (option.multi) {
            int count = contactSelectedAdapter.getCount();
            if (!option.allowSelectEmpty) {
                linearRightBar.setEnabled(count > 0);
            } else {
                linearRightBar.setEnabled(true);
            }

            if (option.allowSelectEmpty) {
                titleBarView.setRightTextBg(R.drawable.bg_send_friend_bg);
            } else {
                if (count < option.minSelectNum) {
                    titleBarView.setRightTextBg(R.drawable.bg_send_friend_bg1);
                } else {
                    titleBarView.setRightTextBg(R.drawable.bg_send_friend_bg);
                }
            }
            titleBarView.setRightText(getOKBtnText(count));
            notifySelectAreaDataSetChanged();
        }
    }

    private void notifySelectAreaDataSetChanged() {
        // gridView 的itemView 的宽高为 30，文字縮放比例为 fontSizeScale
        //int converViewWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36 * ScreenUtils.getFontSizeScale(), this.getResources().getDisplayMetrics()));
        int itemViewWidth = (int) ((ScreenUtils.getFontSizeScale() * ScreenUtils.dp2px(30)) + ScreenUtils.dp2px(6));
        ViewGroup.LayoutParams layoutParams = imageSelectedGridView.getLayoutParams();
        int gridViewWidth = Math.min(itemViewWidth * contactSelectedAdapter.getCount(), ScreenUtils.getScreenWidth());
        layoutParams.width = gridViewWidth;
        imageSelectedGridView.setLayoutParams(layoutParams);
        imageSelectedGridView.setNumColumns(contactSelectedAdapter.getCount());

        try {
            final int x = layoutParams.width;
            final int y = layoutParams.height;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    scrollViewSelected.scrollTo(x, y);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        contactSelectedAdapter.notifyDataSetChanged();
    }

    private String getOKBtnText(int count) {
        String caption = getString(R.string.ok);
//        int showCount = (count < 1 ? 0 : (count - 1));
        StringBuilder sb = new StringBuilder(caption);
        sb.append(" (");
        sb.append(count);
        if (option.maxSelectNumVisible) {
            sb.append("/");
            sb.append(option.maxSelectNum);
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * ************************** select ************************
     */


    private boolean checkMinMaxSelection(int selected) {

        if (selected == 0) {
            com.laka.androidlib.util.toast.ToastHelper.showToast("请选择联系人");
        }

        if (option.minSelectNum > selected) {
            return showMaxMinSelectTip(true);
        } else if (option.maxSelectNum < selected) {
            return showMaxMinSelectTip(false);
        }
        return true;
    }

    private boolean showMaxMinSelectTip(boolean min) {
        if (min) {
            ToastHelper.showToast(this, option.minSelectedTip);
        } else {
            ToastHelper.showToast(this, option.maxSelectedTip);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (data != null) {
                if (option.isForWard) {
                    ArrayList<String> listExtra = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                    ArrayList<String> listExtra2 = data.getStringArrayListExtra(ContactSelectActivity.RESULT_TYPE_DATA);
                    ArrayList<String> removeList = data.getStringArrayListExtra(ContactSelectActivity.RESULT_REMOVE_DATA);
                    if (listExtra == null) return;
                    if (option.multiForward) {
                        ArrayList<String> nowSelect = new ArrayList<>();
                        for (IContact contact : contactSelectedAdapter.getSelectedContacts()) {
                            if (contact.getContactType() == IContact.Type.Team) {
                                nowSelect.add(contact.getContactId());
                            }
                        }
                        for (final String contact : listExtra) {
                            if (!nowSelect.contains(contact)) {
                                contactSelectedAdapter.addContact(new IContact() {
                                    @Override
                                    public String getContactId() {
                                        return contact;
                                    }

                                    @Override
                                    public int getContactType() {
                                        return Type.Team;
                                    }

                                    @Override
                                    public String getDisplayName() {
                                        return null;
                                    }
                                });
                            }
                        }
                        if (removeList != null && removeList.size() > 0) {
                            for (String str : removeList) {

                                List<IContact> selectedContacts = contactSelectedAdapter.getSelectedContacts();
                                if (selectedContacts != null) {
                                    Iterator<IContact> iterator = selectedContacts.iterator();
                                    while (iterator.hasNext()) {
                                        IContact next = iterator.next();
                                        if (str.equals(next.getContactId())) {
                                            iterator.remove();
                                        }
                                    }
                                }
                            }
                        }
                        arrangeSelected();
                        notifySelectAreaDataSetChanged();
                    } else {
                        onSelected(listExtra, listExtra2);
                    }
                }

            }
        }
    }

    /**
     * ************************* search ******************************
     */

    @Override
    public boolean onQueryTextChange(String query) {
        queryText = query;
        if (TextUtils.isEmpty(query)) {
            this.contactAdapter.load(true);
        } else {
            this.contactAdapter.query(query);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String arg0) {
        return false;
    }

    @Override
    public void finish() {
        showKeyboard(false);
        super.finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static class Option implements Serializable {

        /**
         * 联系人选择器中数据源类型：好友（默认）、群、群成员（需要设置teamId）
         */
        public ContactSelectType type = ContactSelectType.BUDDY;

        /**
         * 联系人选择器数据源类型为群成员时，需要设置群号
         */
        public String teamId = null;

        /**
         * 联系人选择器标题
         */
        public String title = "联系人选择器";

        /**
         * 联系人单选/多选（默认）
         */
        public boolean multi = true;
        //        /**
//         * 联系人单选/多选（默认）
//         */
        public boolean multiForward = false;
        /**
         * 至少选择人数
         */
        public int minSelectNum = 1;

        /**
         * 低于最少选择人数的提示
         */
        public String minSelectedTip = null;

        /**
         * 最大可选人数
         */
        public int maxSelectNum = 2000;

        /**
         * 超过最大可选人数的提示
         */
        public String maxSelectedTip = null;

        /**
         * 是否显示已选头像区域
         */
        public boolean showContactSelectArea = true;

        /**
         * 默认勾选（且可操作）的联系人项
         */
        public ArrayList<String> alreadySelectedAccounts = null;

        /**
         * 需要过滤（不显示）的联系人项
         */
        public ContactItemFilter itemFilter = null;

        /**
         * 需要disable(可见但不可操作）的联系人项
         */
        public ContactItemFilter itemDisableFilter = null;

        /**
         * 是否支持搜索
         */
        public boolean searchVisible = true;

        /**
         * 允许不选任何人点击确定
         */
        public boolean allowSelectEmpty = false;

        /**
         * 是否显示最大数目，结合maxSelectNum,与搜索位置相同
         */
        public boolean maxSelectNumVisible = false;

        /**
         * 是否显示最大数目，结合maxSelectNum,与搜索位置相同
         */
        public boolean isShowSelectTeam = false;

        public boolean isForWard = false;

        /**
         * 默认勾选（且可操作）的联系人项
         */
        public ArrayList<String> alreadySelectedForward = null;
//        public ArrayList<String> alreadySelectedForwardTeam = null;

    }
}
