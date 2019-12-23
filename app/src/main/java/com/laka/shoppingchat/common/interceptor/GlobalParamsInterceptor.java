package com.laka.shoppingchat.common.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.laka.androidlib.util.LogUtils;
import com.laka.androidlib.util.StringUtils;
import com.laka.shoppingchat.BuildConfig;
import com.laka.shoppingchat.common.util.device.DeviceInfoUtils;
import com.laka.shoppingchat.mvp.user.utils.UserUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * @Author:summer
 * @Date:2019/3/25
 * @Description:公共参数拦截器，与AuthorizationInterceptor不同，当前拦截器涉及一些业务逻辑
 */
public class GlobalParamsInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        String method = request.method();
        String isRedPackage = request.header("isRedPackage");
        if (!StringUtils.isEmpty(isRedPackage)
                && isRedPackage.equals("1")) {
            if (method.equals("POST")) {
                return chainForPost(chain, request, new HashMap<>());
            } else {
                return chainForGet(chain, request, new HashMap<>());
            }
        }

        //红包接口不需要添加多余的公共参数
        Map<String, String> commonParams = new HashMap<>();//公共参数
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        commonParams.put("build", versionCode + "");
        commonParams.put("version", versionName);
        commonParams.put("platform", "Android");
        commonParams.put("deviceIDFA", DeviceInfoUtils.getDeviceId());
        commonParams.put("deviceInfo", DeviceInfoUtils.getPhoneProducerAndModel());
        commonParams.put("deviceUUID", DeviceInfoUtils.getUUIDAndroidId());
        if (request.url().toString().contains(BuildConfig.ERGOU_BASE_HOST)) {
            commonParams.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } else {
            commonParams.put("timestamp", System.currentTimeMillis() / 1000 + "");
        }
        LogUtils.info("deviceUUID:" + DeviceInfoUtils.getUUIDAndroidId() + DeviceInfoUtils.getDeviceId() + "-------deviceIDFA;" + DeviceInfoUtils.getAndroidId());
        if (UserUtils.INSTANCE.isLogin() && !TextUtils.isEmpty(UserUtils.INSTANCE.getUserToken())) {
            commonParams.put("token", UserUtils.INSTANCE.getUserToken());
        }
        if (UserUtils.INSTANCE.isLogin() && !TextUtils.isEmpty(UserUtils.INSTANCE.getUserId() + "")) {
            commonParams.put("user_id", UserUtils.INSTANCE.getUserId() + "");
        }
        if (method.equals("GET")) {
            return chainForGet(chain, request, commonParams);
        } else if (method.equals("POST")) {
            return chainForPost(chain, request, commonParams);
        }
        return chainForPost(chain, request, commonParams);
    }

    /**
     * 创建nonce，6位随机数，得确保同一个用户60秒内不会出现重复
     */
    private String createNonce() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        return timeStamp.substring(timeStamp.length() - 6).toUpperCase();
    }

    /**
     * 安全校验参数，sha1(time+nonce+用户token)
     */
    private String createParamsSum(String time, String nonce) {
        String content = time + nonce + UserUtils.getImToken();
        String shaResultContent = "";
        try {
            shaResultContent = sha1Encode(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            return shaResultContent;
        }
    }

    /**
     * sha1
     */
    public static String sha1Encode(String inStr) throws UnsupportedEncodingException {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    @NonNull
    private Response chainForGet(Chain chain, Request request, Map<String, String> commonParams) throws IOException {
        Set<String> keySet = commonParams.keySet();
        HttpUrl.Builder builder = request.url().newBuilder();
        for (String key : keySet) {
            String value = commonParams.get(key);
            builder.addQueryParameter(key, value);
        }
        //============================== 购聊接口安全校验 start ====================================
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = createNonce();
        String sum = createParamsSum(time, nonce);
        //============================== 购聊接口安全校验 end ======================================
        HttpUrl newUrl = builder.build();
        Request newRequest = request.newBuilder()
                .url(newUrl)
                .addHeader("time", time)
                .addHeader("nonce", nonce)
                .addHeader("user", UserUtils.getImAccount())
                .addHeader("sum", sum)
                .build();
        return chain.proceed(newRequest);
    }

    /**
     * post方法请求链路
     */
    private Response chainForPost(Chain chain, Request request, Map<String, String> commonParams) throws IOException {
        String bodyParams = bodyToString(request.body());
        StringBuilder buff = new StringBuilder();
        Set<String> addParamsSet = commonParams.keySet();
        if (!TextUtils.isEmpty(bodyParams)) {
            buff.append(bodyParams);
            buff.append("&");
        }
        for (String key : addParamsSet) {
            buff.append(key + "=" + commonParams.get(key) + "&");
        }
        String resultParams = "";
        if (!TextUtils.isEmpty(buff)) {
            resultParams = buff.toString().substring(0, buff.toString().length() - 1);
        }
        LogUtils.info("allParams：" + resultParams);
        //============================== 购聊接口安全校验 start ====================================
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = createNonce();
        String sum = createParamsSum(time, nonce);
        //============================== 购聊接口安全校验 end ======================================
        // 將公共参数添加到表单中
        LogUtils.info("user_token------" + UserUtils.getImAccount());
        Request newRequst = request.newBuilder()
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), resultParams))
                .addHeader("time", time)
                .addHeader("nonce", nonce)
                .addHeader("user", UserUtils.getImAccount())
                .addHeader("sum", sum)
                .build();
        return chain.proceed(newRequst);
    }

    private String bodyToString(final RequestBody requestBody) {
        try {
            final RequestBody copy = requestBody;
            final Buffer buffer = new Buffer();
            if (copy != null) {
                copy.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
