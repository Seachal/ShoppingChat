package com.laka.androidlib.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Lyf
 * @CreateTime 2018/2/1
 * @Description 手机信息相关工具类
 **/
public final class PhoneUtils {

    private PhoneUtils() {
        throw new UnsupportedOperationException("do not instantiate me , please.");
    }

    /**
     * 判别手机是否为正确手机号码
     *
     * @param phone 手机号码
     */
    public static boolean isPhoneNum(String phone) {
        if (phone == null || phone.length() == 0) {
            return false;
        }
        //正则先放弃，本地只做长度判断。正则交给后台
//        Pattern p = Pattern
//                .compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))[0-9]{8}$");
//        Matcher m = p.matcher(phone);
//        return m.matches();
        return phone.length() == 11;
    }
}
