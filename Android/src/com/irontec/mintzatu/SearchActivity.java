package com.irontec.mintzatu;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.model.LatLng;
import com.irontec.adapters.SearchAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.helpers.SegmentedRadioGroup;
import com.irontec.models.KategoriaSinplea;
import com.irontec.models.People;
import com.irontec.models.Place;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class SearchActivity extends SherlockActivity implements OnItemClickListener, OnCheckedChangeListener {

	private static final String ACTION_SEARCH_PEOPLE = "search-people";
	private static final String ACTION_SEARCH_PLACES = "search-places";
	private static final String ACTION_SEARCH_PLACES_RESULT = "search-places-result";
	private static String ACTION = "";
	private Integer ITEMS = 50;
	private Integer PAGE = 1;
	private Context mContext;
	private EditText mOmniBox;
	private ImageButton mOmniButton;
	private ListView mOmniList;
	private Location mUserLocation;
	private LatLng mCoords;
	private SearchAdapter mSearchAdapter;
	private LinearLayout mNoResults;
	private TextView mIzenburua;
	private ArrayList<People> people = new ArrayList<People>();
	private ArrayList<Place> places = new ArrayList<Place>();
	private Dialog mDialog;
	private LocationManager mLocationManager;
	private SegmentedRadioGroup segmentText;
	private RadioButton mButtonOne;
	private RadioButton mButtonTwo;
	private RadioButton mButtonThree;
	private ArrayList<KategoriaSinplea> mKategoriak;

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
		setContentView(R.layout.activity_search);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mContext = getBaseContext();

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Intent intent = getIntent();
		if (intent != null) {
			ACTION = intent.getStringExtra("action");
		}

		segmentText = (SegmentedRadioGroup) findViewById(R.id.segment_text);
		segmentText.setOnCheckedChangeListener(this);
		mButtonOne = (RadioButton)findViewById(R.id.button_one);
		mButtonTwo = (RadioButton)findViewById(R.id.button_two);
		mButtonThree = (RadioButton)findViewById(R.id.button_three);

		if (ACTION.equals(ACTION_SEARCH_PEOPLE)) {
			segmentText.clearCheck();
			mButtonThree.setChecked(true);
		} else if (ACTION.equals(ACTION_SEARCH_PLACES_RESULT)) {
			segmentText.clearCheck();
			mButtonTwo.setChecked(true);
		} else {
			segmentText.clearCheck();
			mButtonOne.setChecked(true);
			performCategorySearch();
		}

		mDialog = new Dialog(SearchActivity.this);
		mDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		mDialog.setCancelable(false);
		mDialog.setContentView(R.layout.dialog_simple_loading);

		mOmniList = (ListView)findViewById(R.id.omnilist);
		mOmniList.setOnItemClickListener(this);

		mSearchAdapter = new SearchAdapter(mContext);
		if (ACTION.equals(ACTION_SEARCH_PEOPLE)) {
			mSearchAdapter.setAdapterTypePeople(people);
		} else if (ACTION.equals(ACTION_SEARCH_PLACES) || ACTION.equals(ACTION_SEARCH_PLACES_RESULT)){
			mSearchAdapter.setAdapterTypePlaces(places);
			if(mUserLocation == null) {
				Criteria criteria = new Criteria();
				String provider = mLocationManager.getBestProvider(criteria, false);
				mUserLocation = mLocationManager.getLastKnownLocation(provider);
				if(mUserLocation != null) {
					mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
				}
			}
		} else {
			SearchActivity.this.finish();
		}
		mOmniList.setAdapter(mSearchAdapter);

		mOmniBox = (EditText)findViewById(R.id.omnibox);
		mOmniBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent keyEvent) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mDialog.show();
					if (segmentText.getCheckedRadioButtonId() == R.id.button_one) {
						performCategorySearch();
					} else if (segmentText.getCheckedRadioButtonId() == R.id.button_two) {
						performPlacesSearch(mOmniBox.getText());
					} else {
						performPeopleSearch(mOmniBox.getText());
					}
					return true;
				}
				return false;
			}
		});
		mOmniButton = (ImageButton)findViewById(R.id.omnibuttom);
		mOmniButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mDialog.show();
				if (segmentText.getCheckedRadioButtonId() == R.id.button_one) {
					performCategorySearch();
				} else if (segmentText.getCheckedRadioButtonId() == R.id.button_two) {
					performPlacesSearch(mOmniBox.getText());
				} else {
					performPeopleSearch(mOmniBox.getText());
				}
			}
		});

		mNoResults = (LinearLayout)findViewById(R.id.noresults);
		mNoResults.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, NewPlaceActivity.class);
				intent.putExtra("placeName", mOmniBox.getText().toString().trim());
				startActivity(intent);
			}
		});
		mIzenburua = (TextView)findViewById(R.id.izenburua);
	}

	private void performCategorySearch() {
		RequestParams categoriesParams = new RequestParams();
		categoriesParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		categoriesParams.put("token", MintzatuAPI.getToken(mContext).toString());

		MintzatuAPI.post(MintzatuAPI.GET_CATEGORIES, categoriesParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					if (error == 0 && !response.isNull("categories")) {
						mSearchAdapter.clearAdapter();
						JSONArray categoriesJson = response.getJSONArray("categories");
						mKategoriak = new ArrayList<KategoriaSinplea>();
						for (int i = 0; i < categoriesJson.length(); i++) {
							JSONObject categoryJson = categoriesJson.getJSONObject(i);
							KategoriaSinplea kategoriaSimplea = new KategoriaSinplea(categoryJson);
							mKategoriak.add(kategoriaSimplea);
						}
						mSearchAdapter.addCategoryList(mKategoriak);
					} else if (error == 0 && response.isNull("categories")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_categories_not_found), Toast.LENGTH_LONG).show();
					} else if (error == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					mDialog.dismiss();
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

	private void performPeopleSearch(Editable editable) {
		if (editable != null && editable.toString().trim().length() < 2) {
			mDialog.dismiss();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.api_min_search_lenght), Toast.LENGTH_SHORT).show();
			return;
		}
		mNoResults.setVisibility(View.GONE);
		RequestParams omniPeopleParams = new RequestParams();
		omniPeopleParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		omniPeopleParams.put("token", MintzatuAPI.getToken(mContext));
		omniPeopleParams.put("items", ITEMS.toString());
		omniPeopleParams.put("page", PAGE.toString());
		if (editable != null && !editable.toString().trim().equals("")) {
			omniPeopleParams.put("subject", editable.toString().trim());
		} else {
			omniPeopleParams.put("subject", "%");
		}

		MintzatuAPI.post(MintzatuAPI.SEARCH_PEOPLE, omniPeopleParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0 && !response.isNull("places")) {
						mSearchAdapter.clearAdapter();
						JSONArray peopleJson = response.getJSONArray("places");
						ArrayList<People> items = new ArrayList<People>();
						for (int i = 0; i < peopleJson.length(); i++) {
							People person = new People(peopleJson.getJSONObject(i));
							items.add(person);
						}
						mSearchAdapter.addPeopleList(items);
					} else if (code == 0 && response.isNull("places")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_people_not_found), Toast.LENGTH_LONG).show();
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					mDialog.dismiss();
				} catch (JSONException e) {
					e.printStackTrace();
					mDialog.dismiss();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mDialog.dismiss();
			}
		});
	}

	private void performPlacesSearch(final Editable editable) {
		if (editable != null && editable.toString().trim().length() < 2) {
			mDialog.dismiss();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.api_min_search_lenght), Toast.LENGTH_SHORT).show();
			return;
		}
		mNoResults.setVisibility(View.VISIBLE);
		RequestParams omniPlacesParams = new RequestParams();
		omniPlacesParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		omniPlacesParams.put("token", MintzatuAPI.getToken(mContext));
		omniPlacesParams.put("items", ITEMS.toString());
		omniPlacesParams.put("page", PAGE.toString());
		if (editable != null && editable.toString().trim().equals("")) {
			omniPlacesParams.put("subject", "%");
		} else {
			omniPlacesParams.put("subject", editable.toString());
		}
		if (mCoords == null) {
			omniPlacesParams.put("lat", Double.valueOf(0).toString());
			omniPlacesParams.put("lng", Double.valueOf(0).toString());
		} else {
			omniPlacesParams.put("lat", Double.valueOf(mCoords.latitude).toString());
			omniPlacesParams.put("lng", Double.valueOf(mCoords.longitude).toString());
		}

		MintzatuAPI.post(MintzatuAPI.SEARCH_PLACES, omniPlacesParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0 && !response.isNull("places")) {
						if (response.getJSONArray("places").length() > 0) {
							mSearchAdapter.clearAdapter();
							JSONArray placesJson = response.getJSONArray("places");
							ArrayList<Place> items = new ArrayList<Place>();
							for (int i = 0; i < placesJson.length(); i++) {
								Place place = new Place(placesJson.getJSONObject(i));
								items.add(place);
							}
							mSearchAdapter.addPlaceList(items);
						} else {
							mIzenburua.setText(mContext.getResources().getString(R.string.noresults2, editable));
						}
					} else if (code == 0 && response.isNull("places")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_places_not_found), Toast.LENGTH_LONG).show();
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					mDialog.dismiss();
				} catch (JSONException e) {
					e.printStackTrace();
					mDialog.dismiss();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mDialog.dismiss();
			}
		});
	}

	private void performPlacesSearchByCategory(final Editable editable, Long idCat) {
		segmentText.clearCheck();
		mButtonTwo.setChecked(true);
		mNoResults.setVisibility(View.VISIBLE);
		RequestParams omniPlacesByCatParams = new RequestParams();
		omniPlacesByCatParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		omniPlacesByCatParams.put("token", MintzatuAPI.getToken(mContext));
		omniPlacesByCatParams.put("items", ITEMS.toString());
		omniPlacesByCatParams.put("page", PAGE.toString());
		omniPlacesByCatParams.put("idKategoria", idCat.toString());
		if (editable != null && editable.toString().trim().equals("")) {
			omniPlacesByCatParams.put("subject", "%");
		} else {
			omniPlacesByCatParams.put("subject", editable.toString());
		}
		if (mCoords == null) {
			omniPlacesByCatParams.put("lat", Double.valueOf(0).toString());
			omniPlacesByCatParams.put("lng", Double.valueOf(0).toString());
		} else {
			omniPlacesByCatParams.put("lat", Double.valueOf(mCoords.latitude).toString());
			omniPlacesByCatParams.put("lng", Double.valueOf(mCoords.longitude).toString());
		}

		MintzatuAPI.post(MintzatuAPI.SEARCH_PLACES, omniPlacesByCatParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					mSearchAdapter.clearAdapter();
					if(code == 0 && !response.isNull("places")) {
						if (response.getJSONArray("places").length() > 0) {
							JSONArray placesJson = response.getJSONArray("places");
							ArrayList<Place> items = new ArrayList<Place>();
							for (int i = 0; i < placesJson.length(); i++) {
								Place place = new Place(placesJson.getJSONObject(i));
								items.add(place);
							}
							mSearchAdapter.addPlaceList(items);
						} else {
							mIzenburua.setText(mContext.getResources().getString(R.string.noresults2, editable));
						}
					} else if (code == 0 && response.isNull("places")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_places_not_found), Toast.LENGTH_LONG).show();
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					mDialog.dismiss();
				} catch (JSONException e) {
					e.printStackTrace();
					mDialog.dismiss();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mDialog.dismiss();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		/*if (ACTION.equals(ACTION_SEARCH_PLACES_RESULT)) {
			Place place = (Place) parent.getItemAtPosition(position);
			finishWithPlaceResult(place);
		} else */
		if (segmentText.getCheckedRadioButtonId() == R.id.button_one) {
			KategoriaSinplea kategoria = (KategoriaSinplea) parent.getItemAtPosition(position);
			performPlacesSearchByCategory(mOmniBox.getText(), kategoria.id);
		} else if (segmentText.getCheckedRadioButtonId() == R.id.button_two) {
			//sergio
			Place place;
			try{
				place = (Place) parent.getItemAtPosition(position);
				Intent intent = new Intent(mContext, PlaceActivity.class);
				intent.putExtra("place", place);
				startActivity(intent);
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
				
			}catch (Exception ex){
				place = null;
				Log.d("Error search", ex.getMessage());
				segmentText.clearCheck();
				mButtonTwo.setChecked(true);
				mIzenburua.setText(mContext.getResources().getString(R.string.noresults2, mOmniBox.getText()));
				mNoResults.setVisibility(View.VISIBLE);
			}
			//sergio
			/*
			Place place = (Place) parent.getItemAtPosition(position);
			Intent intent = new Intent(mContext, PlaceActivity.class);
			intent.putExtra("place", place);
			startActivity(intent);
			overridePendingTransition (R.anim.open_next, R.anim.close_main);
			*/
		} else {
			People people = (People) parent.getItemAtPosition(position);
			finishWithPeopleResult(people);
		}
	}

	private void finishWithPeopleResult(People people) {
		Bundle conData = new Bundle();
		conData.putLong("search_result", people.id);
		Intent intent = new Intent(getBaseContext(), ProfilaActivity.class);
		intent.putExtras(conData);
		startActivity(intent);
		overridePendingTransition (R.anim.open_next, R.anim.close_main);
	}

	private void finishWithPlaceResult(Place place) {
		Bundle conData = new Bundle();
		conData.putParcelable("search_result", place);
		Intent intent = new Intent();
		intent.putExtras(conData);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.place_search, menu);
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

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (mOmniBox != null) {
			if (checkedId == R.id.button_one) {
				mNoResults.setVisibility(View.GONE);
				performCategorySearch();
			} else if (checkedId == R.id.button_two) {
//				performPlacesSearch(mOmniBox.getText());
				mNoResults.setVisibility(View.GONE);
				mSearchAdapter.clearAdapter();
			} else {
//				performPeopleSearch(mOmniBox.getText());
				mNoResults.setVisibility(View.GONE);
				mSearchAdapter.clearAdapter();
			}
		}
	}

}
