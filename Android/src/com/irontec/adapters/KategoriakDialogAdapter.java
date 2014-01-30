package com.irontec.adapters;

import java.util.ArrayList;

import com.irontec.helpers.ImageHelper;
import com.irontec.mintzatu.R;
import com.irontec.models.KategoriaSinplea;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class KategoriakDialogAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<KategoriaSinplea> kategoriak;
	private ImageHelper mImageHelper = new ImageHelper();

	public KategoriakDialogAdapter(Context context, ArrayList<KategoriaSinplea> lista) {
		this.mContext = context;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.kategoriak = lista; 
	}

	@Override
	public int getCount() {
		return kategoriak.size();
	}

	@Override
	public Object getItem(int position) {
		return kategoriak.get(position);
	}

	@Override
	public long getItemId(int position) {
		return kategoriak.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.row_map_list, null);
			holder.izena = (TextView)convertView.findViewById(R.id.izena);
			holder.argazkia = (ImageView)convertView.findViewById(R.id.argazkia);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
			holder.izena.setText(kategoriak.get(position).name);
			if(kategoriak.get(position).imgCat != null) {
				Picasso.with(mContext)
				  .load(kategoriak.get(position).imgCat)
				  .into(holder.argazkia);
				holder.argazkia.setVisibility(View.VISIBLE);
			}
		return convertView;
	}

	static class ViewHolder {
		TextView izena;
		ImageView argazkia;
	}

}
