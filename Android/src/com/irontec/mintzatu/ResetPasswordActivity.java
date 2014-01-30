package com.irontec.mintzatu;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.api.MintzatuAPI;
import com.irontec.dialogs.LoginDialog;
import com.irontec.fragments.LoginCircles;
import com.irontec.models.Me;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ResetPasswordActivity extends SherlockActivity {

	private final static String TAG = ResetPasswordActivity.class.getSimpleName();
	private Context mContext;
	private EditText mPosta;
	private ProgressBar mProgress;
	private Button mBerreskuratu;
	
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
		setContentView(R.layout.activity_reset_password);
		
		mContext = getBaseContext();
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		mPosta = (EditText)findViewById(R.id.posta);
		mProgress = (ProgressBar)findViewById(R.id.progress);
		mBerreskuratu = (Button)findViewById(R.id.btnBerreskuratu);
		
		enableFields();
		
		mBerreskuratu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgress.setVisibility(View.VISIBLE);
				disableFields();
				resetPassword();
			}
		});
		
	}
	
	public void resetPassword() {
		if (mPosta.getText().toString().trim().equals("")) {
			mProgress.setVisibility(View.INVISIBLE);
			enableFields();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.api_required_email), Toast.LENGTH_LONG).show();
			return;
		}
		RequestParams params = new RequestParams();
		params.put("email", mPosta.getText().toString().trim());

		MintzatuAPI.post(MintzatuAPI.RESET_PASSWORD, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_email_reseted), Toast.LENGTH_LONG).show();
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
						Intent intent = new Intent(mContext, LoginCircles.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					} else if (code == MintzatuAPI.ERROR_INVALID_RESET_PASSWORD_EMAIL) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_invalid_pwd_reset_email), Toast.LENGTH_LONG).show();
					} else if (code == MintzatuAPI.ERROR_MALFORMED_EMAIL) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_malformed_email), Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					mProgress.setVisibility(View.INVISIBLE);
					enableFields();
				} catch (JSONException e) {
					e.printStackTrace();
					mProgress.setVisibility(View.INVISIBLE);
					enableFields();
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mProgress.setVisibility(View.INVISIBLE);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
			}
		});
	}
	
	public void enableFields() {
		mPosta.setEnabled(true);
		mBerreskuratu.setEnabled(true);
	}
	
	public void disableFields() {
		mPosta.setEnabled(false);
		mBerreskuratu.setEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.reset_password, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
		Intent intent = new Intent(mContext, LoginCircles.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
			Intent intent = new Intent(mContext, LoginCircles.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
