package com.laka.androidlib.util.imageload;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.laka.androidlib.R;
import com.laka.androidlib.util.LogUtils;
import com.laka.androidlib.util.screen.ScreenUtils;

/**
 * @Author:summer
 * @Date:2019/1/11
 * @Description: Glide 工具类（使用Glide 4.X）
 */
public class GlideLoader {

    public static void displayImage(Fragment fragment, String url, ImageView imageView) {
        GlideApp.with(fragment)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    public static void displayImage(Context activity, String url, ImageView imageView) {
        GlideApp.with(activity)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    /**
     * 异步加载图片，加载成功后可对bitmap进行处理
     */
    public static void loadImage(Context context, int res, final ImageView imageView) {
        GlideApp.with(context)
                .asBitmap()
                .load(res)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    /**
     * 异步加载图片，加载成功后可对bitmap进行处理
     */
    public static void loadImage(Context context, String uri, int placeholder, final ImageView imageView) {
        GlideApp.with(context)
                .asBitmap()
                .load(uri)
                .placeholder(placeholder)
                .error(placeholder)
                .into(imageView);
    }

    /**
     * 异步加载图片，加载成功后可对bitmap进行处理
     */
    public static void loadImage(Context context, String uri, ImageView imageView) {
        GlideApp.with(context)
                .asBitmap()
                .load(uri)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    /**
     * 下载图片
     */
    public static void loadImageForAdapter(Context context, String url, final ImageView imageView) {
        GlideApp.with(context).asBitmap().load(url).placeholder(R.drawable.default_img).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (resource != null) {
                    LogUtils.info("bitmap----height=" + resource.getHeight() + "----width=" + resource.getWidth());
                    int width = ScreenUtils.getScreenWidth();
                    double rotate = resource.getHeight() / (double) resource.getWidth();
                    double height = width * rotate;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                    params.width = width;
                    params.height = (int) height;
                    imageView.setLayoutParams(params);
                    imageView.setImageBitmap(resource);
                }
            }
        });
    }


    /**
     * 加载圆角图片
     */
    public static void displayFilletImage(Fragment fragment, String url, ImageView imageView) {
        GlideApp.with(fragment)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(ScreenUtils.dp2px(5))))
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    /**
     * 加载圆角图片
     */
    public static void displayFilletImage(Context activity, String url, ImageView imageView) {
        GlideApp.with(activity)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(ScreenUtils.dp2px(5))))
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    /**
     * 加载圆角图片
     */
    public static void displayFilletImage(Context activity, String url, int placeholder, ImageView imageView) {
        GlideApp.with(activity)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(ScreenUtils.dp2px(5))))
                .placeholder(placeholder)
                .error(placeholder)
                .into(imageView);
    }

    /**
     * 加载圆角图片
     */
    public static void displayFilletImage(Context activity, int res, int placeholder, ImageView imageView) {
        GlideApp.with(activity)
                .asBitmap()
                .load(res)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(ScreenUtils.dp2px(5))))
                .placeholder(placeholder)
                .error(placeholder)
                .into(imageView);
    }

    /**
     * 加载圆形图片
     */
    public static void displayCircleImage(Fragment fragment, String url, ImageView imageView) {
        GlideApp.with(fragment)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    /**
     * 加载圆形图片
     */
    public static void displayCircleImage(Context activity, String url, ImageView imageView) {
        GlideApp.with(activity)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    /**
     * 加载圆形图片
     */
    public static void displayCircleImage(Context activity, int res, ImageView imageView) {
        GlideApp.with(activity)
                .asBitmap()
                .load(res)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    /**
     * 加载圆形图片
     */
    public static void displayCircleImage(Context activity, String res, int placeholder, ImageView imageView) {
        GlideApp.with(activity)
                .asBitmap()
                .load(res)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .placeholder(placeholder)
                .error(placeholder)
                .into(imageView);
    }

    /**
     * 高斯模糊
     */
    public static void displayBlurImage(Fragment fragment, String url, ImageView imageView) {
        GlideApp.with(fragment)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().transform(new GlideBlurTransformation(fragment.getContext())))
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    /**
     * 高斯模糊
     */
    public static void displayBlurImage(Context activity, String url, ImageView imageView) {
        GlideApp.with(activity)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().transform(new GlideBlurTransformation(activity)))
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(imageView);
    }

    /**
     * 下载图片
     */
    public static void downloadImage(Context context, String url, final DownloadListener listener) {
        GlideApp.with(context).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                listener.onFailed();
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                if (resource != null && !resource.isRecycled()) {
                    listener.onSuccess(resource);
                } else {
                    listener.onFailed();
                }
                return false;
            }
        }).submit();
    }


    /**
     * 下载图片
     */
    public static void downloadImage2(Context context, String url, final DownloadListener listener) {
        GlideApp.with(context)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        listener.onSuccess(bitmap);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        listener.onFailed();
                    }
                });
    }

    public interface DownloadListener {
        void onSuccess(Bitmap resource);

        void onFailed();
    }


}
