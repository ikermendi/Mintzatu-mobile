package com.irontec.mintzatu;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.adapters.FriendRequestAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.models.FriendRequest;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FriendRequestActivity extends SherlockActivity {

	private Context mContext;
	private ListView mZerrenda;
	
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
		setContentView(R.layout.activity_friend_request);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mContext = getBaseContext();
		
		mZerrenda = (ListView)findViewById(R.id.mezuZerrenda);
		
		friendRequests();
	}

	public void friendRequests() {
		RequestParams friendRequestParams = new RequestParams();
		friendRequestParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		friendRequestParams.put("token", MintzatuAPI.getToken(mContext));
		
		MintzatuAPI.post(MintzatuAPI.FRIEND_REQUEST, friendRequestParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					if (error == 0 && !response.isNull("requests")) {
						JSONArray requestsJson = response.getJSONArray("requests");
						ArrayList<FriendRequest> requests = new ArrayList<FriendRequest>();
						for (int i = 0; i < requestsJson.length(); i++) {
							JSONObject requestJson = requestsJson.getJSONObject(i);
							FriendRequest request = new FriendRequest(requestJson);
							requests.add(request);
						}
						loadAdapter(requests);
					} else if (error == 0 && response.isNull("requests")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_requests_not_found), Toast.LENGTH_LONG).show();
					} else if (error == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivity(intent);
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
	
	public void loadAdapter(ArrayList<FriendRequest> requests) {
		FriendRequestAdapter adapter = new FriendRequestAdapter(getBaseContext(), requests);
		mZerrenda.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.message, menu);
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
