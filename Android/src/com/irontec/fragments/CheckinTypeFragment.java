package com.irontec.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.api.MintzatuAPI;
import com.irontec.helpers.ImageHelper;
import com.irontec.mintzatu.R;
import com.irontec.models.FriendProfile;
import com.irontec.models.Place;
import com.irontec.models.PlaceHistory;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;


public class CheckinTypeFragment extends SherlockFragment {

	private Context mContext;
	private ViewSwitcher mViewSwitcher;
	private Place mPlace;
	private PlaceHistory mPlaceHistory;
	private TextView mIzena;
	private TextView mNon;
	private TextView mNoiz;
	private ImageView mArgazkia;
	private ImageView mArgazkiaLekua;
	private LinearLayout mArgazkiaLayout;
	private ImageHelper mImageHelper = new ImageHelper();
	
	public CheckinTypeFragment() {}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(getActivity()).activityStart(getActivity());
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(getActivity()).activityStop(getActivity());
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		mContext = getActivity().getBaseContext();
		
		Bundle data = getArguments();
		if (data != null) {
			mPlace = (Place) data.get("place");
			mPlaceHistory = (PlaceHistory) data.get("placeHistory");
		}
		
		View rootView = inflater.inflate(R.layout.fragment_sub_checkin, container, false);
        mIzena = (TextView)rootView.findViewById(R.id.izena);
        mNon = (TextView)rootView.findViewById(R.id.non);
        //mNoiz = (TextView)rootView.findViewById(R.id.noiz);
        mArgazkia = (ImageView)rootView.findViewById(R.id.argazkia);
        mArgazkiaLekua = (ImageView)rootView.findViewById(R.id.argazkiaLekua);
        mArgazkiaLayout = (LinearLayout)rootView.findViewById(R.id.argazkiaLayout);
		
        loadCheckin();
        
        return rootView;
    }
	
	private void loadCheckin() {
		getProfileImage();
		mIzena.setText(mPlaceHistory.who);
		mNon.setText(mContext.getResources().getString(R.string.sub_non_checkin_param, mPlace.izena));
		if (mPlaceHistory.normalImg != null && mPlaceHistory.normalImg != "") {
			Picasso.with(mContext)
			.load(mPlaceHistory.normalImg)
			.into(mArgazkiaLekua);
		} else {
			mArgazkiaLayout.setVisibility(View.GONE);
		}
	}

	public void getProfileImage() {
		RequestParams profileParams = new RequestParams();
		profileParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		profileParams.put("token", MintzatuAPI.getToken(mContext).toString());
		profileParams.put("idProfile", mPlaceHistory.idWho.toString());
		
		MintzatuAPI.post(MintzatuAPI.PROFILE, profileParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject response) {
				super.onSuccess(response);
				try {
					int error = response.getInt("error");
					if (error == 0) {
						FriendProfile people = new FriendProfile(response);
						if (people != null && people.img != "") {
							Picasso.with(mContext)
							.load(people.img)
							.resize(70, 70)
							.centerInside()
							.into(mArgazkia);
						}
					} else if (error == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginCircles.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
			}
		});
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.lagunak, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
