package com.irontec.adapters;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;
import com.irontec.mintzatu.R;
import com.irontec.models.Picture;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.irontec.helpers.SquaredImageView;

public class GalleryAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Picture> mPictures;
	private Integer size;

	public GalleryAdapter(Context context, ArrayList<Picture> pictures, Integer size) {
		this.mContext = context;
		this.size = size;
		this.mPictures = pictures;
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		SquaredImageView view = (SquaredImageView) convertView;
		if (view == null) {
			view = new SquaredImageView(mContext);
		}

		String url = mPictures.get(position).normalImg;

		if (size != null) {
			Picasso.with(mContext)
			.load(url)
			.placeholder(R.drawable.placeholder)
			.error(R.drawable.placeholder)
			.resize(size, size)
			.centerCrop()
			.into(view);
		} else {
			Picasso.with(mContext)
			.load(url)
			.placeholder(R.drawable.placeholder)
			.error(R.drawable.placeholder)
			.into(view);
		}

		return view;
	}

	@Override public int getCount() {
		return mPictures.size();
	}

	@Override public Picture getItem(int position) {
		return mPictures.get(position);
	}

	@Override public long getItemId(int position) {
		return position;
	}
	public void addItem(Picture picture) {
		mPictures.add(picture);
	}

}