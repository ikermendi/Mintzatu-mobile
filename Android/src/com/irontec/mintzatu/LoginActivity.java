package com.irontec.mintzatu;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.api.MintzatuAPI;
import com.irontec.dialogs.LoginDialog;
import com.irontec.dialogs.SignInDialog;
import com.irontec.mintzatu.R;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends SherlockActivity {

	private Button mBtnSartu;
	private Button mBtnErregistratu;
	private LoginDialog mLoginDialog;
	private SignInDialog mSignInDialog;
	private Context mContext;
	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mContext = getBaseContext();
		
		mBtnSartu = (Button)findViewById(R.id.btnSartu);
		mBtnErregistratu = (Button)findViewById(R.id.btnErregistratu);
		
		if (MintzatuAPI.getUserid(mContext) != null && MintzatuAPI.getToken(mContext) != null) {
			Intent intent = new Intent(mContext, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		
		mBtnSartu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoginDialog = new LoginDialog(LoginActivity.this);
				mLoginDialog.show();
			}
		});
		
		mBtnErregistratu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//mSignInDialog = new SignInDialog(LoginActivity.this);
				mSignInDialog.show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
