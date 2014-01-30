package com.irontec.mintzatu;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchDoubleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.OnDrawableChangeListener;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.adapters.PlaceItemsAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.helpers.ImageHelper;
import com.irontec.models.Badge;
import com.irontec.models.Place;
import com.irontec.models.PlaceHistory;
import com.irontec.models.SubDetailTypes;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PlaceActivity extends SherlockActivity implements OnItemClickListener, OnScrollListener {

	private Integer PAGE = 1;
	private static Integer CHECKIN_ACTION = 103;
	private static Integer COMMENT_ACTION = 108;
	private Context mContext;
	private ArrayList<Bitmap> mArgazkiak = new ArrayList<Bitmap>();
	private ArrayList<PlaceHistory> mHistories;
	private ListView mItemZerrenda;
	private Place mPlace;
	private Long mPlaceId;
	private ActionBar mActionBar;
	private PlaceItemsAdapter mPlaceItemsAdapter;
	private ViewSwitcher mViewSwitcher;
	private Boolean mIsFirstLoad = true;
	private RelativeLayout mBadgeLayout;
	private ImageView mBadgeImage;
	private TextView mBadgeText;
	private TextView mBadgeDescText;
	private ImageView mClose;
	private ImageViewTouch mImage;
	private LinearLayout mImgPreview;
	private Matrix mImageMatrix = new Matrix();
	private Target target = new Target() {
		@Override
		public void onError() {}
		@Override
		public void onSuccess(Bitmap bm) {
			mImage.setImageBitmap( bm, mImageMatrix.isIdentity() ? null : mImageMatrix, ImageViewTouchBase.ZOOM_INVALID, ImageViewTouchBase.ZOOM_INVALID );
			mBadgeLayout.setVisibility(View.VISIBLE);
			mImgPreview.setVisibility(View.VISIBLE);
		}
	};
	
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
		setContentView(R.layout.activity_place);

		this.mContext = getBaseContext();

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		if (intent != null) {
//			mPlaceId = intent.getLongExtra("placeId", 0);
			mPlace = intent.getParcelableExtra("place");
		}

		mActionBar.setTitle(mPlace.izena);
		mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);

		mItemZerrenda = (ListView)findViewById(R.id.zerrenda);
		mItemZerrenda.setOnItemClickListener(PlaceActivity.this);
		mBadgeLayout = (RelativeLayout)findViewById(R.id.badgeLayout);
		mBadgeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				return;
			}
		});
		mBadgeImage = (ImageView)findViewById(R.id.badgeImage);
		mBadgeText = (TextView)findViewById(R.id.badgeText);
		mBadgeDescText = (TextView)findViewById(R.id.badgeDescText);
		mClose = (ImageView)findViewById(R.id.close);
		mClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBadgeLayout.setVisibility(View.GONE);
				clean(mImage);
			}
		});
		mImage = (ImageViewTouch) findViewById( R.id.image );
		mImage.setDisplayType(DisplayType.FIT_TO_SCREEN);
		mImage.setDoubleTapListener( new OnImageViewTouchDoubleTapListener() {
			@Override
			public void onDoubleTap() {
			}
		});
		mImage.setOnDrawableChangedListener( new OnDrawableChangeListener() {
			@Override
			public void onDrawableChanged( Drawable drawable ) {
			}
		});
		mImgPreview = (LinearLayout)findViewById(R.id.imgPreview);
		if (mPlaceItemsAdapter != null && !mPlaceItemsAdapter.isEmpty()) {
			mPlaceItemsAdapter.clear();
		}
		
//		loadPlace();
		photos();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 2 && (mArgazkiak == null || mArgazkiak.isEmpty())) {
			return;
		}
		Intent intent = new Intent(mContext, PlaceSubDetailActivity.class);
		Place place = mPlaceItemsAdapter.getPlaceItem();
		PlaceHistory placeHistory = (PlaceHistory) parent.getItemAtPosition(position);
		intent.putExtra("subdetail_place", place);
		intent.putExtra("subdetail_place_history", placeHistory);
		switch (position) {
		case 1:
			intent.putExtra("subdetail_type", SubDetailTypes.TYPE_COMMENTS);
			startActivity(intent);
			overridePendingTransition (R.anim.open_next, R.anim.close_main);
			break;
		case 2:
			intent.putExtra("subdetail_type", SubDetailTypes.TYPE_PHOTOS);
			startActivity(intent);
			overridePendingTransition (R.anim.open_next, R.anim.close_main);
			break;
		default:
			break;
		}
	}

	public void loadPlace() {
		RequestParams paramsPlace = new RequestParams();
		paramsPlace.put("token", MintzatuAPI.getToken(mContext));
		paramsPlace.put("id", MintzatuAPI.getUserid(mContext).toString());
		paramsPlace.put("idPlace", mPlace.id_lekua.toString());

		MintzatuAPI.post(MintzatuAPI.PLACE, paramsPlace, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0) {
						mPlace = new Place(response.getJSONObject("place"));
						if (mPlace != null) {
							mActionBar.setTitle(mPlace.izena);
							photos();
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

	public void photos() {
		RequestParams paramsPhotos = new RequestParams();
		paramsPhotos.put("token", MintzatuAPI.getToken(mContext));
		paramsPhotos.put("id", MintzatuAPI.getUserid(mContext).toString());
		paramsPhotos.put("idPlace", mPlace.id_lekua.toString());

		MintzatuAPI.post(MintzatuAPI.PHOTOS, paramsPhotos, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0 && !response.isNull("pictures")) {
						JSONArray pictures = response.getJSONArray("pictures");
						for (int i = 0; i < pictures.length(); i++) {
							JSONObject picture = pictures.getJSONObject(i);
							final String url = picture.getString("tinyImg");
							if(url != null) {
								new Thread(new Runnable() {
									public void run() {
										mArgazkiak.add(ImageHelper.getBitmapFromURL(url));
									}
								}).start();
							}
						}
					} else if (code == 0 && response.isNull("pictures")) {
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					history();
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

	public void history() {
		RequestParams paramsHistories = new RequestParams();
		paramsHistories.put("token", MintzatuAPI.getToken(mContext));
		paramsHistories.put("id", MintzatuAPI.getUserid(mContext).toString());
		paramsHistories.put("idPlace", mPlace.id_lekua.toString());
		paramsHistories.put("page", PAGE.toString());

		MintzatuAPI.post(MintzatuAPI.HISTORIES, paramsHistories, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0 && !response.isNull("activities")) {
						JSONArray placeActivities = response.getJSONArray("activities");
						mHistories = new ArrayList<PlaceHistory>();
						for (int i = 0; i < placeActivities.length(); i++) {
							PlaceHistory placeHistory = new PlaceHistory(placeActivities.getJSONObject(i));
							mHistories.add(placeHistory);
						}
					} else if (code == 0 && response.isNull("activities")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_activities_not_found), Toast.LENGTH_LONG).show();
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					if (PAGE > 1) {
						if (mPlaceItemsAdapter == null) {
							mPlaceItemsAdapter = (PlaceItemsAdapter) mItemZerrenda.getAdapter();
						}
						if (!mHistories.isEmpty()) {
							mPlaceItemsAdapter.addItemList(mHistories);
						}
					} else {
						mPlaceItemsAdapter = new PlaceItemsAdapter(PlaceActivity.this, mPlace, mArgazkiak, mHistories);
						mItemZerrenda.setAdapter(mPlaceItemsAdapter);
						//mItemZerrenda.setOnItemClickListener(PlaceActivity.this);
						mItemZerrenda.setOnScrollListener(PlaceActivity.this);
					}
					if (mIsFirstLoad) {
						mViewSwitcher.showNext();
						mIsFirstLoad = false;
					}
					setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
				} catch (JSONException e) {
					e.printStackTrace();
					mViewSwitcher.showNext();
					setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mViewSwitcher.showNext();
				setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHECKIN_ACTION) {
			if (resultCode == RESULT_OK) {
				Bundle res = data.getExtras();
				if (res != null) {
					ArrayList<Badge> result = res.getParcelableArrayList("checkin_results");
					if (!result.isEmpty()) {
						WindowManager.LayoutParams lp = getWindow().getAttributes();
						lp.dimAmount=0.0f;  
						getWindow().setAttributes(lp);  
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
						Badge badge = result.get(0);
						mBadgeText.setText(badge.name);
						mBadgeDescText.setText(badge.desc);
						Picasso.with(mContext)
						.load(badge.img)
						.placeholder(R.drawable.placeholder)
						.error(R.drawable.placeholder)
						.resize(150, 150)
						.centerInside()
						.into(mBadgeImage);
						mBadgeLayout.setVisibility(View.VISIBLE);
					}
				}
				if (mPlaceItemsAdapter != null && !mPlaceItemsAdapter.isEmpty()) {
					mPlaceItemsAdapter.clear();
					mArgazkiak.clear();
				}
				loadPlace();
			}
		} else if (requestCode == COMMENT_ACTION) {
			if (resultCode == RESULT_OK) {
				if (mPlaceItemsAdapter != null && !mPlaceItemsAdapter.isEmpty()) {
					mPlaceItemsAdapter.clear();
					mArgazkiak.clear();
				}
				loadPlace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.place, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			overridePendingTransition (R.anim.open_main, R.anim.close_next);
			return true;
		case R.id.action_checkin:
			if (mPlace != null && mPlace.izena != null) {
				Intent intent = new Intent(mContext, CheckinActivity.class);
				intent.putExtra("place", mPlace);
				startActivityForResult(intent, CHECKIN_ACTION);
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}
			return true;
		case R.id.action_comment:
			if (mPlace != null && mPlace.izena != null) {
				Intent intent = new Intent(mContext, IruzkinaActivity.class);
				intent.putExtra("placeId", mPlace.id_lekua);
				intent.putExtra("placeName", mPlace.izena);
				startActivityForResult(intent, COMMENT_ACTION);
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig ) {
		super.onConfigurationChanged( newConfig );
	}

	public void handleImageTransformation(Bitmap bm, String url) {
		Picasso.with(mContext)
		.load(url)
		.resize(250, 250)
		.centerInside()
		.error(R.drawable.placeholder)
		.into(target);
	}

	public void clean(ImageViewTouch image) {
		if (image != null) {
			unbindDrawables(image);
		}
		System.gc();
	}

	private void unbindDrawables(ImageView view) {		
		view.setImageDrawable(null);
		if (view.getDrawable() != null) {
			view.getDrawable().setCallback(null);
		}	
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (mItemZerrenda.getLastVisiblePosition() >= mItemZerrenda.getCount() - 4) {
				setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
				PAGE++;
				history();
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
	}
}
