package com.example.selectlocalresourcetest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.selectpicvideoutils.utils.GlideUtils;
import com.example.selectpicvideoutils.utils.SavePicByUrlUtils;


public class MainActivity extends Activity {

    ImageView ima1,ima2,ima3,ima4,ima5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ima1=findViewById(R.id.ima1);
        ima2=findViewById(R.id.ima2);
        ima3=findViewById(R.id.ima3);
        ima4=findViewById(R.id.ima4);
        ima5=findViewById(R.id.ima5);

        String imageUrl="https://goss.veer.com/creative/vcg/veer/800water/veer-167716257.jpg";
        GlideUtils.load(this,imageUrl,ima1,null);
        ima1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SavePicByUrlUtils.saveImage2Photo(MainActivity.this,ima1);
            }
        });
    }
}
