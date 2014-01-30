package com.irontec.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.irontec.helpers.ImageHelper;
import com.irontec.mintzatu.R;
import com.irontec.models.Comment;

public class SimpleCommentListAdapter extends BaseAdapter {

	private ArrayList<Comment> mData = new ArrayList<Comment>();
	private LayoutInflater mInflater;
	private Context mContext;
	private ImageHelper mImageHelper = new ImageHelper();

	public SimpleCommentListAdapter(Context context, ArrayList<Comment> list) {
		this.mContext = context;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mData = list; 
	}

	public void addItem(final Comment item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Comment getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mData.get(position).idLeku;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.row_map_list, null);
			holder.izena = (TextView)convertView.findViewById(R.id.izena);
			holder.iruzkina = (TextView)convertView.findViewById(R.id.helbidea);
			holder.argazkia = (ImageView)convertView.findViewById(R.id.argazkia);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.izena.setText(mData.get(position).izena);
		holder.iruzkina.setText(mData.get(position).iruzkina);
		if (mData.get(position).userImg != null && mData.get(position).userImg != "") {
			mImageHelper.download(mData.get(position).userImg, holder.argazkia);
			holder.argazkia.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}
	static class ViewHolder {
		TextView izena;
		TextView iruzkina;
		ImageView argazkia;
	}

}
