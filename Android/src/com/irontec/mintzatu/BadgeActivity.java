package com.irontec.mintzatu;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.adapters.BadgeAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.models.Badge;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class BadgeActivity extends SherlockActivity implements OnItemClickListener{

	private Context mContext;
	private Long mFriendId;
	private ArrayList<Badge> mBadges;
	private GridView mGridView;
	private int mSize;
	
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
		setContentView(R.layout.activity_badge);

		mContext = getBaseContext();
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Point displaySize = getDisplaySize();
		mSize = displaySize.x / getResources().getInteger(R.integer.column_count);

		mGridView = (GridView) findViewById(R.id.grid_view);
		
		Intent intent = getIntent();
		if (intent != null) {
			mFriendId = intent.getLongExtra("friendId", -1);
		}
		
		loadBadges();
		
	}
	
	public void loadBadges() {
		RequestParams params = new RequestParams();
		params.put("id", MintzatuAPI.getUserid(mContext).toString());
		if (mFriendId != null && mFriendId > 0) {
			params.put("idProfile", mFriendId.toString());
		} else {
			params.put("idProfile", MintzatuAPI.getUserid(mContext).toString());
		}
		params.put("token", MintzatuAPI.getToken(mContext));
		MintzatuAPI.post(MintzatuAPI.GET_BADGES, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0) {
						if(response.isNull("badges")) {
							Toast.makeText(mContext, getString(R.string.no_badges), Toast.LENGTH_LONG).show();
						} else {
							JSONArray badgeList = response.getJSONArray("badges");
							mBadges = new ArrayList<Badge>();
							for (int i = 0; i < badgeList.length(); i++) {
								Badge badge = new Badge(badgeList.getJSONObject(i));
								mBadges.add(badge);
							}
							mGridView.setAdapter(new BadgeAdapter(mContext, mBadges, mSize));
							mGridView.setOnItemClickListener(BadgeActivity.this);
						}
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
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
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Badge badge = (Badge) mGridView.getItemAtPosition(position);
		if (badge != null && badge.img != "") {
			Intent intent = new Intent(mContext, BadgeDetailActivity.class);
			intent.putExtra("badge", badge);
			startActivity(intent);
		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.badge, menu);
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
	
	@SuppressWarnings("deprecation")
	private Point getDisplaySize() {
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		size.x = display.getWidth();
		size.y = display.getHeight();
		return size;
	}

}
