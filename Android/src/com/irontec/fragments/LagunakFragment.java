package com.irontec.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.adapters.FriendListAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.mintzatu.ProfilaActivity;
import com.irontec.mintzatu.R;
import com.irontec.mintzatu.SearchActivity;
import com.irontec.models.Friend;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class LagunakFragment extends SherlockFragment implements OnItemClickListener{

	private static final String ACTION_SEARCH_PEOPLE = "search-people";
	private static Integer SEARCH_ACTION = 108;
	private Context mContext;
	private ListView mLagunZerrenda;
	private ViewSwitcher mViewSwitcher;
	private FriendListAdapter mFriendAdapter;
	private Boolean mIsFirstLoad = true;

	public LagunakFragment() {}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(getActivity()).activityStart(getActivity());
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(getActivity()).activityStop(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		mContext = getActivity().getBaseContext();

		View rootView = inflater.inflate(R.layout.fragment_lagunak, container, false);
		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher1);
		mLagunZerrenda = (ListView) rootView.findViewById(R.id.friend_list);

		loadFriends();

		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("changeToProfileFragment", "VA A ENTRAR DESDE EL ITEMCLICK");
		changeToProfileFragment(id);
	}

	private void loadFriends() {
		RequestParams friendsParams = new RequestParams();
		friendsParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		friendsParams.put("idProfile", MintzatuAPI.getUserid(mContext).toString());
		friendsParams.put("token", MintzatuAPI.getToken(mContext));

		MintzatuAPI.post(MintzatuAPI.FRIENDS, friendsParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					if (error == 0 && !response.isNull("allFriends")) {
						ArrayList<Friend> friends = new ArrayList<Friend>();
						JSONArray friendsJson = response.getJSONArray("allFriends");
						for (int i = 0; i < friendsJson.length(); i++) {
							JSONObject friendJson = friendsJson.getJSONObject(i);
							Friend friend = new Friend();
							friend.id = friendJson.getLong("id");
							friend.username = friendJson.getString("name");
							friend.userImage = friendJson.getString("userImage");
							friends.add(friend);
						}
						mFriendAdapter = new FriendListAdapter(mContext, friends);
						mLagunZerrenda.setAdapter(mFriendAdapter);
						mLagunZerrenda.setOnItemClickListener(LagunakFragment.this);
					}else if (error == 0 && response.isNull("friends")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_friends_not_found), Toast.LENGTH_LONG).show();
					} else if (error == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginCircles.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					if (mIsFirstLoad) {
						mViewSwitcher.showNext();
						mIsFirstLoad = false;
					} else {
						mViewSwitcher.showPrevious();
						mIsFirstLoad = true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					mViewSwitcher.showNext();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mViewSwitcher.showNext();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SEARCH_ACTION) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle res = data.getExtras();
				Long friendId = res.getLong("search_result");
				changeToProfileFragment(friendId);
			}
		}
	}

	private void changeToProfileFragment(Long friendId) {
		Bundle conData = new Bundle();
		conData.putLong("search_result", friendId);
		Intent intent = new Intent(getActivity().getBaseContext(), ProfilaActivity.class);
		intent.putExtras(conData);
		getActivity().startActivity(intent);
		getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.lagunak, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_websearch:
			Intent intent = new Intent(mContext, SearchActivity.class);
			intent.putExtra("action", ACTION_SEARCH_PEOPLE);
			startActivityForResult(intent, SEARCH_ACTION);
			getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
