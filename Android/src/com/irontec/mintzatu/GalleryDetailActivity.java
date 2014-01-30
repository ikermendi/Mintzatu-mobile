package com.irontec.mintzatu;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.models.Picture;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryDetailActivity extends SherlockActivity {

	private Picture mPicture;
	private ImageViewTouch mImage;
	private TextView mIzena;
	private TextView mNoiz;
	
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
		setContentView(R.layout.activity_gallery_detail);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		mImage = (ImageViewTouch) findViewById(R.id.image);
		mIzena = (TextView) findViewById(R.id.izena);
		mNoiz = (TextView) findViewById(R.id.ordua);

		Intent intent = getIntent();
		if (intent != null) {
			mPicture = intent.getParcelableExtra("picture");
		}

		Picasso.with(this)
		.load(mPicture.normalImg)
		.into(mImage);
		
		if (mPicture.username != null && mPicture.username != "") {
			mIzena.setText(mPicture.username);
		}
		if (mPicture.datetime != null && mPicture.datetime != "") {
			DateTime dt = new DateTime(Long.valueOf(mPicture.datetime) * 1000);
			DateTime fixedDate = dt.plusHours(2);
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:ss:mm");
			mNoiz.setText(fixedDate.toString(fmt));
		}
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
