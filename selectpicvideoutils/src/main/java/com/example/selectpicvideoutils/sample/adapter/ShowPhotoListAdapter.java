package com.example.selectpicvideoutils.sample.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.selectpicvideoutils.PhototViewActivity;
import com.example.selectpicvideoutils.R;
import com.example.selectpicvideoutils.utils.GlideUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ShowPhotoListAdapter extends RecyclerView.Adapter<ShowPhotoListAdapter.MyViewHolde> {
    private int width;
    private Context context;
    protected List<String> list;
    protected LayoutInflater mInflater;
    Set<Integer> isSelected;
    private int count;

    /**
     * 文件夹路径
     */
    private String mDirPath;

    public ShowPhotoListAdapter(Context context, List<String> list, String dirPath,int count) {
        this.context = context;
        this.list = list;
        isSelected = new HashSet<>();
        this.mInflater = LayoutInflater.from(context);
        this.mDirPath = dirPath;
        width = context.getResources().getDisplayMetrics().widthPixels / 3;
        this.count=count;
    }

    @Override
    public MyViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_show_photo_list_view, parent, false);
        MyViewHolde myViewHolde = new MyViewHolde(view, width);
        return myViewHolde;
    }


    @Override
    public void onBindViewHolder(final MyViewHolde holder, final int position) {
        String imagepath;
        if (null == mDirPath) {
            imagepath = list.get(position);
        } else {
            imagepath = mDirPath + "/" + list.get(position);
        }

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .bitmapTransform(GlideUtils.getRoundOptions(context, 20));//圆角
        GlideUtils.load(context, imagepath, holder.image, options);
        holder.ckb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected.size() >= count && holder.ckb.isChecked()) {
                    holder.ckb.setChecked(false);
                    Toast.makeText(context, "最多只能选"+count+"张", Toast.LENGTH_SHORT).show();
                } else {
                    if (holder.ckb.isChecked()) {
                        isSelected.add(position);
                    } else {
                        isSelected.remove(position);
                    }
                }
            }
        });
        holder.ckb.setChecked(isSelected.contains(position));//把选中的position放入isSelected
//        //图片
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PhototViewActivity.class);
                Bundle bundle=new Bundle();
                bundle.putStringArrayList("mImages", (ArrayList<String>) list);
                bundle.putInt("position",position);
                bundle.putString("mDirPath",mDirPath + "/");
                intent.putExtra("bundle",bundle);
                context.startActivity(intent);
            }
        });

        setUpItemEvent(holder);
    }

    public ArrayList<String> getPhotoPath() {
        ArrayList<String> path = new ArrayList<>();
        if (null == mDirPath) {
            for (Integer pos : isSelected) {
                path.add(list.get(pos));
            }
        } else {
            for (Integer pos : isSelected) {
                path.add(mDirPath + "/" + list.get(pos));
            }
        }
        return path;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //点击事件
    public OnItemClickListener mOnItemClicklistener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClicklistener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public static class MyViewHolde extends RecyclerView.ViewHolder {
        ImageView image;
        CheckBox ckb;

        public MyViewHolde(View itemView, int width) {
            super(itemView);
            image =  itemView.findViewById(R.id.image);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
            image.setLayoutParams(layoutParams);
            ckb =  itemView.findViewById(R.id.ckb);
        }
    }

    protected void setUpItemEvent(final MyViewHolde arg0) {
        if (mOnItemClicklistener != null) {
            //
            arg0.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int layoutpos = arg0.getAdapterPosition();// 找到所点击的posiong
                    mOnItemClicklistener.onItemClick(arg0.image, layoutpos);
                }
            });
        }
    }
}