package com.netease.nim.uikit.business.session.actions;

import android.app.Activity;
import android.content.Intent;

import com.laka.androidlib.util.toast.ToastHelper;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.ChatBusinessNavigator;
import com.netease.nim.uikit.business.ChatRouteManager;
import com.netease.nim.uikit.business.session.constant.RedPackageConstant;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.HashMap;

/**
 * @Author:summer
 * @Date:2019/9/21
 * @Description:多功能面板--发红包item的点击处理类
 */
public class RedPacketAction extends BaseAction {

    public static final int CREATE_GROUP_RED_PACKET = 51;
    public static final int CREATE_SINGLE_RED_PACKET = 10;

    public RedPacketAction() {
        super(R.drawable.message_plus_rp_selector, R.string.red_package);
    }

    @Override
    public void onClick() {
        int requestCode;
        if (getContainer().sessionType == SessionTypeEnum.Team) {
            requestCode = makeRequestCode(CREATE_GROUP_RED_PACKET);
        } else if (getContainer().sessionType == SessionTypeEnum.P2P) {
            requestCode = makeRequestCode(CREATE_SINGLE_RED_PACKET);
        } else {
            return;
        }
        //todo 进入发红包页面
        HashMap<String, Object> params = new HashMap<>();
        params.put(RedPackageConstant.KEY_SESSION_TYPE, getContainer().sessionType);
        params.put(RedPackageConstant.KEY_USER_ACCOUNT, getAccount());
        ChatRouteManager.get(ChatRouteManager.ROUTE_SEND_RED_PACKAGE_ACTIVITY).onJump(getActivity(), params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        //sendRpMessage(data);
    }

    /**
     * 发送自定义红包消息
     */
    private void sendRpMessage(Intent data) {
        //ToastHelper.showCenterToast("发送红包成功");
    }
}
