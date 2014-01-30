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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.adapters.GalleryAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.models.Picture;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GalleryActivity extends SherlockActivity implements OnScrollListener, OnItemClickListener {

	private Integer PAGE = 1;
	private Integer ITEMS = 6;
	private Context mContext;
	private GridView mGridView;
	private GalleryAdapter mAdapter;
	private Boolean mIsFirstLoad = true;
	private ViewSwitcher mViewSwitcher;
	private ArrayList<Picture> mPictures;

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
		setContentView(R.layout.activity_gallery);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mContext = getBaseContext();

		mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
		mGridView = (GridView) findViewById(R.id.argazkiGaleria);

		loadPhotos();

	}

	private void loadPhotos() {
		RequestParams photosParams = new RequestParams();
		photosParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		photosParams.put("token", MintzatuAPI.getToken(mContext).toString());
		photosParams.put("page", PAGE.toString());
		photosParams.put("items", ITEMS.toString());

		MintzatuAPI.post(MintzatuAPI.MY_PHOTOS, photosParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					if (error == 0 && !response.isNull("pictures")) {
						mPictures = new ArrayList<Picture>();
						JSONArray picturesJson = response.getJSONArray("pictures");
						for (int i = 0; i < picturesJson.length(); i++) {
							Picture picture = new Picture(picturesJson.getJSONObject(i));
							mPictures.add(picture);
						}
						if (PAGE > 1) {
							if (mAdapter == null) {
								mAdapter = (GalleryAdapter) mGridView.getAdapter();
							}
							for(Picture picture : mPictures) {
								mAdapter.addItem(picture);
							}
							mAdapter.notifyDataSetChanged();
						} else {
							Integer displaySize = getDisplaySize();
//							int size = displaySize.x / getResources().getInteger(R.integer.column_count);
							Integer size = getDisplaySize();
							mAdapter = new GalleryAdapter(mContext, mPictures, size);
							mGridView.setAdapter(mAdapter);
							mGridView.setOnScrollListener(GalleryActivity.this);
							mGridView.setOnItemClickListener(GalleryActivity.this);
						}
					} else if (error == 0 && response.isNull("pictures")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_pictures_not_found), Toast.LENGTH_LONG).show();
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
		getSupportMenuInflater().inflate(R.menu.gallery, menu);
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
			if (mGridView.getLastVisiblePosition() >= mGridView.getCount() - 4) {
				PAGE++;
				loadPhotos();
			}
		}
	}

	private Integer getDisplaySize() {
		if (this != null && !this.isFinishing()) {
			WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
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
			overridePendingTransition (R.anim.open_next, R.anim.close_main);
		}
	}

}
