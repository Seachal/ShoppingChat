package com.netease.nim.uikit.business.session.attachment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public class CustomAttachParser implements MsgAttachmentParser {

    public static final String KEY_TYPE = "msg_type";
    public static final String KEY_DATA = "data";

    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = JSON.parseObject(json);
            int type = object.getInteger(KEY_TYPE);
            JSONObject data = object.getJSONObject(KEY_DATA);
            switch (type) {
                case CustomAttachmentType.redPackage:
                    attachment = new RedPackageAttachment();
                    break;
                case CustomAttachmentType.robRedPackage:
                    attachment = new RobRedPackageAttachment();
                    break;
                default:
                    attachment = new DefaultCustomAttachment();
                    break;
            }
            if (attachment != null) {
                attachment.fromJson(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attachment;
    }

    public static String packData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, type);
        if (data != null) {
            object.put(KEY_DATA, data);
        }
        return object.toJSONString();
    }
}
