package com.example.selectpicvideoutils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.example.selectpicvideoutils.fragment.PhotoFragment;
import com.example.selectpicvideoutils.utils.PhotoViewPager;

import java.util.ArrayList;
import java.util.List;

public class PhototViewActivity extends AppCompatActivity implements View.OnClickListener {

    List<String> list;
    int position = 0;
    private PhotoViewPager photoViewPager;
    private String mDirPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photot_view);
        getSupportActionBar().hide();//隐藏顶部栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        list = new ArrayList<>();
        getData();
        photoViewPager = findViewById(R.id.photoView);
        photoViewPager.setAdapter(new PhotoAdapter(getSupportFragmentManager()));
    }

    public void getData() {
        Bundle bundle = this.getIntent().getBundleExtra("bundle");
        list = bundle.getStringArrayList("mImages");
        position = bundle.getInt("position");
        mDirPath = bundle.getString("mDirPath");
    }

    @Override
    public void onClick(View view) {
        finish();
    }


    class PhotoAdapter extends FragmentStatePagerAdapter {

        public PhotoAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            PhotoFragment pf = new PhotoFragment();
            Bundle bundle = new Bundle();
            if (list != null && !list.isEmpty()) {
                bundle.putString(PhotoFragment.ARG_PATH, mDirPath + list.get(arg0));
            }
            pf.setArguments(bundle);
            return pf;
        }

        @Override
        public int getCount() {
            if (list != null && !list.isEmpty()) {
                return list.size();
            } else {
                return 0;
            }
        }
    }

}
