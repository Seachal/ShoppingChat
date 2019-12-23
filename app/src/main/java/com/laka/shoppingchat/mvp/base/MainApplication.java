package com.laka.shoppingchat.mvp.base;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback;
import com.bumptech.glide.Glide;
import com.laka.androidlib.base.application.BaseApplication;
import com.laka.androidlib.util.InterceptorUtils;
import com.laka.androidlib.util.LogUtils;
import com.laka.shoppingchat.BuildConfig;
import com.laka.shoppingchat.common.interceptor.GlobalParamsInterceptor;
import com.laka.shoppingchat.common.interceptor.IllegalTokenInterceptor;
import com.laka.shoppingchat.common.util.MetaDataUtils;
import com.laka.shoppingchat.common.util.share.WxHelper;
import com.laka.shoppingchat.mvp.nim.NIMInitManager;
import com.laka.shoppingchat.mvp.nim.NimSDKOptionConfig;
import com.laka.shoppingchat.mvp.nim.contact.ContactHelper;
import com.laka.shoppingchat.mvp.nim.mixpush.DemoMixPushMessageHandler;
import com.laka.shoppingchat.mvp.nim.mixpush.DemoPushContentProvider;
import com.laka.shoppingchat.mvp.nim.preference.UserPreferences;
import com.laka.shoppingchat.mvp.nim.session.SessionHelper;
import com.laka.shoppingchat.mvp.user.utils.UserUtils;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.business.contact.core.query.PinYin;
import com.netease.nim.uikit.business.recent.RpOpenedMessageFilter;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.mixpush.NIMPushClient;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.simple.spiderman.SpiderMan;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import cn.jiguang.verifysdk.api.JVerificationInterface;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * @Author:Rayman
 * @Date:2018/12/15
 * @Description:项目Application
 */

public class MainApplication extends BaseApplication {

    public static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        //放在其他库初始化前
        SpiderMan.init(this);
        initAliBaiChuan();
        initImageLoader();
        initBugly();
        initUMeng();
        initImUI();
        initJpush();
        initJverification();
        initWechatSDK();
        initCustomInterceptor();
        closeAndroidPDialog();
        initLeakCanary();
        initNIM();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    private void initNIM() {
//        DemoCache.setContext(this);
        // 4.6.0 开始，第三方推送配置入口改为 SDKOption#mixPushConfig，旧版配置方式依旧支持。
        NIMClient.init(this, getLoginInfo(), NimSDKOptionConfig.getSDKOptions(this));
        if (NIMUtil.isMainProcess(this)) {

            // 注册自定义推送消息处理，这个是可选项
            NIMPushClient.registerMixPushMessageHandler(new DemoMixPushMessageHandler());

            // 初始化红包模块，在初始化UIKit模块之前执行
//            NIMRedPacketClient.init(this);
            // init pinyin
            PinYin.init(this);
            PinYin.validate();
            // 初始化UIKit模块
            initUIKit();
            // 初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
            //关闭撤回消息提醒
//            NIMClient.toggleRevokeMessageNotification(false);
            // 云信sdk相关业务初始化
            NIMInitManager.getInstance().init(true);
            // 初始化音视频模块
//            initAVChatKit();
            // 初始化rts模块
//            initRTSKit();
            //注册消息过滤器
            registerMessageFilter();
        }
    }

    //消息过滤器
    private void registerMessageFilter() {
        RpOpenedMessageFilter.startFilter();
//        RpOpenedMessageFilter22.startFilter();
    }

    private void initUIKit() {
        // 初始化
        NimUIKit.init(this, buildUIKitOptions(), UserUtils.getImAccount());

        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
//        NimUIKit.setLocationProvider(new NimDemoLocationProvider());

        // IM 会话窗口的定制初始化。
        SessionHelper.init();

        // 聊天室聊天窗口的定制初始化。
//        ChatRoomSessionHelper.init();


        // 通讯录列表定制初始化
        ContactHelper.init();

        // 添加自定义推送文案以及选项，请开发者在各端（Android、IOS、PC、Web）消息发送时保持一致，以免出现通知不一致的情况
        NimUIKit.setCustomPushContentProvider(new DemoPushContentProvider());

//        NimUIKit.setOnlineStateContentProvider(new DemoOnlineStateContentProvider());
    }

    private UIKitOptions buildUIKitOptions() {
        UIKitOptions options = new UIKitOptions();
        options.audioRecordMaxTime = 60;
        // 设置app图片/音频/日志等缓存目录
        options.appCacheDir = NimSDKOptionConfig.getAppCacheDir(this) + "/app";
        return options;
    }

    private LoginInfo getLoginInfo() {
        UserUtils.INSTANCE.updateLocalUserInfo();
        String account = UserUtils.getImAccount();
        String token = UserUtils.getImToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {//1
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    /**
     * 初始化公共拦截器，解决androidlib 不能反向调用 main 应用的痛点
     */
    private void initCustomInterceptor() {
        InterceptorUtils.addCommonInterceptor(new GlobalParamsInterceptor());
        InterceptorUtils.addCommonInterceptor(new IllegalTokenInterceptor());
    }

    private void initWechatSDK() {
        WxHelper wxHelper = new WxHelper(this);
    }

    private void initJpush() {
        String channel = MetaDataUtils.getMateDataForApplicationInfo("UMENG_CHANNEL");
        LogUtils.info("application_channel:" + channel);
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);
        JPushInterface.setChannel(this, channel);
        //设置标签
        if (BuildConfig.DEBUG) {
            Set<String> tags = new HashSet<>();
            //tags.add("Rayman");
            JPushInterface.setTags(this, tags, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    Log.d(TAG, "setTags . set = " + set.toString() + " ;result = " + i);
                }
            });
        }
    }

    private void initJverification() {
        JVerificationInterface.init(this);
        JVerificationInterface.setDebugMode(BuildConfig.DEBUG);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initImageLoader() {

    }

    /**
     * 初始化阿里百川
     */
    private void initAliBaiChuan() {
        AlibcTradeSDK.asyncInit(this, new AlibcTradeInitCallback() {
            @Override
            public void onSuccess() {
                LogUtils.error("成功");
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.error(i + ":" + s);
            }
        });
    }

    private void initUMeng() {
        /**
         * 由于基类的 activity 存放于 androidlib 中，所以将友盟SDK放到 androidlib 中，这样就可以直接在基类中
         * 进行友盟的配置了
         * */
        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数3:Push推送业务的secret
         */
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
    }

    private void initBugly() {
        CrashReport.initCrashReport(getApplicationContext(), "047325753e", BuildConfig.DEBUG);
    }

    private void initImUI() {
        // LitePal.initialize(this);
        //初始化融云
        // initRongCloud();
        //初始化红包
        // initRedPacket();
        //初始化仿微信控件ImagePicker
        //initImagePicker();
    }


    /**
     * 禁止android P 非官方sdk 访问限制弹窗，下一个版本再进行兼容
     */
    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
