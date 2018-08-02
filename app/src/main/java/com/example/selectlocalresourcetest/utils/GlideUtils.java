package com.example.selectlocalresourcetest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.selectlocalresourcetest.R;

/**
 * Created by cjl on 2018/8/2.
 * =======================================
 * Nothing is true,everything is permitted.
 */
public class GlideUtils {

    /**
     * 加载自定义形状的图片
     *
     * @param context
     * @param url
     * @param imageView
     * @param options
     */
    public static void load(Context context, String url, ImageView imageView, RequestOptions options) {
        if (null==options){
            options=new RequestOptions();
        }
        options.placeholder(R.drawable.ic_pic_loading)//占位符
                .error(R.drawable.ic_pic_loading_fail);//加载错误显示
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
    }

    /**
     * 加载圆角的options
     * 在RequestOptions里调用.bitmapTransform(GlideUtils.getRoundOptions(Context context, int p))
     * @param context
     * @param p  长度
     */
    public static Transformation<Bitmap> getRoundOptions(Context context, int p) {
        return new GlideRoundTransform(context, p);
    }

//    public void loadImage(Context mContext, String url) {
//        RequestOptions options = new RequestOptions()
//                .centerCrop()//居中
//                .circleCrop()//设置圆形
//                .placeholder(R.drawable.ic_pic_loading)//占位符
//                .error(R.drawable.ic_pic_loading_fail)//加载失败
//                .override(200, 200)//指定图片大小（通常Glide会以ImageView大小为准，不需要设定）
//                .override(Target.SIZE_ORIGINAL)//加载图片的原始尺寸
//                .skipMemoryCache(true)//true表示禁用内存缓存（默认开启，一般不禁用）
//                .diskCacheStrategy(DiskCacheStrategy.NONE);//禁用硬盘缓存（默认开启，一般不禁用）
//        DiskCacheStrategy.NONE： 表示不缓存任何内容
//        DiskCacheStrategy.DATA： 表示只缓存原始图片
//        DiskCacheStrategy.RESOURCE： 表示只缓存转换过后的图片
//        DiskCacheStrategy.ALL ： 表示既缓存原始图片，也缓存转换过后的图片
//        DiskCacheStrategy.AUTOMATIC： 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）
//        .priority(Priority.HIGH)
//        Glide.with(mContext)
//                .asBitmap()//只允许加载静态图片（如果是gif则加载第一帧，只会显示图片,在4.0中这个方法要在load之前）
//                .load(url)
//                .preload();//预加载（之后在加载同一个url的图片时，可以直接加载）
//        更多Glide4.x,看：https://blog.csdn.net/guolin_blog/article/details/78582548
//    }


}
