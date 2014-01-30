package com.irontec.mintzatu;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.adapters.SimplePlaceListAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.models.Place;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MyCheckinActivity extends SherlockActivity implements OnScrollListener {

	private Context mContext;
	private Integer PAGE = 1;
	private Integer ITEMS = 20;
	private ListView mCheckinZerrenda;
	private ViewSwitcher mViewSwitcher;
	private SimplePlaceListAdapter mAdapter;
	private Boolean mIsFirstLoad = true;

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_my_checkin);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mContext = getBaseContext();

		mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
		mCheckinZerrenda = (ListView)findViewById(R.id.checkin_list);

		loadCheckins();

	}

	public void loadCheckins() {
		RequestParams checkinsParams = new RequestParams();
		checkinsParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		checkinsParams.put("idProfile", MintzatuAPI.getUserid(mContext).toString());
		checkinsParams.put("token", MintzatuAPI.getToken(mContext));
		checkinsParams.put("page", PAGE.toString());
		checkinsParams.put("items", ITEMS.toString());

		MintzatuAPI.post(MintzatuAPI.MY_CHECK_INS, checkinsParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					ArrayList<Place> checkins = new ArrayList<Place>();
					if (error == 0 && !response.isNull("checkins")) {
						JSONArray jsonCheckins = response.getJSONArray("checkins");
						for (int i = 0; i < jsonCheckins.length(); i++) {
							JSONObject jsonCheckin = jsonCheckins.getJSONObject(i);
							Place place = new Place();
							place.id_lekua = jsonCheckin.getLong("id_lekua");
							place.izena = jsonCheckin.getString("izena");
							place.helbidea = jsonCheckin.getString("dir");
							place.katImgUrl = jsonCheckin.getString("imgCat");
							place.irudia = jsonCheckin.getString("irudia");
							place.deskribapena = jsonCheckin.getString("deskribapena");
							checkins.add(place);
						}
						if (PAGE > 1) {
							if (mAdapter == null) {
								mAdapter = (SimplePlaceListAdapter) mCheckinZerrenda.getAdapter();
							}
							for(Place place : checkins) {
								mAdapter.addItem(place);
							}
							mAdapter.notifyDataSetChanged();
						} else {
							mAdapter = new SimplePlaceListAdapter(mContext, checkins);
							mCheckinZerrenda.setAdapter(mAdapter);
							mCheckinZerrenda.setOnScrollListener(MyCheckinActivity.this);
							mCheckinZerrenda.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
									Place place = (Place) parent.getItemAtPosition(position);
									Intent intent = new Intent(mContext, PlaceActivity.class);
									intent.putExtra("place", place);
									startActivity(intent);
								}
							});
						}
					} else if (error == 0 && response.isNull("checkins")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_checkins_not_found), Toast.LENGTH_LONG).show();
					} else if (error == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					setProgressBarIndeterminateVisibility(Boolean.FALSE);
					if (mIsFirstLoad) {
						mViewSwitcher.showNext();
						mIsFirstLoad = false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					mViewSwitcher.showNext();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mViewSwitcher.showNext();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.mayorship, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			overridePendingTransition (R.anim.open_main, R.anim.close_next);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (mCheckinZerrenda.getLastVisiblePosition() >= mCheckinZerrenda.getCount() - 4) {
				PAGE++;
				setProgressBarIndeterminateVisibility(Boolean.TRUE);
				loadCheckins();
			}
		}
	}

}
