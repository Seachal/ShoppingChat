package com.laka.shoppingchat.mvp.nim.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.laka.androidlib.util.ActivityManager;
import com.laka.androidlib.util.LogUtils;
import com.laka.androidlib.widget.titlebar.TitleBarView;
import com.laka.shoppingchat.R;
import com.laka.shoppingchat.mvp.nim.session.SessionHelper;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.api.model.team.TeamDataChangedObserver;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.business.contact.core.model.ContactGroupStrategy;
import com.netease.nim.uikit.business.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.business.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.business.contact.core.viewholder.ContactHolder;
import com.netease.nim.uikit.business.contact.core.viewholder.LabelHolder;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nim.uikit.impl.cache.DataCacheManager;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

/**
 * 群列表(通讯录)
 * <p/>
 * Created by huangjun on 2015/4/21.
 */
public class TeamListActivity extends UI implements AdapterView.OnItemClickListener {

    private static final String EXTRA_DATA_ITEM_TYPES = "EXTRA_DATA_ITEM_TYPES";
    private static final String EXTRA_DATA_IS_CLOSE = "EXTRA_DATA_IS_CLOSE";

    private ContactDataAdapter adapter;

    private ListView lvContacts;
    private EditText editQuery;
    private TextView countText;
    private View countLayout;
    private int itemType;
    private boolean isClose;

    public static final void start(Context context, int teamItemTypes) {
        Intent intent = new Intent();
        intent.setClass(context, TeamListActivity.class);
        intent.putExtra(EXTRA_DATA_ITEM_TYPES, teamItemTypes);
        context.startActivity(intent);
    }

    public static final void start(Context context, int teamItemTypes, boolean isClose) {
        Intent intent = new Intent();
        intent.setClass(context, TeamListActivity.class);
        intent.putExtra(EXTRA_DATA_ITEM_TYPES, teamItemTypes);
        intent.putExtra(EXTRA_DATA_IS_CLOSE, isClose);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemType = getIntent().getIntExtra(EXTRA_DATA_ITEM_TYPES, ItemTypes.TEAMS.ADVANCED_TEAM);
        isClose = getIntent().getBooleanExtra(EXTRA_DATA_IS_CLOSE, false);
        setContentView(R.layout.group_list_activity);
        //父类已添加，这里重复了
        //ActivityManager.getInstance().addActivity(this);
        TitleBarView titleBarView = findViewById(R.id.title_bar);
        editQuery = findViewById(R.id.edit_query);
        titleBarView.setLeftIcon(R.drawable.selector_nav_btn_back)
                .setTitleTextSize(18)
                .setTitleTextColor(R.color.color_2d2d2d)
                .setBackGroundColor(R.color.color_ededed)
                .showDivider(false)
                .setTitle("群聊");
        editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (TextUtils.isEmpty(query)) {
                    adapter.load(true);
                    countLayout.setVisibility(View.VISIBLE);
                } else {
                    adapter.query(query);
                    countLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        ToolBarOptions options = new NimToolBarOptions();
//        options.titleId = itemType == ItemTypes.TEAMS.ADVANCED_TEAM ? R.string.advanced_team : R.string.normal_team;
//        setToolBar(R.id.toolbar, options);
        lvContacts = findViewById(R.id.group_list);

        GroupStrategy groupStrategy = new GroupStrategy();
        IContactDataProvider dataProvider = new ContactDataProvider(itemType);

        adapter = new ContactDataAdapter(this, groupStrategy, dataProvider) {
            @Override
            protected List<AbsContactItem> onNonDataItems() {
                return null;
            }

            @Override
            protected void onPreReady() {
            }

            @Override
            protected void onPostLoad(boolean empty, String queryText, boolean all) {
            }
        };
        adapter.addViewHolder(ItemTypes.LABEL, LabelHolder.class);
        adapter.addViewHolder(ItemTypes.TEAM, ContactHolder.class);
        countLayout = View.inflate(this, R.layout.nim_contacts_count_item, null);
        countLayout.setClickable(false);
        countText = countLayout.findViewById(R.id.contactCountText);
        lvContacts.addFooterView(countLayout); // 注意：addFooter要放在setAdapter之前，否则旧版本手机可能会add不上
        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(this);
        lvContacts.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                showKeyboard(false);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        // load data
        initData();
        adapter.load(true);

        registerTeamUpdateObserver(true);
    }

    private void initData() {
        //加载群总数
        int count = NIMClient.getService(TeamService.class).queryTeamCountByTypeBlock(TeamTypeEnum.Advanced);
        if (count == 0) {
            if (itemType == ItemTypes.TEAMS.ADVANCED_TEAM) {
                ToastHelper.showToast(TeamListActivity.this, R.string.no_team);
            } else if (itemType == ItemTypes.TEAMS.NORMAL_TEAM) {
                ToastHelper.showToast(TeamListActivity.this, R.string.no_normal_team);
            }
        }
        countText.setText(count + "个群聊");
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AbsContactItem item = (AbsContactItem) adapter.getItem(position);
        if (item == null) return;
        switch (item.getItemType()) {
            case ItemTypes.TEAM:
                if (isClose) {
                    SessionCustomization commonTeamSessionCustomization = NimUIKitImpl.getCommonTeamSessionCustomization();
                    LogUtils.debug("SessionCustomization---->" + commonTeamSessionCustomization);
                    if (commonTeamSessionCustomization != null) {
                        commonTeamSessionCustomization.isClose = true;
                    }
                    finish();
                }
                SessionHelper.startTeamSession(this, ((ContactItem) item).getContact().getContactId());
                break;
        }
    }

    private void registerTeamUpdateObserver(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamDataChangedObserver(teamDataChangedObserver, register);
    }

    TeamDataChangedObserver teamDataChangedObserver = new TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            initData();
            adapter.load(true);
        }

        @Override
        public void onRemoveTeam(Team team) {
            initData();
            adapter.load(true);
        }
    };

    private static class GroupStrategy extends ContactGroupStrategy {
        GroupStrategy() {
            add(ContactGroupStrategy.GROUP_NULL, 0, ""); // 默认分组
        }

        @Override
        public String belongs(AbsContactItem item) {
            switch (item.getItemType()) {
                case ItemTypes.TEAM:
                    return GROUP_NULL;
                default:
                    return null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (teamDataChangedObserver != null) {
            registerTeamUpdateObserver(false);
        }
        if (adapter != null) {
            adapter.cancelAllTask();
        }
        super.onDestroy();
    }
}
