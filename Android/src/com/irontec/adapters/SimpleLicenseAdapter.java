package com.irontec.adapters;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import com.irontec.mintzatu.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SimpleLicenseAdapter extends BaseAdapter {

	private ArrayList<BasicNameValuePair> mValues = new ArrayList<BasicNameValuePair>();
	private LayoutInflater mInflater;
	private Context mContext;

	public SimpleLicenseAdapter(Context context, ArrayList<BasicNameValuePair> values) {
		this.mContext = context;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mValues = values;
	}

	public void addItem(BasicNameValuePair value) {
		mValues.add(value);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mValues.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.row_license, null);
			holder.izena = (TextView)convertView.findViewById(R.id.izenburua);
			holder.url = (TextView)convertView.findViewById(R.id.url);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BasicNameValuePair data = mValues.get(position);
		holder.izena.setText(data.getName());
		holder.url.setText(data.getValue());
		
		return convertView;
	}
	static class ViewHolder {
		TextView izena;
		TextView url;
	}
	
	@Override
	public BasicNameValuePair getItem(int position) {
		return mValues.get(position);
	}

}
