package com.irontec.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.adapters.GalleryAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.mintzatu.GalleryDetailActivity;
import com.irontec.mintzatu.R;
import com.irontec.models.Picture;
import com.irontec.models.Place;
import com.irontec.models.PlaceHistory;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class PhotoTypeFragment extends SherlockFragment implements OnScrollListener, OnItemClickListener {

	private Integer PAGE = 1;
	private Integer ITEMS = 6;
	private Context mContext;
	private ViewSwitcher mViewSwitcher;
	private Place mPlace;
	private PlaceHistory mPlaceHistory;
	private GridView mGridView;
	private GalleryAdapter mAdapter;
	
	public PhotoTypeFragment() {}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		EasyTracker.getInstance(getActivity()).activityStart(getActivity());
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
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
		
		View rootView = inflater.inflate(R.layout.fragment_sub_photos, container, false);
		mGridView = (GridView)rootView.findViewById(R.id.argazkiGaleria);
		
        loadPhotos();
        
        return rootView;
    }
	
	@Override
	public void onResume() {
		super.onResume();
		PAGE = 1;
		loadPhotos();
	}
	
	private void loadPhotos() {
		RequestParams photosParams = new RequestParams();
		photosParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		photosParams.put("token", MintzatuAPI.getToken(mContext).toString());
		photosParams.put("idPlace", mPlace.id_lekua.toString());
		photosParams.put("page", PAGE.toString());
		photosParams.put("items", ITEMS.toString());
		
		MintzatuAPI.post(MintzatuAPI.PHOTOS, photosParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					if (error == 0 && !response.isNull("pictures")) {
						ArrayList<Picture> pictures = new ArrayList<Picture>();
						JSONArray picturesJson = response.getJSONArray("pictures");
						for (int i = 0; i < picturesJson.length(); i++) {
							Picture picture = new Picture(picturesJson.getJSONObject(i));
							pictures.add(picture);
						}
						if (PAGE > 1) {
							if (mAdapter == null) {
								mAdapter = (GalleryAdapter) mGridView.getAdapter();
							}
							for(Picture picture : pictures) {
								mAdapter.addItem(picture);
							}
							mAdapter.notifyDataSetChanged();
						} else {
							Integer size = getDisplaySize();
							mAdapter = new GalleryAdapter(mContext, pictures, size);
							mGridView.setAdapter(mAdapter);
							mGridView.setOnScrollListener(PhotoTypeFragment.this);
							mGridView.setOnItemClickListener(PhotoTypeFragment.this);
						}
					} else if (error == 0 && response.isNull("pictures")) {
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
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (mGridView.getLastVisiblePosition() >= mGridView.getCount() - 4) {
				PAGE++;
				loadPhotos();
			}
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.lagunak, menu);
	}

	private Integer getDisplaySize() {
		if (getActivity() != null && !getActivity().isFinishing()) {
			WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			size.x = display.getWidth();
			size.y = display.getHeight();
			return size.x / getResources().getInteger(R.integer.column_count);
		} else {
			return null;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Picture picture = (Picture) mGridView.getItemAtPosition(position);
		if (picture != null && picture.url != "") {
			Intent intent = new Intent(mContext, GalleryDetailActivity.class);
			intent.putExtra("picture", picture);
			startActivity(intent);
		}
	}
	
}
