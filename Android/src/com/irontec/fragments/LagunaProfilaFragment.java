package com.irontec.fragments;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.api.MintzatuAPI;
import com.irontec.mintzatu.BadgeActivity;
import com.irontec.mintzatu.MayorshipActivity;
import com.irontec.mintzatu.R;
import com.irontec.models.FriendProfile;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;


public class LagunaProfilaFragment extends SherlockFragment {

	private static final String TAG = LagunaProfilaFragment.class.getSimpleName();
	private static Context mContext;
	private TextView mIzena;
	private TextView mLekua;
	private TextView mLekuaText;
	private LinearLayout mDominak;
	private ImageView mArgazkia;
	private ListView mZerrenda;
	private LinearLayout mEskaerak;
	private LinearLayout mLagunEgin;
	private LinearLayout mAlkatetzak;
	private ArrayList<String> mIzenak;
	private ViewSwitcher mViewSwitcher;
	private TextView mZenbatAlkatetzak;
	private TextView mZenbatDomin;
	private TextView mZenbat;
	private Long mFriendId;
	private Dialog mDialog;

	public LagunaProfilaFragment() {}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		EasyTracker.getInstance(getActivity()).activityStart(getActivity());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance(getActivity()).activityStop(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profila_laguna, container, false);

		mDialog = new Dialog(getActivity());
		mDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		mDialog.setCancelable(false);
		mDialog.setContentView(R.layout.dialog_simple_loading);

		mContext = getActivity().getBaseContext();

		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher1);
		mIzena = (TextView)rootView.findViewById(R.id.izena);
		mArgazkia = (ImageView)rootView.findViewById(R.id.avatar);
		mZerrenda = (ListView)rootView.findViewById(R.id.zerrenda);
		mDominak = (LinearLayout)rootView.findViewById(R.id.dominak);
		mEskaerak = (LinearLayout)rootView.findViewById(R.id.eskaerak);
		mLagunEgin = (LinearLayout)rootView.findViewById(R.id.lagunEskaera);
		mAlkatetzak = (LinearLayout)rootView.findViewById(R.id.alkatetza);
		mZenbatAlkatetzak = (TextView)rootView.findViewById(R.id.zenbatAlkatetzak);
		mZenbatDomin = (TextView)rootView.findViewById(R.id.zenbatDomin);
		mZenbat = (TextView)rootView.findViewById(R.id.zenbat);
		mLekua = (TextView)rootView.findViewById(R.id.lekua);
		mLekuaText = (TextView)rootView.findViewById(R.id.lekuatxt);

		Bundle extras = getArguments();
		if (extras != null) {
			mFriendId = (Long) extras.get("friendId");
		}
		if (mFriendId != null) {
			mEskaerak.setVisibility(View.GONE);
			loadFriendProfile();
		}

		mLagunEgin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendFriendRequest();
			}
		});
		mDominak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, BadgeActivity.class);
				if (mFriendId != null) {
					intent.putExtra("friendId", mFriendId);
				}
				startActivity(intent);
				getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}
		});
		mAlkatetzak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MayorshipActivity.class);
				if (mFriendId != null) {
					intent.putExtra("friendId", mFriendId);
				}
				startActivity(intent);
				getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}
		});

		return rootView;

	}

	private void loadFriendProfile() {
		
		Log.d("ProfilaFragment", "LOAD FRIEND");
		
		RequestParams params = new RequestParams();
		params.put("id", MintzatuAPI.getUserid(mContext).toString());
		params.put("idProfile", mFriendId.toString());
		params.put("token", MintzatuAPI.getToken(mContext));

		MintzatuAPI.post(MintzatuAPI.PROFILE, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0) {
						final FriendProfile friendProfile = new FriendProfile(response);
						mIzena.setText(friendProfile.fullname);
						mZenbatAlkatetzak.setText(friendProfile.mayorships.toString());
						mZenbatDomin.setText(friendProfile.badges.toString());
						if (friendProfile.lastPlaceName != null && !friendProfile.lastPlaceName.equals("")) {
							mLekua.setText(friendProfile.lastPlaceName);
							mLekuaText.setVisibility(View.VISIBLE);
						} else {
							if (isAdded()) {
								mLekua.setText(getResources().getString(R.string.no_checkin_yet));
							}
						}
						if (friendProfile.friends != null && friendProfile.friends) {
							mLagunEgin.setVisibility(View.GONE);
						} else if (friendProfile.friends != null && !friendProfile.friends) {
							mLagunEgin.setVisibility(View.VISIBLE);
						} else if (friendProfile.friendshipState != null && friendProfile.friendshipState == 1) {
							if (isAdded()) {
								mLagunEgin.setBackgroundColor(mContext.getResources().getColor(R.color.mintzatu_orange));
							}
						}

						Picasso.with(mContext)
						.load(friendProfile.img)
						.placeholder(R.drawable.user_placeholder)
						.error(R.drawable.user_placeholder)
						.skipCache()
						.into(mArgazkia);

						mIzenak = new ArrayList<String>();
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						if (isAdded()) {
							Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						}
						Intent intent = new Intent(mContext, LoginCircles.class);
						startActivity(intent);
					} else {
						if (isAdded()) {
							Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
						}
					}
					mViewSwitcher.showNext();
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


	public void sendFriendRequest() {
		RequestParams makeFriendsParams = new RequestParams();
		makeFriendsParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		makeFriendsParams.put("token", MintzatuAPI.getToken(mContext).toString());
		makeFriendsParams.put("idProfile", mFriendId.toString());

		MintzatuAPI.post(MintzatuAPI.SEND_FRIEND_REQUEST, makeFriendsParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					if (error == 0) {
						if (isAdded()) {
							mLagunEgin.setBackgroundColor(mContext.getResources().getColor(R.color.mintzatu_orange));
						}
					} else if (error == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						if (isAdded()) {
							Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						}
						Intent intent = new Intent(mContext, LoginCircles.class);
						startActivity(intent);
					} else {
						if (isAdded()) {
							Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
						}
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

}
