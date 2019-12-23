package com.laka.androidlib.util.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.security.MessageDigest;

/**
 * @Author:summer
 * @Date:2019/7/17
 * @Description:高斯模糊Transformation
 */
public class GlideBlurTransformation extends CenterCrop {

    private Context context;

    public GlideBlurTransformation(Context context) {
        this.context = context;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = super.transform(pool, toTransform, outWidth, outHeight);
        return BlurBitmapUtil.getInstance().blurBitmap(context, bitmap, 25, (int) (outWidth * 0.5), (int) (outHeight * 0.5));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
    }
}
