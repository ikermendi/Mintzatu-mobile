package com.irontec.adapters;

import java.util.ArrayList;

import org.json.JSONObject;

import com.irontec.api.MintzatuAPI;
import com.irontec.mintzatu.R;
import com.irontec.models.FriendRequest;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendRequestAdapter extends ArrayAdapter<FriendRequest> {
	private Context mContext;
	private ArrayList<FriendRequest> mRequets;
	private LayoutInflater mInflater;

	public FriendRequestAdapter(Context context, ArrayList<FriendRequest> requests) {
		super(context, R.layout.row_user_friend_request, requests);
		this.mContext = context;
		this.mRequets = requests;
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.row_user_friend_request, null);
			holder.izena = (TextView)convertView.findViewById(R.id.izena);
			holder.argazkia = (ImageView)convertView.findViewById(R.id.argazkia);
			holder.onartu = (Button) convertView.findViewById(R.id.onartu);
			holder.ukatu = (Button) convertView.findViewById(R.id.ukatu);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.izena.setText(mRequets.get(position).who);
		if(mRequets.get(position).userImg != null && mRequets.get(position).userImg != "") {
			Picasso.with(mContext)
			  .load(mRequets.get(position).userImg)
			  .into(holder.argazkia);
		}
		holder.onartu.setTag(mRequets.get(position).idRel);
		holder.onartu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				RequestParams onartuParams = new RequestParams();
				onartuParams.put("id", MintzatuAPI.getUserid(mContext).toString());
				onartuParams.put("token", MintzatuAPI.getToken(mContext));
				onartuParams.put("idRel", v.getTag().toString());
				onartuParams.put("answer", "1");
				
				MintzatuAPI.post(MintzatuAPI.ANSWER_REQUEST, onartuParams, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode, JSONObject response) {
						super.onSuccess(statusCode, response);
						v.setBackgroundColor(mContext.getResources().getColor(R.color.request_acepted));
					}
					@Override
					public void onFailure(Throwable e, JSONObject errorResponse) {
						super.onFailure(e, errorResponse);
					}
				});
			}
		});
		holder.ukatu.setTag(mRequets.get(position).idRel);
		holder.ukatu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				RequestParams onartuParams = new RequestParams();
				onartuParams.put("id", MintzatuAPI.getUserid(mContext).toString());
				onartuParams.put("token", MintzatuAPI.getToken(mContext));
				onartuParams.put("idRel", v.getTag().toString());
				onartuParams.put("answer", "0");
				
				MintzatuAPI.post(MintzatuAPI.ANSWER_REQUEST, onartuParams, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode, JSONObject response) {
						super.onSuccess(statusCode, response);
						v.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_orange_dark));
					}
					@Override
					public void onFailure(Throwable e, JSONObject errorResponse) {
						super.onFailure(e, errorResponse);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
				});
			}
		});
		return convertView;
	}
	
	static class ViewHolder {
		TextView izena;
		ImageView argazkia;
		Button onartu;
		Button ukatu;
	}

	public void addItem(final FriendRequest item) {
		mRequets.add(item);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mRequets.size();
	}

	@Override
	public FriendRequest getItem(int position) {
		return mRequets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mRequets.get(position).userId;
	}

}

