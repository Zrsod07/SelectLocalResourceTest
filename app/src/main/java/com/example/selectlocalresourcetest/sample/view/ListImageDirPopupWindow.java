package com.example.selectlocalresourcetest.sample.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.selectlocalresourcetest.R;
import com.example.selectlocalresourcetest.sample.adapter.CommonAdapter;
import com.example.selectlocalresourcetest.sample.adapter.ViewHolder;
import com.example.selectlocalresourcetest.sample.utils.ImageFloder;

import java.util.List;


public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFloder> {
    private ListView mListDir;
    private OnImageDirSelected mImageDirSelected;

    public ListImageDirPopupWindow(int width, int height,
                                   List<ImageFloder> mImageFloders, View convertView) {
        super(convertView, width, height, true, mImageFloders);
    }

    @Override
    public void initViews() {
        mListDir = (ListView) findViewById(R.id.id_list_dir);
        mListDir.setAdapter(new CommonAdapter<ImageFloder>(context, mDatas,
                R.layout.view_cameralist_dir_item) {
            @Override
            public void convert(ViewHolder helper, ImageFloder item) {
                helper.setText(R.id.id_dir_item_name, item.getName());
                helper.setImageByUrl(R.id.id_dir_item_image,
                        item.getFirstImagePath());
                helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
            }
        });
    }

    public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
        this.mImageDirSelected = mImageDirSelected;
    }

    @Override
    public void initEvents() {
        mListDir.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (mImageDirSelected != null) {
                    mImageDirSelected.selected(mDatas.get(position));
                }
            }
        });
    }

    @Override
    public void init() {

    }

    @Override
    protected void beforeInitWeNeedSomeParams(Object... params) {

    }

    public interface OnImageDirSelected {
        void selected(ImageFloder floder);
    }

}
