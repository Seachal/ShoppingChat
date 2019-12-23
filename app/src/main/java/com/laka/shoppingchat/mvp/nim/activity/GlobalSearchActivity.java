package com.laka.shoppingchat.mvp.nim.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.laka.androidlib.base.activity.BaseMvpActivity;
import com.laka.androidlib.mvp.IBasePresenter;
import com.laka.androidlib.util.KeyboardHelper;
import com.laka.androidlib.util.LogUtils;
import com.laka.androidlib.util.RsaUtils;
import com.laka.androidlib.widget.dialog.CommonConfirmDialog;
import com.laka.shoppingchat.R;
import com.laka.shoppingchat.mvp.chat.model.bean.QrCodeInfo;
import com.laka.shoppingchat.mvp.nim.constract.INimConstract;
import com.laka.shoppingchat.mvp.nim.model.bean.FriendDataResp;
import com.laka.shoppingchat.mvp.nim.presenter.NimPresenter;
import com.laka.shoppingchat.mvp.nim.session.SessionHelper;
import com.laka.shoppingchat.mvp.nim.session.search.DisplayMessageActivity;
import com.laka.shoppingchat.mvp.user.utils.UserUtils;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.item.MsgItem;
import com.netease.nim.uikit.business.contact.core.model.AbsContactDataList;
import com.netease.nim.uikit.business.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.business.contact.core.model.ContactGroupStrategy;
import com.netease.nim.uikit.business.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.business.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.business.contact.core.viewholder.ContactHolder;
import com.netease.nim.uikit.business.contact.core.viewholder.LabelHolder;
import com.netease.nim.uikit.business.contact.core.viewholder.MsgHolder;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.search.model.MsgIndexRecord;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全局搜索页面
 * 支持通讯录搜索、消息全文检索
 * <p/>
 * Created by huangjun on 2015/4/13.
 */
public class GlobalSearchActivity extends BaseMvpActivity<String> implements OnItemClickListener, INimConstract.IBaseNimView {

    private ContactDataAdapter adapter;

    private ListView lvContacts;

    private SearchView searchView;

    private ImageView ivBack;
    private EditText editQuery;
    private TextView mTvContentEmptry;
    private View headerView;
    private TextView tvPhone;
    private CommonConfirmDialog commonConfirmDialog;
    private NimPresenter mPresenter;


    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, GlobalSearchActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected IBasePresenter createPresenter() {
        mPresenter = new NimPresenter();
        return mPresenter;
    }

    public boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null) {
            searchView.clearFocus();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardHelper.openKeyBoard(GlobalSearchActivity.this, editQuery);
            }
        }, 100);
    }

    @Override
    public int setContentView() {
        return R.layout.global_search_result;
    }

    @Override
    public void initIntent() {

    }

    @Override
    protected void initViews() {
        ivBack = findViewById(R.id.iv_back);
        editQuery = findViewById(R.id.edit_query);
        mTvContentEmptry = findViewById(R.id.tv_content_emptry);
        headerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.global_view_header, null);
        tvPhone = headerView.findViewById(R.id.tv_phone);
        lvContacts = findViewById(R.id.searchResultList);
        lvContacts.setVisibility(View.GONE);


    }

    @Override
    protected void initData() {
        commonConfirmDialog = new CommonConfirmDialog(this);
        SearchGroupStrategy searchGroupStrategy = new SearchGroupStrategy();
        IContactDataProvider dataProvider = new ContactDataProvider(ItemTypes.FRIEND, ItemTypes.TEAM, ItemTypes.MSG);
        adapter = new ContactDataAdapter(this, searchGroupStrategy, dataProvider);
        adapter.addViewHolder(ItemTypes.LABEL, LabelHolder.class);
        adapter.addViewHolder(ItemTypes.FRIEND, ContactHolder.class);
        adapter.addViewHolder(ItemTypes.TEAM, ContactHolder.class);
        adapter.addViewHolder(ItemTypes.MSG, MsgHolder.class);
        lvContacts.addHeaderView(headerView);
        lvContacts.setAdapter(adapter);
    }

    @Override
    protected void initEvent() {
        commonConfirmDialog.setOnClickSureListener(new CommonConfirmDialog.OnClickSureListener() {
            @Override
            public void onClickSure(View view) {

            }
        });
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = tvPhone.getText().toString();
                if (phone.equals(UserUtils.getMobile())) {
                    commonConfirmDialog.show();
                    commonConfirmDialog.setCancelVisibility(View.GONE);
                    commonConfirmDialog.setDefaultTitleTxt("不能添加自己到通讯录");
                } else {
                    mPresenter.searchFriend(phone);
                    //                    query(key)
                    //  这里记得一定要将键盘隐藏了
                    KeyboardHelper.hideKeyBoard(GlobalSearchActivity.this, editQuery);
                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                adapter.query(query);
                adapter.setLoadDataFinishLisenter(new ContactDataAdapter.onLoadDataFinishLisenter() {
                    @Override
                    public void loadDataFinish(AbsContactDataList datas) {
                        if (isNumeric(query) && query.length() == 11) {
                            lvContacts.addHeaderView(headerView);
                            tvPhone.setText(query);
                            lvContacts.setVisibility(View.VISIBLE);
                            mTvContentEmptry.setVisibility(View.GONE);
                        } else {
                            lvContacts.removeHeaderView(headerView);
                            if (adapter.isEmpty()) {
                                lvContacts.setVisibility(View.GONE);
                                mTvContentEmptry.setVisibility(View.VISIBLE);
                            } else {
                                lvContacts.setVisibility(View.VISIBLE);
                                mTvContentEmptry.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        });
        lvContacts.setOnItemClickListener(this);
        lvContacts.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                KeyboardHelper.hideKeyBoard(GlobalSearchActivity.this, lvContacts);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        findViewById(R.id.global_search_root).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void showData(@NonNull String data) {

    }

    @Override
    public void showErrorMsg(String msg) {

    }

    @Override
    public void onSearchFriend(@NotNull FriendDataResp resp) {
        UserInfoActivity.start(
                GlobalSearchActivity.this, RsaUtils.decryptData(
                        this,
                        resp.getAccid()
                )
        );
    }

    @Override
    public void getGroupIdForQrCodeSuccess(@NotNull QrCodeInfo resp) {

    }

    private static class SearchGroupStrategy extends ContactGroupStrategy {
        public static final String GROUP_FRIEND = "FRIEND";
        public static final String GROUP_TEAM = "TEAM";
        public static final String GROUP_MSG = "MSG";

        SearchGroupStrategy() {
            add(ContactGroupStrategy.GROUP_NULL, 0, "");
            add(GROUP_TEAM, 1, "群组");
            add(GROUP_FRIEND, 2, "好友");
            add(GROUP_MSG, 3, "聊天记录");
        }

        @Override
        public String belongs(AbsContactItem item) {
            switch (item.getItemType()) {
                case ItemTypes.FRIEND:
                    return GROUP_FRIEND;
                case ItemTypes.TEAM:
                    return GROUP_TEAM;
                case ItemTypes.MSG:
                    return GROUP_MSG;
                default:
                    return null;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String query = tvPhone.getText().toString();
        if (isNumeric(query) && query.length() == 11) {
            position = position - 1;
        }
        AbsContactItem item = (AbsContactItem) adapter.getItem(position);
        switch (item.getItemType()) {
            case ItemTypes.TEAM: {
                SessionHelper.startTeamSession(this, ((ContactItem) item).getContact().getContactId());
                break;
            }

            case ItemTypes.FRIEND: {
                SessionHelper.startP2PSession(this, ((ContactItem) item).getContact().getContactId());
                break;
            }

            case ItemTypes.MSG: {
                MsgIndexRecord msgIndexRecord = ((MsgItem) item).getRecord();
                if (msgIndexRecord.getCount() > 1) {
                    GlobalSearchDetailActivity2.start(this, msgIndexRecord);
                } else {
//                    DisplayMessageActivity.start(this, msgIndexRecord.getMessage());

                    if (msgIndexRecord.getSessionType() == SessionTypeEnum.Team) {
                        SessionHelper.startTeamSession(this, ((MsgItem) item).getContact().getContactId(), msgIndexRecord.getMessage());
                    } else if (msgIndexRecord.getSessionType() == SessionTypeEnum.P2P) {
                        SessionHelper.startP2PSession(this, ((MsgItem) item).getContact().getContactId(), msgIndexRecord.getMessage());
                    }


                }
                break;
            }

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter.cancelAllTask();
        }
        super.onDestroy();
    }

}
