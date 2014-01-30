package com.irontec.dialogs;

import org.json.JSONException;
import org.json.JSONObject;

import com.irontec.api.MintzatuAPI;
import com.irontec.fragments.BaseLoginActivity;
import com.irontec.helpers.StringUtils;
import com.irontec.mintzatu.MainActivity;
import com.irontec.mintzatu.R;
import com.irontec.mintzatu.ResetPasswordActivity;
import com.irontec.models.Me;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginDialog extends Dialog{

	private final static String TAG = LoginDialog.class.getSimpleName();
	private Activity mActivity;
	private Context mContext;
	private Button mBtnSartu;
	private Button mBtnEzeztatu;
	private EditText mPosta;
	private EditText mPasahitza;
	private TextView mTxtBerreskuratu;
	private ProgressBar mProgress;
	private String mCGMRegid;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			login();
		}
	};

	public LoginDialog(Activity activity) {
		super(activity);
		this.mActivity = activity;
		this.mContext = activity.getBaseContext();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_login);

		mPosta = (EditText) findViewById(R.id.posta);
		mPasahitza = (EditText) findViewById(R.id.pasahitza);

		mBtnSartu = (Button) findViewById(R.id.btnSartu);
		mBtnEzeztatu = (Button) findViewById(R.id.btnEzeztatu);

		enableFields();

		mProgress = (ProgressBar) findViewById(R.id.progress);
		mTxtBerreskuratu = (TextView) findViewById(R.id.txtBerreskuratu);
		mTxtBerreskuratu.setText(mContext.getResources().getString(R.string.txtBerreskuratu));
		mTxtBerreskuratu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity.getBaseContext(), ResetPasswordActivity.class);
				mActivity.startActivity(intent);
				LoginDialog.this.dismiss();
			}
		});

		mBtnSartu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgress.setVisibility(View.VISIBLE);
				mTxtBerreskuratu.setVisibility(View.GONE);
				disableFields();
				MintzatuAPI.registerGCM(mActivity, mHandler);
			}
		});
		mBtnEzeztatu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgress.setVisibility(View.INVISIBLE);
				mTxtBerreskuratu.setVisibility(View.VISIBLE);
				InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
				LoginDialog.this.dismiss();
			}
		});
	}

	public void login() {
		mCGMRegid = MintzatuAPI.getGCMRegid(mContext);

		if (!StringUtils.isValidEmail(mPosta.getText().toString())) {
			mProgress.setVisibility(View.INVISIBLE);
			mTxtBerreskuratu.setVisibility(View.VISIBLE);
			enableFields();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.bad_email), Toast.LENGTH_LONG).show();
			return;
		}

		RequestParams params = new RequestParams();
		params.put("user", mPosta.getText().toString());
		params.put("password", mPasahitza.getText().toString());
		params.put("uuid", mCGMRegid);

		MintzatuAPI.post(MintzatuAPI.LOGIN, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0) {
						MintzatuAPI.setUserid(response.getInt("id"), mActivity.getBaseContext());
						MintzatuAPI.setToken(response.getString("token"), mActivity.getBaseContext());
						MintzatuAPI.setUserName(response.getString("username"), mActivity.getBaseContext());
						Intent intent = new Intent(mActivity.getBaseContext(), MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						mActivity.startActivity(intent);
						LoginDialog.this.dismiss();
						//getUserProfile();
					} else if (code == MintzatuAPI.ERROR_BAD_PARAMS) {
						mProgress.setVisibility(View.INVISIBLE);
						mTxtBerreskuratu.setVisibility(View.VISIBLE);
						enableFields();
						Toast.makeText(mActivity.getBaseContext(), mActivity.getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					} else {
						mProgress.setVisibility(View.INVISIBLE);
						mTxtBerreskuratu.setVisibility(View.VISIBLE);
						enableFields();
						Toast.makeText(mActivity.getBaseContext(), mActivity.getString(R.string.bad_login), Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					mProgress.setVisibility(View.INVISIBLE);
					mTxtBerreskuratu.setVisibility(View.VISIBLE);
					enableFields();
					e.printStackTrace();
				}
				InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mProgress.setVisibility(View.INVISIBLE);
				mTxtBerreskuratu.setVisibility(View.VISIBLE);
				enableFields();
				Toast.makeText(mActivity.getBaseContext(), mActivity.getString(R.string.api_failed), Toast.LENGTH_LONG).show();
				InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPosta.getWindowToken(), 0);
				LoginDialog.this.dismiss();
			}
		});
	}

	public void enableFields() {
		mPosta.setEnabled(true);
		mPasahitza.setEnabled(true);
		mBtnSartu.setEnabled(true);
	}

	public void disableFields() {
		mPosta.setEnabled(false);
		mPasahitza.setEnabled(false);
		mBtnSartu.setEnabled(false);
	}

}
