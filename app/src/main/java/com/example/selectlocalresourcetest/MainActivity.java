package com.example.selectlocalresourcetest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.example.selectlocalresourcetest.utils.Constant;
import com.example.selectlocalresourcetest.utils.GlideUtils;
import com.example.selectlocalresourcetest.utils.SavePicByUrlUtils;

import java.util.List;

public class MainActivity extends Activity {

    ImageView ima1, ima2, ima3, ima4, ima5;
    private static final int REQUEST_Q = 247;
    private Button btn;

    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path="http://img.zcool.cn/community/01b34f58eee017a8012049efcfaf50.jpg@1280w_1l_2o_100sh.jpg";
        ima1 = findViewById(R.id.img_1);
        ima2 = findViewById(R.id.img_2);
        ima3 = findViewById(R.id.img_3);
        ima4 = findViewById(R.id.img_4);
        ima5 = findViewById(R.id.img_5);

        GlideUtils.load(this,path,ima1,null);
        //长按：保存图片
        ima1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SavePicByUrlUtils.saveImage2Photo(MainActivity.this,ima1);
                return false;
            }
        });
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectPicVidUtils.create(MainActivity.this)
                        .selectDatas(5, Constant.Files_Type_image, REQUEST_Q);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_Q) {
                List<String> list = data.getStringArrayListExtra(Constant.Files_Check_Photo);
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .override(300,300);
                GlideUtils.load(this, list.get(0), ima1, options);
                if (list.size() > 1) {
                    GlideUtils.load(this, list.get(1), ima2, options);
                }
                if (list.size() > 2) {
                    GlideUtils.load(this, list.get(2), ima3, options);
                }
                if (list.size() > 3) {
                    GlideUtils.load(this, list.get(3), ima4, options);
                }
                if (list.size() > 4) {
                    GlideUtils.load(this, list.get(4), ima5, options);
                }
            }
        }
    }


}
