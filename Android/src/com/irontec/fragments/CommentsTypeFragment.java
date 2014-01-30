package com.irontec.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.adapters.SimpleCommentListAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.mintzatu.R;
import com.irontec.models.Comment;
import com.irontec.models.Place;
import com.irontec.models.PlaceHistory;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class CommentsTypeFragment extends SherlockFragment implements OnScrollListener {

	private Integer PAGE = 1;
	private Integer ITEMS = 20;
	private Context mContext;
	private ListView mCommentsZerrenda;
	private ViewSwitcher mViewSwitcher;
	private Place mPlace;
	private PlaceHistory mPlaceHistory;
	private ArrayList<Comment> mComments;
	private SimpleCommentListAdapter mCommentAdapter;
	private Boolean mIsFirstLoad = true;

	public CommentsTypeFragment() {}

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

		mContext = getActivity().getBaseContext();

		Bundle data = getArguments();
		if (data != null) {
			mPlace = (Place) data.get("place");
			mPlaceHistory = (PlaceHistory) data.get("placeHistory");
		}

		View rootView = inflater.inflate(R.layout.fragment_sub_comments, container, false);
		mCommentsZerrenda = (ListView)rootView.findViewById(R.id.comments_list);
		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher1);

		loadComments();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void loadComments() {
		RequestParams commentsParams = new RequestParams();
		commentsParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		commentsParams.put("idPlace", mPlace.id_lekua.toString());
		commentsParams.put("token", MintzatuAPI.getToken(mContext));
		commentsParams.put("page", PAGE.toString());
		commentsParams.put("items", ITEMS.toString());

		MintzatuAPI.post(MintzatuAPI.PLACE_COMMENTS, commentsParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					if (error == 0 && !response.isNull("comments")) {
						JSONArray commentsJson = response.getJSONArray("comments");
						mComments = new ArrayList<Comment>();
						for (int i = 0; i < commentsJson.length(); i++) {
							JSONObject commentJson = commentsJson.getJSONObject(i);
							Comment comment = new Comment(commentJson);
							mComments.add(comment);
						}
						if (PAGE > 1) {
							if (mCommentAdapter == null) {
								mCommentAdapter = (SimpleCommentListAdapter) mCommentsZerrenda.getAdapter();
							}
							for(Comment place : mComments) {
								mCommentAdapter.addItem(place);
							}
							mCommentAdapter.notifyDataSetChanged();
						} else {
							mCommentAdapter = new SimpleCommentListAdapter(mContext, mComments);
							mCommentsZerrenda.setAdapter(mCommentAdapter);
							mCommentsZerrenda.setOnScrollListener(CommentsTypeFragment.this);
						}
					} else if (error == 0 && response.isNull("comments")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_comments_not_found), Toast.LENGTH_LONG).show();
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
					}
					getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.lagunak, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (mCommentsZerrenda.getLastVisiblePosition() >= mCommentsZerrenda.getCount() - 4) {
				PAGE++;
				getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
				loadComments();
			}
		}
	}
}
