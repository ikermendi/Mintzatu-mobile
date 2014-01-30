package com.irontec.fragments;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.view.Window;
import com.irontec.adapters.LoginFragmentAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.dialogs.LoginDialog;
import com.irontec.dialogs.SignInDialog;
import com.irontec.helpers.FacebookHelper;
import com.irontec.mintzatu.CheckinActivity;
import com.irontec.mintzatu.MainActivity;
import com.irontec.mintzatu.R;
import com.irontec.mintzatu.SigninActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLoginListener;
import com.sromku.simple.fb.SimpleFacebook.OnProfileRequestListener;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.utils.Logger;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class LoginCircles extends BaseLoginActivity implements OnPageChangeListener {

	private static final String TAG = LoginCircles.class.getSimpleName();
	private SimpleFacebook mSimpleFacebook;
	private Context mContext;
	private LinearLayout mLayoutFbSignIn;
	private String mCGMRegid;
	private Dialog mDialog;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mSimpleFacebook.getProfile(mOnProfileRequestListener);
		}
	};
	private OnProfileRequestListener mOnProfileRequestListener = new SimpleFacebook.OnProfileRequestListener()
	{
		@Override
		public void onFail(String reason) { Log.w(TAG, reason); mDialog.dismiss();}
		@Override
		public void onException(Throwable throwable) { Log.e(TAG, "Exception " + throwable.toString()); mDialog.dismiss(); }
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
	
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = FacebookHelper.getSimpleFacebookInstance(LoginCircles.this);
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_circles);

		Logger.DEBUG = true;
		
		mAdapter = new LoginFragmentAdapter(getSupportFragmentManager());

		mContext = getBaseContext();

		mDialog = new Dialog(LoginCircles.this);
		mDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		mDialog.setCancelable(false);
		mDialog.setContentView(R.layout.dialog_simple_loading);

		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mControls = (RelativeLayout)findViewById(R.id.controls);
		mButtons = (RelativeLayout)findViewById(R.id.buttons);

		mBtnSartu = (Button)findViewById(R.id.btnSartu);
		mBtnErregistratu = (Button)findViewById(R.id.btnErregistratu);

		if (MintzatuAPI.getUserid(getBaseContext()) != null && MintzatuAPI.getToken(getBaseContext()) != null) {
			Intent intent = new Intent(getBaseContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		mBtnSartu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoginDialog = new LoginDialog(LoginCircles.this);
				mLoginDialog.show();
			}
		});

		mBtnErregistratu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), SigninActivity.class);
				startActivity(intent);
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}
		});
		mLayoutFbSignIn = (LinearLayout)findViewById(R.id.layoutFbSignIn);
		mLayoutFbSignIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSimpleFacebook = FacebookHelper.getSimpleFacebookInstance(LoginCircles.this);
				mDialog.show();
				if (FacebookHelper.isConnected(LoginCircles.this)) {
					MintzatuAPI.registerGCM(LoginCircles.this, mHandler);
				} else {
					mSimpleFacebook.login(new OnLoginListener() {
						@Override
						public void onFail(String reason) { 
							Log.e(TAG, reason); 
							if (mSimpleFacebook != null) {
								mSimpleFacebook.clean();
							}
							mDialog.dismiss();
						}
						@Override
						public void onException(Throwable throwable) { 
							Log.e(TAG, throwable.toString());
							if (mSimpleFacebook != null) {
								mSimpleFacebook.clean();
							}
							mDialog.dismiss();
						}
						@Override
						public void onThinking() { 
							Log.d(TAG, "Thinking Login"); 
						}
						@Override
						public void onLogin() {
							FacebookHelper.setFacebookAccessToken(mContext, mSimpleFacebook);
							MintzatuAPI.registerGCM(LoginCircles.this, mHandler);
						}
						@Override
						public void onNotAcceptingPermissions() {
							Log.d(TAG, "No permisions");
							mDialog.dismiss();
						}
					});
				}
			}
		});
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mIndicator = indicator;
		indicator.setViewPager(mPager);
		indicator.setOnPageChangeListener(this);

		final float density = getResources().getDisplayMetrics().density;
		indicator.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		indicator.setRadius(10 * density);
		indicator.setPageColor(getResources().getColor(R.color.button_blue));
		indicator.setFillColor(getResources().getColor(R.color.mintzatu_orange));
		indicator.setStrokeColor(0xFF000000);
		indicator.setStrokeWidth(2 * density);
		mPager.setOffscreenPageLimit(2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (position == 3) {
			mControls.setVisibility(View.GONE);
			mButtons.setVisibility(View.VISIBLE);
		} else {
			mControls.setVisibility(View.VISIBLE);
			mButtons.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPageSelected(int position) {}

	private void signinFb(String email, String username, String idFb, String firstName, String lastName) {
		mCGMRegid = MintzatuAPI.getGCMRegid(mContext);
		Log.d(TAG, "mCGMRegid - "+mCGMRegid);
		RequestParams params = new RequestParams();
		params.put("firstName", firstName);
		params.put("username", username);
		params.put("email", email);
		params.put("lastName", lastName);
		params.put("idFb", idFb);
		params.put("uuid", mCGMRegid);
		params.put("fb", "true");

		MintzatuAPI.post(MintzatuAPI.SIGN_IN, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					Log.d(TAG, response.toString());
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
					mDialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mDialog.dismiss();
				Toast.makeText(mContext, getString(R.string.api_failed), Toast.LENGTH_LONG).show();
			}
		});
	}

}