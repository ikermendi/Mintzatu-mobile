package com.irontec.mintzatu;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.api.MintzatuAPI;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class IruzkinaActivity extends SherlockActivity {

	private final static String TAG = IruzkinaActivity.class.getSimpleName();
	private Context mContext;
	private ActionBar mActionBar;
	private EditText mIruzkina;
	private TextView mIzena;
	private TextView mLuzeera;
	private Button mIruzkinaButton;
	private Long mPlaceId;
	private String mPlaceName;
	
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
		setContentView(R.layout.activity_iruzkina);
		
		this.mContext = getBaseContext();

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		if (intent != null) {
			mPlaceId = intent.getLongExtra("placeId", 0);
			mPlaceName = intent.getStringExtra("placeName");
		}
		
		mLuzeera = (TextView) findViewById(R.id.luzeera);
		mLuzeera.setText(getString(R.string.comment_lenght, 0));
		mIruzkina = (EditText) findViewById(R.id.iruzkina);
		mIruzkina.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	if (mLuzeera != null) {
					mLuzeera.setText(getString(R.string.comment_lenght, mIruzkina.getText().length()));
				}
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
		mIzena = (TextView) findViewById(R.id.izena);
		if (mPlaceName != null) {
			mIzena.setText(mPlaceName);
		}
		mIruzkinaButton = (Button) findViewById(R.id.iruzkinaEgin);
		mIruzkinaButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addComment();
			}
		});
		
	}
	
	public void addComment() {
		RequestParams paramsComment = new RequestParams();
		if (mIruzkina == null || mIruzkina.getText() == null) {
			return;
		} else {
			if (mIruzkina.getText().toString().trim().equals("")) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.empty_comment), Toast.LENGTH_LONG).show();
				return;
			}
		}
		paramsComment.put("token", MintzatuAPI.getToken(mContext));
		paramsComment.put("id", MintzatuAPI.getUserid(mContext).toString());
		paramsComment.put("idPlace", mPlaceId.toString());
		paramsComment.put("comment",  mIruzkina.getText().toString());

		MintzatuAPI.post(MintzatuAPI.ADD_COMMENT, paramsComment, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0) {
						finishWithResult();
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivity(intent);
						overridePendingTransition (R.anim.open_main, R.anim.close_next);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
			}
		});
	}
	
	private void finishWithResult() {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.iruzkina, menu);
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
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
	}
}
