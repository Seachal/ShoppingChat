package com.netease.nim.uikit.business.team.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.laka.androidlib.base.activity.BaseActivity;
import com.laka.androidlib.util.StatusBarUtil;
import com.laka.androidlib.widget.titlebar.TitleBarView;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.string.StringTextWatcher;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;


public class ChangeTeamNickActivity extends UI {

    public static final String EXTRA_DATA = "EXTRA_DATA";
    private static final String EXTRA_TID = "EXTRA_TID";
    private static final String EXTRA_FIELD = "EXTRA_FIELD";

    // view
    private EditText editText;
    private TextView mTvAlert;

    // data
    private String teamId;

    private TitleBarView titleBarView;


    public static void start(Activity activity, String teamId) {
        Intent intent = new Intent();
        intent.setClass(activity, ChangeTeamNickActivity.class);
        intent.putExtra(EXTRA_TID, teamId);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_team_name_activity);
        titleBarView = findView(R.id.title_bar);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.color_ededed));
        titleBarView.setLeftIcon(R.drawable.selector_nav_btn_back)
                .setTitleTextSize(18)
                .setTitleTextColor(R.color.color_2d2d2d)
                .setBackGroundColor(R.color.color_ededed)
                .setRightTextBg(R.drawable.bg_send_friend_bg)
                .setRightText("保存")
                .setRightTextColor(R.color.white)
                .showDivider(false)
                .setOnRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showKeyboard(false);
                        complete();
                    }
                });
//        ToolBarOptions options = new NimToolBarOptions();
//        setToolBar(R.id.toolbar, options);

        findViews();
        parseIntent();

//        TextView toolbarView = findView(R.id.action_bar_right_clickable_textview);
//        toolbarView.setText(R.string.save);
//        toolbarView.setOnClickListener(this);
    }

    private void parseIntent() {
        teamId = getIntent().getStringExtra(EXTRA_TID);
        initData();
    }

    private void initData() {
        int limit = 0;
        titleBarView.setTitle(getResources().getString(R.string.team_settings_name));
        editText.setHint(R.string.team_settings_set_name);
        mTvAlert.setText("群昵称");
        limit = 10;
        String nameWithoutMe = TeamHelper.getDisplayNameWithoutMe(teamId, NimUIKit.getAccount());
        editText.setText(nameWithoutMe);
        editText.setSelection(nameWithoutMe.length());
        editText.addTextChangedListener(new StringTextWatcher(limit, editText));
    }

    private void findViews() {
        mTvAlert = findViewById(R.id.tv_alert);
        editText = findViewById(R.id.discussion_name);
        editText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP;
            }

        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    complete();
                    return true;
                } else {
                    return false;
                }
            }
        });
        showKeyboardDelayed(editText);

        LinearLayout backgroundLayout = (LinearLayout) findViewById(R.id.background);
        backgroundLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showKeyboard(false);
            }
        });
    }


    /**
     * 点击保存
     */
    private void complete() {
        char[] s = editText.getText().toString().toCharArray();
        int i;
        for (i = 0; i < s.length; i++) {
            if (String.valueOf(s[i]).equals(" ")) {
                ToastHelper.showToast(this, R.string.now_allow_space);
                break;
            }
        }
        if (i == s.length) {
            saveTeamProperty();
        }
    }

    private void saved() {
        finish();
    }

    /**
     * 保存设置
     */
    private void saveTeamProperty() {
        String string = editText.getText().toString();
        if (string.equals("")) {
            string = UserInfoHelper.getUserName(NimUIKitImpl.getAccount());
        }
        NIMClient.getService(TeamService.class).updateMemberNick(teamId, NimUIKit.getAccount(), string)
                .setCallback(
                        new RequestCallback<Void>() {

                            @Override
                            public void onSuccess(Void param) {
                                ToastHelper.showToast(ChangeTeamNickActivity.this, R.string.update_success);
                                saved();
                            }

                            @Override
                            public void onFailed(int code) {
                                ToastHelper.showToast(ChangeTeamNickActivity.this,
                                        String.format(getString(R.string.update_failed), code));
                            }

                            @Override
                            public void onException(Throwable exception) {

                            }
                        });
    }

    @Override
    public void onBackPressed() {
        showKeyboard(false);
        super.onBackPressed();
    }
}
