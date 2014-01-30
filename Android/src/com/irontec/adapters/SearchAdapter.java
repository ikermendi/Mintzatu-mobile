package com.irontec.adapters;

import java.util.ArrayList;

import com.irontec.helpers.ImageHelper;
import com.irontec.mintzatu.R;
import com.irontec.models.KategoriaSinplea;
import com.irontec.models.People;
import com.irontec.models.Place;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {

	private ArrayList<Place> mDataPlaces = new ArrayList<Place>();
	private ArrayList<People> mDataPeople = new ArrayList<People>();
	private ArrayList<KategoriaSinplea> mDataCategories = new ArrayList<KategoriaSinplea>();
	private LayoutInflater mInflater;
	private Context mContext;
	private ImageHelper mImageHelper = new ImageHelper();

	public SearchAdapter(Context context) {
		this.mContext = context;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setAdapterTypePeople(ArrayList<People> list) {
		this.mDataPeople = list; 
	}

	public void setAdapterTypePlaces(ArrayList<Place> list) {
		this.mDataPlaces = list; 
	}
	
	public void setAdaterTypeCategories(ArrayList<KategoriaSinplea> list) {
		this.mDataCategories = list;
	}
	
	static class ViewHolder {
		TextView izena;
		TextView helbidea;
		ImageView argazkia;
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
		if (mDataPeople != null && !mDataPeople.isEmpty()) {
			holder.izena.setText(mDataPeople.get(position).username);
			holder.helbidea.setText("");
			if (holder.argazkia != null && mDataPeople.get(position).userImg != null &&  mDataPeople.get(position).userImg != "") {
				Picasso.with(mContext)
				  .load(mDataPeople.get(position).userImg)
				  .into(holder.argazkia);
				holder.argazkia.setVisibility(View.VISIBLE);
			}
		} else if (mDataPlaces != null && !mDataPlaces.isEmpty()) {
			holder.izena.setText(mDataPlaces.get(position).izena);
			holder.helbidea.setText(mDataPlaces.get(position).helbidea);
			if (holder.argazkia != null && mDataPlaces.get(position).irudia != null && mDataPlaces.get(position).irudia != "") {
				Picasso.with(mContext)
				  .load(mDataPlaces.get(position).katImgUrl)
				  .into(holder.argazkia);
				holder.argazkia.setVisibility(View.VISIBLE);
			}
		} else {
			holder.izena.setText(mDataCategories.get(position).name);
			if (holder.argazkia != null && mDataCategories.get(position).imgCat != null &&  mDataCategories.get(position).imgCat != "") {
				Picasso.with(mContext)
				  .load(mDataCategories.get(position).imgCat)
				  .into(holder.argazkia);
				holder.argazkia.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}

	public void addPlace(final Place item) {
		mDataPlaces.add(item);
		notifyDataSetChanged();
	}
	
	public void addPlaceList(final ArrayList<Place> items) {
		mDataPlaces.addAll(items);
		notifyDataSetChanged();
	}
	
	public void addCategoryList(final ArrayList<KategoriaSinplea> items) {
		mDataCategories.addAll(items);
		notifyDataSetChanged();
	}
	
	public void clearAdapter(){
		if (mDataPeople != null && !mDataPeople.isEmpty()) {
			mDataPeople.clear();
		}
		if (mDataPlaces != null && !mDataPlaces.isEmpty()) {
			mDataPlaces.clear();
		}
		if (mDataCategories != null && !mDataCategories.isEmpty()) {
			mDataCategories.clear();
		}
		notifyDataSetChanged();
	}

	public void addPeople(final People item) {
		mDataPeople.add(item);
		notifyDataSetChanged();
	}
	
	public void addPeopleList(final ArrayList<People> items) {
		mDataPeople.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mDataPlaces != null && !mDataPlaces.isEmpty()) {
			return mDataPlaces.size();
		} else if (mDataPeople != null && !mDataPeople.isEmpty()) {
			return mDataPeople.size();
		} else {
			return mDataCategories.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (mDataPlaces != null && !mDataPlaces.isEmpty()) {
			return mDataPlaces.get(position);
		} else if (mDataPeople != null && !mDataPeople.isEmpty()) {
			return mDataPeople.get(position);
		} else {
			return mDataCategories.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		if (mDataPlaces != null && !mDataPlaces.isEmpty()) {
			return mDataPlaces.get(position).id_lekua;
		} else if (mDataPeople != null && !mDataPeople.isEmpty()) {
			return 0;
		} else {
			return 0;
		}

	}
	
}
