package com.example.selectpicvideoutils.fragment;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.selectpicvideoutils.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoFragment extends BaseFragment {
	public static final String ARG_URL = "url";
	public static final String ARG_PATH = "path";
	ImageView ivPhoto;
	PhotoViewAttacher mAttacher;

	@Override
	protected int getViewResId() {
		return R.layout.fragment_photo;
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		ivPhoto = (ImageView) mRootView.findViewById(R.id.ivPhoto);
		String path = getArguments().getString(ARG_PATH);
		if (null!=path) {
			RequestOptions options=new RequestOptions()
					.centerCrop()
					.placeholder(R.drawable.ic_pic_loading)
					.error(R.drawable.ic_pic_loading_fail);
			Glide.with(this.getActivity())
					.load(path)
					.apply(options)
					.listener(new RequestListener<Drawable>() {
						@Override
						public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
							Toast.makeText(mContext, "加载图片失败", Toast.LENGTH_SHORT).show();
							return false;
						}

						@Override
						public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean
								isFirstResource) {
							mAttacher = new PhotoViewAttacher(ivPhoto);
							return false;
						}
					})
					.into(ivPhoto);
		}
	}
}