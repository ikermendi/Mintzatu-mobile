package com.irontec.adapters;

import java.util.ArrayList;

import com.irontec.helpers.ImageHelper;
import com.irontec.mintzatu.R;
import com.irontec.models.Place;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MapListAdapter extends BaseAdapter {

	private ArrayList<Place> mData = new ArrayList<Place>();
	private LayoutInflater mInflater;
	private Context mContext;
	private ImageHelper mImageHelper = new ImageHelper();

	public MapListAdapter(Context context, ArrayList<Place> list) {
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
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return (position == 0) ? 0 : 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int theType = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			if (theType == 0) {
				convertView = mInflater.inflate(R.layout.row_map_list_transparent, null);
			} else if (theType == 1) {
				convertView = mInflater.inflate(R.layout.row_map_list, null);
				holder.izena = (TextView)convertView.findViewById(R.id.izena);
				holder.helbidea = (TextView)convertView.findViewById(R.id.helbidea);
				holder.argazkia = (ImageView)convertView.findViewById(R.id.argazkia);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (theType == 1) {
			holder.izena.setText(mData.get(position).izena);
			holder.helbidea.setText(mData.get(position).helbidea);
			if(mData.get(position).kategoriak.get(0).imgUrl != null) {
				Picasso.with(mContext)
				  .load(mData.get(position).kategoriak.get(0).imgUrl)
				  .placeholder(R.drawable.placeholder)
				  .into(holder.argazkia);
				holder.argazkia.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}
	static class ViewHolder {
		TextView izena;
		TextView helbidea;
		ImageView argazkia;
	}

}
