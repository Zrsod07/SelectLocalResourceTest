package com.example.selectlocalresourcetest;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.selectlocalresourcetest.utils.Constant;

/**
 * Created by cjl on 2018/8/2.
 * =======================================
 * Nothing is true,everything is permitted.
 */

public class SelectPicVidUtils {
    private static final int File_Type_Image = 326;//图片
    private static final int File_Type_Video = 331;//视频
    private static final int File_Type_music = 478;//音频文件

//    private final WeakReference<Activity> mActivity;
//    private final WeakReference<Fragment> mFragment;

    private final Activity mActivity;
    private final Fragment mFragment;

    private SelectPicVidUtils(Activity activity) {
        this(activity, (Fragment)null);
    }

    private SelectPicVidUtils(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private SelectPicVidUtils(Activity activity, Fragment fragment) {
        this.mActivity = activity;
        this.mFragment = fragment;
    }

    /**
     * 实例化 Activity
     * @param activity
     * @return
     */
    public static SelectPicVidUtils create(Activity activity) {
        return new SelectPicVidUtils(activity);
    }

    /**
     * 实例化 Fragment
     * @param fragment
     * @return
     */
    public static SelectPicVidUtils create(Fragment fragment) {
        return new SelectPicVidUtils(fragment);
    }

    /**
     * 传递的参数
     * @param count 可以的文件数量
     * @param fileType 文件类型：File_Type_Image图片File_Type_Video视频File_Type_music音频
     * @param activityResult 用于返回的Result
     */
    public void selectDatas(int count,String fileType,int activityResult){
        if (null==fileType||"".equals(fileType)){
            Toast.makeText(mActivity,"请指定文件类型",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=new Intent(mActivity,SelectLocalResourceActivity.class);
        intent.putExtra(Constant.Files_Count,count);
        intent.putExtra(Constant.Files_Type,fileType);
        mActivity.startActivityForResult(intent,activityResult);
    }
}
