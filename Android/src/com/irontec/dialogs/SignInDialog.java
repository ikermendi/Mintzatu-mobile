package com.irontec.dialogs;

import org.json.JSONObject;

import com.irontec.api.MintzatuAPI;
import com.irontec.helpers.FacebookHelper;
import com.irontec.helpers.StringUtils;
import com.irontec.mintzatu.MainActivity;
import com.irontec.mintzatu.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLoginListener;
import com.sromku.simple.fb.SimpleFacebook.OnProfileRequestListener;
import com.sromku.simple.fb.entities.Profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SignInDialog extends Dialog{
	
	private final static String TAG = SignInDialog.class.getSimpleName();
	private Activity mActivity;
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
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			signin();
		}
	};
	private OnProfileRequestListener mOnProfileRequestListener = new SimpleFacebook.OnProfileRequestListener()
	{
	    @Override
	    public void onFail(String reason) { Log.w(TAG, reason); }
	    @Override
	    public void onException(Throwable throwable) { Log.e(TAG, "Exception " + throwable.toString()); }
	    @Override
	    public void onThinking() { Log.i(TAG, "Thinking..."); }

	    @Override
	    public void onComplete(Profile profile)
	    {
	        mPosta.setText(profile.getEmail());
	    }
	};
	private OnLoginListener mOnLoginListener = new OnLoginListener() {
		@Override
		public void onFail(String reason) { Log.e(TAG, reason); }
		@Override
		public void onException(Throwable throwable) { Log.e(TAG, throwable.toString()); }
		@Override
		public void onThinking() { Log.d(TAG, "Thinking"); }
		@Override
		public void onLogin() {
			FacebookHelper.setFacebookAccessToken(mContext, mSimpleFacebook);
			mSimpleFacebook.getProfile(mOnProfileRequestListener);
		}
		@Override
		public void onNotAcceptingPermissions() {}
	};

	public SignInDialog(Activity activity, SimpleFacebook simpleFacebook) {
		super(activity);
		this.mActivity = activity;
		this.mContext = activity.getBaseContext();
		this.mSimpleFacebook = simpleFacebook;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_signin);
		
		mPosta = (EditText) findViewById(R.id.posta);
		mEzizena = (EditText) findViewById(R.id.ezizena);
		mPasahitza = (EditText) findViewById(R.id.pasahitza);
		mPasahitzaBerriro = (EditText) findViewById(R.id.pasahitzaBerriro);
		
		mBtnErregistratu = (Button) findViewById(R.id.btnErregistratu);
		mBtnEzeztatu = (Button) findViewById(R.id.btnEzeztatu);
		
		mLayoutFbSignIn = (LinearLayout)findViewById(R.id.layoutFbSignIn);
		mLayoutFbSignIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (FacebookHelper.isConnected(mActivity)) {
					mSimpleFacebook.getProfile(mOnProfileRequestListener);
				} else {
					mSimpleFacebook.login(mOnLoginListener);
				}
			}
		});
		
		enableFields();
		
		mProgress = (ProgressBar) findViewById(R.id.progress);
		
		mBtnErregistratu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				mProgress.setVisibility(View.VISIBLE);
				disableFields();
				MintzatuAPI.registerGCM(mActivity, mHandler);
			}
		});
		mBtnEzeztatu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgress.setVisibility(View.INVISIBLE);
				InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
				SignInDialog.this.dismiss();
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
							MintzatuAPI.setUserid(response.getInt("id"), mActivity.getBaseContext());
							MintzatuAPI.setToken(response.getString("token"), mActivity.getBaseContext());
							MintzatuAPI.setUserName(response.getString("username"), mActivity.getBaseContext());
							Intent intent = new Intent(mActivity.getBaseContext(), MainActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
							mActivity.startActivity(intent);
							SignInDialog.this.dismiss();
						} else if (error == MintzatuAPI.ERROR_PASSWORD_TOO_SHORT) {
							Toast.makeText(mActivity.getBaseContext(), mActivity.getString(R.string.password_too_short), Toast.LENGTH_LONG).show();
						} else if (error == MintzatuAPI.ERROR_REGISTERED_EMAIL) {
							Toast.makeText(mActivity.getBaseContext(), mActivity.getString(R.string.registered_email), Toast.LENGTH_LONG).show();
						} else if (error == MintzatuAPI.ERROR_REGISTERED_USER) {
							Toast.makeText(mActivity.getBaseContext(), mActivity.getString(R.string.registered_user), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mActivity.getBaseContext(), mActivity.getString(R.string.bad_signin), Toast.LENGTH_LONG).show();
						}
						InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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
					InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
					Toast.makeText(mActivity.getBaseContext(), mActivity.getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					SignInDialog.this.dismiss();
				}
			});
		} else {
			mProgress.setVisibility(View.INVISIBLE);
			enableFields();
			Toast.makeText(mActivity.getBaseContext(), mActivity.getString(R.string.password_no_match), Toast.LENGTH_LONG).show();
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
	
}
