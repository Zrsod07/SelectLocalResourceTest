package com.example.selectpicvideoutils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.selectpicvideoutils.sample.adapter.ShowPhotoListAdapter;
import com.example.selectpicvideoutils.sample.utils.ImageFloder;
import com.example.selectpicvideoutils.sample.view.ListImageDirPopupWindow;
import com.example.selectpicvideoutils.utils.Constant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SelectLocalResourceActivity extends Activity implements ListImageDirPopupWindow.OnImageDirSelected, View.OnClickListener {

    int totalCount = 0;
    private ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    private String fileType;
    private int mScreenHeight;
    private TextView tv_samplecamera_confirm;
    private TextView tv_return;
    private TextView mChooseDir;
    private TextView mImageCount;
    private RelativeLayout mBottomLy;
    private int filesCount = -1;
    private ListImageDirPopupWindow mListImageDirPopupWindow;
    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;
    //    private GridView mGirdView;
//    private MyAdapter mAdapter;
    private ShowPhotoListAdapter mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();
    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_local_resource);
        initView();
    }

    private void initView() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mRecyclerView = (RecyclerView) findViewById(R.id.listview_photo);
        tv_samplecamera_confirm = (TextView) findViewById(R.id.tv_samplecamera_confirm);
        tv_samplecamera_confirm.setOnClickListener(this);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        tv_return = (TextView) findViewById(R.id.tv_return);
        mImageCount = (TextView) findViewById(R.id.id_total_count);
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        if (Constant.Files_Type_image.equals(fileType)) {
            mChooseDir.setText("图片");
        } else if (Constant.Files_Type_video.equals(fileType)){
            mChooseDir.setText("视频");
        }
        initEvent();
        getBundleDatas();
        getImages(fileType);
        //适配器
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                3, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(2, 2, 2, 2);
            }
        });
    }

    private void getBundleDatas() {
        filesCount = this.getIntent().getExtras().getInt(Constant.Files_Count);
        fileType = this.getIntent().getExtras().getString(Constant.Files_Type);
    }

    private void initEvent() {
        tv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow.setAnimationStyle(R.style.popmenu_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     *
     * @param typeStr image 图片 video 视频
     */
    private void getImages(final String typeStr) {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String firstImage = null;
                ContentResolver mContentResolver = SelectLocalResourceActivity.this
                        .getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor = null;
                if (Constant.Files_Type_image.equals(typeStr)) {
                    Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;//从外置内存卡获取图片uri
                    mCursor = mContentResolver.query(mImageUri, null,
                            MediaStore.Images.Media.MIME_TYPE + "=? or "
                                    + MediaStore.Images.Media.MIME_TYPE + "=?",
                            new String[]{"image/jpeg", "image/png"},
                            MediaStore.Images.Media.DATE_MODIFIED);
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));
                        Log.e("TAG", path);
                        // 拿到第一张图片的路径
                        if (firstImage == null)
                            firstImage = path;
                        // 获取该图片的父路径名
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null)
                            continue;
                        String dirPath = parentFile.getAbsolutePath();
                        ImageFloder imageFloder = null;
                        // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                        if (mDirPaths.contains(dirPath)) {
                            continue;
                        } else {
                            mDirPaths.add(dirPath);
                            // 初始化imageFloder
                            imageFloder = new ImageFloder();
                            imageFloder.setDir(dirPath);
                            imageFloder.setFirstImagePath(path);
                        }
                        int picSize = 0;
                        try {
                            picSize = parentFile.list(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String filename) {
                                    if (filename.endsWith(".jpg")
                                            || filename.endsWith(".png")
                                            || filename.endsWith(".jpeg"))
                                        return true;
                                    return false;
                                }
                            }).length;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        totalCount += picSize;
                        imageFloder.setCount(picSize);
                        mImageFloders.add(imageFloder);
                        if (picSize > mPicsSize) {
                            mPicsSize = picSize;
                            mImgDir = parentFile;
                        }
                    }
                    mCursor.close();
                } else if (Constant.Files_Type_image.equals(typeStr)){
                    Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    mCursor = mContentResolver.query(mVideoUri, null,
                            MediaStore.Video.Media.MIME_TYPE + "=? ",
                            new String[]{"video/mp4"},
                            MediaStore.Video.Media.DATE_MODIFIED);
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor
                                .getColumnIndex(MediaStore.Video.Media.DATA));
                        Log.e("TAG", path);
                        // 拿到第一张图片的路径
                        if (firstImage == null)
                            firstImage = path;
                        // 获取该图片的父路径名
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null)
                            continue;
                        String dirPath = parentFile.getAbsolutePath();
                        ImageFloder imageFloder = null;
                        // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                        if (mDirPaths.contains(dirPath)) {
                            continue;
                        } else {
                            mDirPaths.add(dirPath);
                            // 初始化imageFloder
                            imageFloder = new ImageFloder();
                            imageFloder.setDir(dirPath);
                            imageFloder.setFirstImagePath(path);
                        }
                        int picSize = 0;
                        try {
                            picSize = parentFile.list(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String filename) {
                                    if (filename.endsWith(".mp4") || filename.endsWith(".3GP"))
                                        return true;
                                    return false;
                                }
                            }).length;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        totalCount += picSize;
                        imageFloder.setCount(picSize);
                        mImageFloders.add(imageFloder);
                        if (picSize > mPicsSize) {
                            mPicsSize = picSize;
                            mImgDir = parentFile;
                        }
                    }
                    mCursor.close();
                }
                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0);

            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    // 为View绑定数据
                    data2View();
                    // 初始化展示文件夹的popupWindw
                    initListDirPopupWindw();
                    break;
            }

        }
    };

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgDir == null) {
            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "图片没扫描到",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mImgs = Arrays.asList(mImgDir.list());
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new ShowPhotoListAdapter(this, getFiles(), mImgDir.getAbsolutePath(),filesCount);
        mRecyclerView.setAdapter(mAdapter);
        mImageCount.setText(totalCount + "张");
//        itemOnClick(getFiles());
        mProgressDialog.dismiss();
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                (int) ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.view_cameralist_dir, null));
        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    private List<String> getFiles() {
        List<String> mImages = new ArrayList<>();
        if (Constant.Files_Type_image.equals(fileType)) {
            for (String imgStr : mImgs) {
                if (imgStr.endsWith(".mp4")) {
                    continue;
                } else {
                    mImages.add(imgStr);
                }
            }
        } else {
            for (String imgStr : mImgs) {
                if (imgStr.endsWith(".mp4")) {
                    mImages.add(imgStr);
                } else {
                    continue;
                }
            }
        }
        return mImages;
    }

    @Override
    public void onClick(View view) {
        ArrayList<String> photoChecks = mAdapter.getPhotoPath();
        if (photoChecks.size() > filesCount) {
            Toast.makeText(this,"只能选择" + filesCount + "张照片",Toast.LENGTH_SHORT).show();
            return;
        }
        if (null==photoChecks||photoChecks.size()==0){
            Toast.makeText(this,"未选择文件",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra(Constant.Files_Check_Photo, photoChecks);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void selected(ImageFloder floder) {
        mImgDir = new File(floder.getDir());
        if ("image".equals(fileType)) {
            mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.endsWith(".jpg") || filename.endsWith(".png")
                            || filename.endsWith(".jpeg"))
                        return true;
                    return false;
                }
            }));
        } else {
            mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.endsWith(".mp4"))
                        return true;
                    return false;
                }
            }));
        }
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new ShowPhotoListAdapter(this, getFiles(), mImgDir.getAbsolutePath(),filesCount);
        mRecyclerView.setAdapter(mAdapter);
        mImageCount.setText(floder.getCount() + "张");
        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();
    }

    private void itemOnClick(final List<String> mImages){
        mAdapter.setOnItemClickListener(new ShowPhotoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(SelectLocalResourceActivity.this,PhototViewActivity.class);
                Bundle bundle=new Bundle();
                bundle.putStringArrayList("mImages", (ArrayList<String>) mImages);
                bundle.putInt("position",position);
                bundle.putString("mDirPath",mImgDir.getAbsolutePath() + "/");
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });


    }

}
