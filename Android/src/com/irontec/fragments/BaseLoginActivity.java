package com.irontec.fragments;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.irontec.adapters.LoginFragmentAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.dialogs.LoginDialog;
import com.irontec.dialogs.SignInDialog;
import com.irontec.mintzatu.R;
import com.viewpagerindicator.PageIndicator;

public class BaseLoginActivity extends SherlockFragmentActivity {

	LoginFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;
	RelativeLayout mButtons;
	RelativeLayout mControls;
	Button mBtnSartu;
	Button mBtnErregistratu;
	LoginDialog mLoginDialog;
	SignInDialog mSignInDialog;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_start:
			mControls.setVisibility(View.GONE);
			mButtons.setVisibility(View.VISIBLE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
