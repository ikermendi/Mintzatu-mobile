package com.irontec.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.irontec.helpers.SquaredImageView;
import com.irontec.mintzatu.R;
import com.irontec.models.Badge;
import com.squareup.picasso.Picasso;

public class BadgeAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Badge> mBadges;
	private final int size;

	public BadgeAdapter(Context context, ArrayList<Badge> badges, int size) {
		this.mContext = context;
		this.size = size;
		this.mBadges = badges;
	}

	public int getCount() {
		return mBadges.size();
	}

	public Badge getItem(int position) {
		return mBadges.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		SquaredImageView view = (SquaredImageView) convertView;
		if (view == null) {
			view = new SquaredImageView(mContext);
		}

		Badge badge = getItem(position);

		Picasso.with(mContext)
		.load(badge.img)
		.placeholder(R.drawable.placeholder)
		.error(R.drawable.placeholder)
		.resize(size, size)
		.centerCrop()
		.into(view);

		return view;
	}

}