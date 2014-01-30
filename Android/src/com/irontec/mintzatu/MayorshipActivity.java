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
import com.irontec.models.Category;
import com.irontec.models.Place;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class MayorshipActivity extends SherlockActivity implements OnScrollListener{

	private Context mContext;
	private Integer PAGE = 1;
	private Integer ITEMS = 20;
	private ListView mAlkatetzaZerrenda;
	private ViewSwitcher mViewSwitcher;
	private SimplePlaceListAdapter mAdapter;
	private Long mFriendId;
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
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mayorship);
		
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
	    mContext = getBaseContext();
	    
	    mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
		mAlkatetzaZerrenda = (ListView)findViewById(R.id.mayor_list);
		
		Intent intent = getIntent();
		if (intent != null) {
			mFriendId = intent.getLongExtra("friendId", -1);
		}
		
		loadMayorships();
		
	}
	
	public void loadMayorships() {
		RequestParams mayorshipParams = new RequestParams();
		mayorshipParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		if (mFriendId != null && mFriendId > 0) {
			mayorshipParams.put("idProfile", mFriendId.toString());
		} else {
			mayorshipParams.put("idProfile", MintzatuAPI.getUserid(mContext).toString());
		}
		mayorshipParams.put("token", MintzatuAPI.getToken(mContext));
		mayorshipParams.put("page", PAGE.toString());
		mayorshipParams.put("items", ITEMS.toString());
		
		MintzatuAPI.post(MintzatuAPI.MAYORSHIPS, mayorshipParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					ArrayList<Place> alkatetzak = new ArrayList<Place>();
					if (error == 0 && !response.isNull("places")) {
						JSONArray jsonAlkatetzak = response.getJSONArray("places");
						for (int i = 0; i < jsonAlkatetzak.length(); i++) {
							JSONObject jsonPlace = jsonAlkatetzak.getJSONObject(i);
							Place place = new Place();
							place.id_lekua = jsonPlace.getLong("id_lekua");
							place.izena = jsonPlace.getString("name");
							place.helbidea = jsonPlace.getString("address");
							place.katImgUrl = jsonPlace.getString("imgCat");
							place.irudia = jsonPlace.getString("irudia");
							place.deskribapena = jsonPlace.getString("deskribapena");
							alkatetzak.add(place);
						}
						if (PAGE > 1) {
							if (mAdapter == null) {
								mAdapter = (SimplePlaceListAdapter) mAlkatetzaZerrenda.getAdapter();
							}
							for(Place place : alkatetzak) {
								mAdapter.addItem(place);
							}
							mAdapter.notifyDataSetChanged();
						} else {
							mAdapter = new SimplePlaceListAdapter(mContext, alkatetzak);
							mAlkatetzaZerrenda.setAdapter(mAdapter);
							mAlkatetzaZerrenda.setOnScrollListener(MayorshipActivity.this);
							mAlkatetzaZerrenda.setOnItemClickListener(new OnItemClickListener() {
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
					} else if (error == 0 && response.isNull("places")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_places_not_found), Toast.LENGTH_LONG).show();
					} else if (error == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					if (mIsFirstLoad) {
						mViewSwitcher.showNext();
						mIsFirstLoad = false;
					}
					setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
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
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (mAlkatetzaZerrenda.getLastVisiblePosition() >= mAlkatetzaZerrenda.getCount() - 4) {
				PAGE++;
				setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
				loadMayorships();
			}
		}
	}

}
