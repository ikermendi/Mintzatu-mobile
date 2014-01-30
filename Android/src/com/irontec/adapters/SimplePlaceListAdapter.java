package com.irontec.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.irontec.mintzatu.R;
import com.irontec.models.Place;
import com.squareup.picasso.Picasso;

public class SimplePlaceListAdapter extends BaseAdapter {

	private ArrayList<Place> mData = new ArrayList<Place>();
	private LayoutInflater mInflater;
	private Context mContext;

	public SimplePlaceListAdapter(Context context, ArrayList<Place> list) {
		this.mContext = context;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mData = list; 
	}

	public void addItem(final Place item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Place getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mData.get(position).id_lekua;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.row_map_list, null);
			holder.izena = (TextView)convertView.findViewById(R.id.izena);
			holder.helbidea = (TextView)convertView.findViewById(R.id.helbidea);
			holder.argazkia = (ImageView)convertView.findViewById(R.id.argazkia);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.izena.setText(mData.get(position).izena);
		holder.helbidea.setText(mData.get(position).helbidea);
		if (mData.get(position).katImgUrl != null && mData.get(position).katImgUrl != "") {
			Picasso.with(mContext)
			  .load(mData.get(position).katImgUrl)
			  .into(holder.argazkia);
			holder.argazkia.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}
	static class ViewHolder {
		TextView izena;
		TextView helbidea;
		ImageView argazkia;
	}

}
