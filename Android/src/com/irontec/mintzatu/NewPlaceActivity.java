package com.irontec.mintzatu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.model.LatLng;
import com.irontec.adapters.KategoriakDialogAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.helpers.ImageHelper;
import com.irontec.helpers.LocationHelper;
import com.irontec.models.KategoriaSinplea;
import com.irontec.models.Place;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class NewPlaceActivity extends SherlockFragmentActivity {

	private static final int NEW_PLACE_MAP_ACTION = 105;
	private Location mUserLocation;
	private LatLng mCoords;
	private ImageHelper mImageHelper = new ImageHelper();
	private ImageView mStaticMap;
	private Context mContext;
	private LatLng mFinalPlacePosition;
	private Button mGehituLekua;
	private EditText mIzena;
	private EditText mHelbidea;
	private EditText mKategoria;
	private EditText mHerria;
	private Long mKategoriaId;
	private String mPlaceName;
	private ArrayList<KategoriaSinplea> mKategoriak;
	private KategoriaSinplea mKategoriaSinplea;
	private Dialog mDialog;
	private boolean mGeocoderServiceNotAvailable;
	private LocationManager mLocationManager;

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
		setContentView(R.layout.activity_new_place);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mContext = getBaseContext();

		mDialog = new Dialog(NewPlaceActivity.this);
		mDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		mDialog.setCancelable(false);
		mDialog.setContentView(R.layout.dialog_simple_loading);

		Intent intent = getIntent();
		if (intent != null) {
			mPlaceName = intent.getStringExtra("placeName");
		}

		mStaticMap = (ImageView)findViewById(R.id.staticMap);
		mStaticMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, NewPlaceMapActivity.class);
				startActivityForResult(intent, NEW_PLACE_MAP_ACTION);
			}
		});

		mGehituLekua = (Button)findViewById(R.id.gehitu);
		mGehituLekua.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.show();
				addNewPlace();
			}
		});

		mIzena = (EditText)findViewById(R.id.izena);
		if (mPlaceName != null && mPlaceName != "") {
			mIzena.setText(mPlaceName);
		}
		mHelbidea = (EditText)findViewById(R.id.helbidea);
		mHerria = (EditText)findViewById(R.id.town);
		mKategoria = (EditText)findViewById(R.id.kategoria);
		mKategoria.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getCategories();
				}
			}
		});

		loadMap();

	}

	private void addNewPlace() {
		if (mIzena.getText().toString().trim().equals("")) {
			Toast.makeText(mContext, mContext.getResources().getString(R.string.noname), Toast.LENGTH_LONG).show();
			mDialog.dismiss();
			return;
		}
		if (mKategoria.getText().toString().trim().equals("")) {
			Toast.makeText(mContext, mContext.getResources().getString(R.string.nocategory), Toast.LENGTH_LONG).show();
			mDialog.dismiss();
			return;
		}

		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> addresses = new ArrayList<Address>();
		try {
			addresses = geocoder.getFromLocation(mFinalPlacePosition.latitude, mFinalPlacePosition.longitude, 1);
		} catch (IOException e1) {
			e1.printStackTrace();
			mGeocoderServiceNotAvailable = true;
		}

		RequestParams addPlaceParams = new RequestParams();
		addPlaceParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		addPlaceParams.put("token", MintzatuAPI.getToken(mContext).toString());
		addPlaceParams.put("name", mIzena.getText().toString());
		addPlaceParams.put("katId", mKategoriaSinplea.id.toString());
		if (mHelbidea != null && !mHelbidea.getText().toString().trim().equals("")) {
			addPlaceParams.put("address", mHelbidea.getText().toString());
		} else {
			if (!mGeocoderServiceNotAvailable && !addresses.isEmpty()) {
				Address address = addresses.get(0);
				addPlaceParams.put("address", address.getThoroughfare().toString());
			} else {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.specific_address_needed), Toast.LENGTH_LONG).show();
				mDialog.dismiss();
				return;
			}
		}

		if (mHerria != null && !mHerria.getText().toString().trim().equals("")) {
			addPlaceParams.put("town", mHelbidea.getText().toString());
		} else {
			if (!mGeocoderServiceNotAvailable && !addresses.isEmpty()) {
				Address address = addresses.get(0);
				addPlaceParams.put("town", address.getLocality().toString());
			} else {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.specific_address_needed), Toast.LENGTH_LONG).show();
				mDialog.dismiss();
				return;
			}
		}
//		if (mHelbidea != null && !mHelbidea.getText().toString().trim().equals("")) {
//			addPlaceParams.put("address", mHelbidea.getText().toString());
//			if (!addresses.isEmpty()) {
//				Address address = addresses.get(0);
//				addPlaceParams.put("town", address.getLocality().toString());
//			}
//		} else {
//			if (!mGeocoderServiceNotAvailable && !addresses.isEmpty()) {
//				Address address = addresses.get(0);
//				addPlaceParams.put("address", address.getThoroughfare().toString());
//				addPlaceParams.put("town", address.getLocality().toString());
//			} else {
//				Toast.makeText(mContext, mContext.getResources().getString(R.string.specific_address_needed), Toast.LENGTH_LONG).show();
//				return;
//			}
//		}
		addPlaceParams.put("lat", String.valueOf(mFinalPlacePosition.latitude));
		addPlaceParams.put("lng", String.valueOf(mFinalPlacePosition.longitude));

		MintzatuAPI.post(MintzatuAPI.ADD_PLACE, addPlaceParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					if (error == 0 && !response.isNull("place")) {
						Place place = new Place(response.getJSONObject("place"));
						Intent intent = new Intent(mContext, PlaceActivity.class);
						intent.putExtra("place", place);
						mDialog.dismiss();
						startActivity(intent);
					} else if (error == MintzatuAPI.ERROR_DUPLICATED_PLACE) {
						mDialog.dismiss();
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_duplicated_place), Toast.LENGTH_LONG).show();
					} else {
						mDialog.dismiss();
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					mDialog.dismiss();
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mDialog.dismiss();
			}
		});
	}

	private void loadMap() {
		if(mUserLocation == null) {
			Criteria criteria = new Criteria();
			String provider = mLocationManager.getBestProvider(criteria, false);
			mUserLocation = mLocationManager.getLastKnownLocation(provider);
//			LocationHelper locationHelper = new LocationHelper(this);
//			mUserLocation = locationHelper.getBestLocation();
			if(mUserLocation != null) {
				mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
				mFinalPlacePosition = mCoords;
				getStaticMap();
			}
		}

	}

	private void getStaticMap() {
		String staticMapUrl = null;
		try {
			staticMapUrl = MintzatuAPI.getStaticMapUrl(mCoords);
			mImageHelper.download(staticMapUrl, mStaticMap);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}



	public void getCategories() {

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
						JSONArray categoriesJson = response.getJSONArray("categories");
						mKategoriak = new ArrayList<KategoriaSinplea>();
						for (int i = 0; i < categoriesJson.length(); i++) {
							JSONObject categoryJson = categoriesJson.getJSONObject(i);
							KategoriaSinplea kategoriaSimplea = new KategoriaSinplea(categoryJson);
							mKategoriak.add(kategoriaSimplea);
						}
						loadDialog();
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

	public void loadDialog(){
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				NewPlaceActivity.this);
		builderSingle.setIcon(R.drawable.mintzatu);
		builderSingle.setTitle("Kategoria aukeratu");
		final KategoriakDialogAdapter arrayAdapter = new KategoriakDialogAdapter(mContext, mKategoriak);

		builderSingle.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mKategoriaSinplea = (KategoriaSinplea) arrayAdapter.getItem(which);
				mKategoria.setText(mKategoriaSinplea.name);
				dialog.dismiss();
			}
		});
		if (!NewPlaceActivity.this.isFinishing()) {
			builderSingle.show();
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == NEW_PLACE_MAP_ACTION) {
			if (resultCode == RESULT_OK) {
				Bundle res = data.getExtras();
				mFinalPlacePosition = res.getParcelable("map_location");
				String staticMapUrl = null;
				try {
					staticMapUrl = MintzatuAPI.getStaticMapUrl(mFinalPlacePosition);
					mImageHelper.download(staticMapUrl, mStaticMap);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.new_place, menu);
		return true;
	}

}
