package com.irontec.mintzatu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.models.Badge;
import com.squareup.picasso.Picasso;

public class BadgeDetailActivity extends SherlockActivity {

	private static Badge mBadge;
	
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
		setContentView(R.layout.activity_badge_detail);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		ImageView argazkia = (ImageView) findViewById(R.id.argazkia);
		TextView izena = (TextView) findViewById(R.id.izena);
		TextView deskribapena = (TextView) findViewById(R.id.deskribapena);

		Intent intent = getIntent();
		if (intent != null) {
			mBadge = intent.getParcelableExtra("badge");
		}

		if (mBadge != null) {
			Picasso.with(this)
			.load(mBadge.img)
			.fit()
			.into(argazkia);
			
			if (mBadge.name != null && !mBadge.name.equals("")) {
				izena.setText(mBadge.name);
			}
			if (mBadge.desc != null && !mBadge.desc.equals("")) {
				deskribapena.setText(mBadge.desc);
			}
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.gallery_detail, menu);
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

}
