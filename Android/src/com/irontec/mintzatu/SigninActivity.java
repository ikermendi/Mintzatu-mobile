package com.irontec.mintzatu;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.api.MintzatuAPI;
import com.irontec.dialogs.SignInDialog;
import com.irontec.fragments.LoginCircles;
import com.irontec.helpers.FacebookHelper;
import com.irontec.helpers.StringUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLoginListener;
import com.sromku.simple.fb.SimpleFacebook.OnProfileRequestListener;
import com.sromku.simple.fb.entities.Profile;


public class SigninActivity extends SherlockActivity {

	private static final String TAG = SigninActivity.class.getSimpleName();
	private Context mContext;
	private Button mBtnErregistratu;
	private Button mBtnEzeztatu;
	private EditText mPosta;
	private EditText mPasahitza;
	private EditText mEzizena;
	private EditText mPasahitzaBerriro;
	private ProgressBar mProgress;
	private String mCGMRegid;
	private LinearLayout mLayoutFbSignIn;
	private SimpleFacebook mSimpleFacebook;
	private ActionBar mActionBar;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			signin();
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

	private OnProfileRequestListener mOnProfileRequestListener = new SimpleFacebook.OnProfileRequestListener()
	{
		@Override
		public void onFail(String reason) { Log.w(TAG, reason); }
		@Override
		public void onException(Throwable throwable) { Log.e(TAG, "Exception " + throwable.toString()); }
		@Override
		public void onThinking() { Log.i(TAG, "Thinking Profile"); }

		@Override
		public void onComplete(Profile profile)
		{
			signinFb(
					profile.getEmail(),
					profile.getUsername(),
					profile.getId(),
					profile.getFirstName(),
					profile.getLastName());
		}
	};
	private OnLoginListener mOnLoginListener = new OnLoginListener() {
		@Override
		public void onFail(String reason) { 
			Log.e(TAG, reason); 
			if (mSimpleFacebook != null) {
				mSimpleFacebook.clean();
			}
		}
		@Override
		public void onException(Throwable throwable) { 
			Log.e(TAG, throwable.toString());
			if (mSimpleFacebook != null) {
				mSimpleFacebook.clean();
			}
		}
		@Override
		public void onThinking() { 
			Log.d(TAG, "Thinking Login"); 
		}
		@Override
		public void onLogin() {
			FacebookHelper.setFacebookAccessToken(mContext, mSimpleFacebook);
			mSimpleFacebook.getProfile(mOnProfileRequestListener);
		}
		@Override
		public void onNotAcceptingPermissions() {
			Log.d(TAG, "No permisions");
			if (mSimpleFacebook != null) {
				mSimpleFacebook.clean();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);

		mContext = getBaseContext();

		mSimpleFacebook = FacebookHelper.getSimpleFacebookInstance(SigninActivity.this);

		mPosta = (EditText) findViewById(R.id.posta);
		mEzizena = (EditText) findViewById(R.id.ezizena);
		mPasahitza = (EditText) findViewById(R.id.pasahitza);
		mPasahitzaBerriro = (EditText) findViewById(R.id.pasahitzaBerriro);

		mBtnErregistratu = (Button) findViewById(R.id.btnErregistratu);
		mBtnEzeztatu = (Button) findViewById(R.id.btnEzeztatu);

		/*mLayoutFbSignIn = (LinearLayout)findViewById(R.id.layoutFbSignIn);
		mLayoutFbSignIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (FacebookHelper.isConnected(SigninActivity.this)) {
					mSimpleFacebook.getProfile(mOnProfileRequestListener);
				} else {
					mSimpleFacebook.login(mOnLoginListener);
				}
			}
		});*/

		enableFields();

		mProgress = (ProgressBar) findViewById(R.id.progress);

		mBtnErregistratu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				mProgress.setVisibility(View.VISIBLE);
				disableFields();
				MintzatuAPI.registerGCM(SigninActivity.this, mHandler);
			}
		});
		mBtnEzeztatu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgress.setVisibility(View.INVISIBLE);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
			}
		});
	}

	private void signinFb(String email, String username, String idFb, String firstName, String lastName) {
		mCGMRegid = MintzatuAPI.getGCMRegid(mContext);			
		RequestParams params = new RequestParams();
		params.put("firstName", firstName);
		params.put("username", username);
		params.put("email", email);
		params.put("lastName", mPasahitza.getText().toString());
		params.put("idFb", mPasahitza.getText().toString());
		params.put("uuid", mCGMRegid);
		params.put("fb", "true");

		MintzatuAPI.post(MintzatuAPI.SIGN_IN, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					if(error == 0) {
						MintzatuAPI.setUserid(response.getInt("id"), mContext);
						MintzatuAPI.setToken(response.getString("token"), mContext);
						MintzatuAPI.setUserName(response.getString("username"), mContext);
						Intent intent = new Intent(mContext, MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					} else if (error == MintzatuAPI.ERROR_PASSWORD_TOO_SHORT) {
						Toast.makeText(mContext, getString(R.string.password_too_short), Toast.LENGTH_LONG).show();
					} else if (error == MintzatuAPI.ERROR_REGISTERED_EMAIL) {
						Toast.makeText(mContext, getString(R.string.registered_email), Toast.LENGTH_LONG).show();
					} else if (error == MintzatuAPI.ERROR_REGISTERED_USER) {
						Toast.makeText(mContext, getString(R.string.registered_user), Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(mContext, getString(R.string.bad_signin), Toast.LENGTH_LONG).show();
					}
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
					mProgress.setVisibility(View.INVISIBLE);
					enableFields();
				} catch (Exception e) {
					e.printStackTrace();
					mProgress.setVisibility(View.INVISIBLE);
					enableFields();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mProgress.setVisibility(View.INVISIBLE);
				enableFields();
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
				Toast.makeText(mContext, getString(R.string.api_failed), Toast.LENGTH_LONG).show();
			}
		});
	}

	private void signin() {
		mCGMRegid = MintzatuAPI.getGCMRegid(mContext);
		if (!StringUtils.isValidEmail(mPosta.getText().toString())) {
			mProgress.setVisibility(View.INVISIBLE);
			enableFields();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.bad_email2), Toast.LENGTH_LONG).show();
			return;
		}
		if (StringUtils.isEmptyString(mEzizena.getText().toString())) {
			mProgress.setVisibility(View.INVISIBLE);
			enableFields();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.bad_username), Toast.LENGTH_LONG).show();
			return;
		}
		if (StringUtils.isEmptyString(mEzizena.getText().toString())) {
			mProgress.setVisibility(View.INVISIBLE);
			enableFields();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.bad_name), Toast.LENGTH_LONG).show();
			return;
		}
		if (StringUtils.isEmptyString(mPasahitza.getText().toString())) {
			mProgress.setVisibility(View.INVISIBLE);
			enableFields();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.bad_password), Toast.LENGTH_LONG).show();
			return;
		}

		if(mPasahitza.getText().toString().equals(mPasahitzaBerriro.getText().toString())) {

			RequestParams params = new RequestParams();
			params.put("name", mEzizena.getText().toString());
			params.put("username", mEzizena.getText().toString());
			params.put("email", mPosta.getText().toString());
			params.put("password", mPasahitza.getText().toString());
			params.put("uuid", mCGMRegid);

			MintzatuAPI.post(MintzatuAPI.SIGN_IN, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, JSONObject response) {
					super.onSuccess(statusCode, response);
					try {
						int error = response.getInt("error");
						if(error == 0) {
							MintzatuAPI.setUserid(response.getInt("id"), mContext);
							MintzatuAPI.setToken(response.getString("token"), mContext);
							MintzatuAPI.setUserName(response.getString("username"), mContext);
							Intent intent = new Intent(mContext, MainActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						} else if (error == MintzatuAPI.ERROR_PASSWORD_TOO_SHORT) {
							Toast.makeText(mContext, getString(R.string.password_too_short), Toast.LENGTH_LONG).show();
						} else if (error == MintzatuAPI.ERROR_REGISTERED_EMAIL) {
							Toast.makeText(mContext, getString(R.string.registered_email), Toast.LENGTH_LONG).show();
						} else if (error == MintzatuAPI.ERROR_REGISTERED_USER) {
							Toast.makeText(mContext, getString(R.string.registered_user), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mContext, getString(R.string.bad_signin), Toast.LENGTH_LONG).show();
						}
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
						mProgress.setVisibility(View.INVISIBLE);
						enableFields();
					} catch (Exception e) {
						e.printStackTrace();
						mProgress.setVisibility(View.INVISIBLE);
						enableFields();
					}
				}
				@Override
				public void onFailure(Throwable e, JSONObject errorResponse) {
					super.onFailure(e, errorResponse);
					mProgress.setVisibility(View.INVISIBLE);
					enableFields();
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
					Toast.makeText(mContext, getString(R.string.api_failed), Toast.LENGTH_LONG).show();
				}
			});
		} else {
			mProgress.setVisibility(View.INVISIBLE);
			enableFields();
			Toast.makeText(mContext, getString(R.string.password_no_match), Toast.LENGTH_LONG).show();
		}
	}

	public void enableFields() {
		mPosta.setEnabled(true);
		mEzizena.setEnabled(true);
		mPasahitza.setEnabled(true);
		mPasahitzaBerriro.setEnabled(true);
		mBtnErregistratu.setEnabled(true);
	}

	public void disableFields() {
		mPosta.setEnabled(false);
		mEzizena.setEnabled(false);
		mPasahitza.setEnabled(false);
		mPasahitzaBerriro.setEnabled(false);
		mBtnErregistratu.setEnabled(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.signin, menu);
		return true;
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
