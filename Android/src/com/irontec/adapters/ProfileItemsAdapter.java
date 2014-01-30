package com.irontec.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.irontec.mintzatu.R;

public class ProfileItemsAdapter extends BaseAdapter {
	private final Context context;
	private final ArrayList<String> mIzenak;
	private final Integer mZenbatMezu;
 
	public ProfileItemsAdapter(Context context, ArrayList<String> izenak, Integer zenbatMezu) {
		super();
		this.context = context;
		this.mIzenak = izenak;
		this.mZenbatMezu = zenbatMezu;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.row_profile_item, parent, false);
		TextView izenburua = (TextView) rowView.findViewById(R.id.izenburua);
		TextView zenbat = (TextView) rowView.findViewById(R.id.zenbat);
		izenburua.setText(mIzenak.get(position));
		if(mZenbatMezu != null && mZenbatMezu > 0 && position == 0) {
			zenbat.setText(mZenbatMezu.toString());
			zenbat.setVisibility(TextView.VISIBLE);
		}
 
		return rowView;
	}

	@Override
	public int getCount() {
		return mIzenak.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mIzenak.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}