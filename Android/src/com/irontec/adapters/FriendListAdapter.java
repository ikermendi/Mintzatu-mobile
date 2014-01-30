package com.irontec.adapters;

import java.util.ArrayList;

import com.irontec.helpers.ImageHelper;
import com.irontec.mintzatu.R;
import com.irontec.models.Friend;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter {

	private ArrayList<Friend> mLagunak = new ArrayList<Friend>();
	private LayoutInflater mInflater;
	private Context mContext;
	private ImageHelper mImageHelper = new ImageHelper();

	public FriendListAdapter(Context context, ArrayList<Friend> friends) {
		this.mContext = context;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mLagunak = friends; 
	}

	public void addItem(final Friend laguna) {
		mLagunak.add(laguna);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mLagunak.size();
	}

	@Override
	public Friend getItem(int position) {
		return mLagunak.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mLagunak.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.row_friend_list, null);
			holder.izena = (TextView)convertView.findViewById(R.id.izena);
			//holder.deskribapena = (TextView)convertView.findViewById(R.id.deskribapena);
			holder.argazkia = (ImageView)convertView.findViewById(R.id.friendAvatar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.izena.setText(mLagunak.get(position).username);
		//holder.deskribapena.setText(mLagunak.get(position).desc);
		if(mLagunak.get(position).userImage != null && mLagunak.get(position).userImage != "") {
			Picasso.with(mContext)
			  .load(mLagunak.get(position).userImage)
			  .placeholder(R.drawable.placeholder)
			  .error(R.drawable.placeholder)
			  .into(holder.argazkia);
			holder.argazkia.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView izena;
		TextView deskribapena;
		ImageView argazkia;
	}
}
