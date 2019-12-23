package com.laka.androidlib.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @Author:Rayman
 * @Date:2019/1/19
 * @Description:Base64转换工具
 */
public class Base64Utils {

    /**
     * Bitmap位图转base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Base64转Bitmap位图
     *
     * @param base64Str
     * @return
     */
    public static Bitmap stringToBitmap(String base64Str) {
        byte[] bytes = Base64.decode(base64Str, Base64.DEFAULT);
        LogUtils.info("输出base64字符集：" + base64Str + "\nbyte数据:" + bytes.toString());
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Base64转String
     */
    public static String decode2String(String base64Str) {
        byte[] bytes = Base64.decode(base64Str, Base64.DEFAULT);
        return new String(bytes, Charset.defaultCharset());
    }

    public static String encode(String str) {
        byte[] bytes = Base64.encode(str.getBytes(), Base64.DEFAULT);
        return new String(bytes, Charset.defaultCharset());
    }


    /**
     * Base64转byte数组
     *
     * @param base64Str
     * @return
     */
    public static byte[] stringToByteArray(String base64Str) {
        try {
            byte[] data = Base64.decode(base64Str, Base64.DEFAULT);
            for (int i = 0; i < data.length; i++) {
                if (data[i] < 0) {
                    //调整异常数据
                    data[i] += 256;
                }
            }
            return data;
        } catch (IllegalArgumentException e) {
            return new byte[]{};
        }
    }
}
