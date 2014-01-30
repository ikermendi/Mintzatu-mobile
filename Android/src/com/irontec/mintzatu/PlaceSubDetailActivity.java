package com.irontec.mintzatu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.fragments.CheckinTypeFragment;
import com.irontec.fragments.CommentsTypeFragment;
import com.irontec.fragments.PhotoTypeFragment;
import com.irontec.fragments.SingleCommentTypeFragment;
import com.irontec.fragments.SinglePhotoTypeFragment;
import com.irontec.models.Place;
import com.irontec.models.PlaceHistory;
import com.irontec.models.SubDetailTypes;

public class PlaceSubDetailActivity extends SherlockFragmentActivity  {

	private static Integer TYPE = 0;
	private Context mContext;
	private Place mPlace;
	private PlaceHistory mPlaceHistory;
	private ActionBar mActionBar;

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
		setContentView(R.layout.activity_place_sub_detail);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		mContext = getBaseContext();
		
		Intent intent = getIntent();
		if (intent != null) {
			TYPE = intent.getIntExtra("subdetail_type", 0);
			mPlace = intent.getParcelableExtra("subdetail_place");
			mPlaceHistory = intent.getParcelableExtra("subdetail_place_history");
			selectFragment(TYPE);
		}

	}

	private void selectFragment(Integer type) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Bundle arguments = new Bundle();
		arguments.putParcelable("place", mPlace);
		arguments.putParcelable("placeHistory", mPlaceHistory);
		if (type != null) {
			if (type == SubDetailTypes.TYPE_COMMENTS) {
				mActionBar.setTitle(R.string.sub_azalpenak);
				CommentsTypeFragment commentsTypeFragment = new CommentsTypeFragment();
				commentsTypeFragment.setArguments(arguments);
				ft.replace(R.id.subdetail_content, commentsTypeFragment);
			} else if (type == SubDetailTypes.TYPE_PHOTOS) {
				mActionBar.setTitle(R.string.sub_argazkiak);
				PhotoTypeFragment photoTypeFragment = new PhotoTypeFragment();
				photoTypeFragment.setArguments(arguments);
				ft.replace(R.id.subdetail_content, photoTypeFragment);
			} else if (type == SubDetailTypes.TYPE_CHECKIN){
				mActionBar.setTitle(R.string.sub_checkin);
				CheckinTypeFragment checkinTypeFragment = new CheckinTypeFragment();
				checkinTypeFragment.setArguments(arguments);
				ft.replace(R.id.subdetail_content, checkinTypeFragment);
			} else if (type == SubDetailTypes.TYPE_SINGLE_PHOTO){
				mActionBar.setTitle(R.string.sub_argazkia);
				SinglePhotoTypeFragment singlePhotoTypeFragment = new SinglePhotoTypeFragment();
				singlePhotoTypeFragment.setArguments(arguments);
				ft.replace(R.id.subdetail_content, singlePhotoTypeFragment);
			} else if (type == SubDetailTypes.TYPE_SINGLE_COMMENT){
				mActionBar.setTitle(R.string.sub_comment);
				SingleCommentTypeFragment singleCommentTypeFragment = new SingleCommentTypeFragment();
				singleCommentTypeFragment.setArguments(arguments);
				ft.replace(R.id.subdetail_content, singleCommentTypeFragment);
			}
		}
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.place_sub_detail, menu);
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
		overridePendingTransition (R.anim.open_next, R.anim.close_main);
	}

}
