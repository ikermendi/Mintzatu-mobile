package com.irontec.fragments;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.irontec.api.MintzatuAPI;
import com.irontec.helpers.ImageHelper;
import com.irontec.helpers.LocationHelper;
import com.irontec.mintzatu.PlaceActivity;
import com.irontec.mintzatu.R;
import com.irontec.mintzatu.SearchActivity;
import com.irontec.models.Me;
import com.irontec.models.Place;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class EsploratuFragment extends SherlockFragment implements 
OnInfoWindowClickListener, OnClickListener, OnCameraChangeListener, 
GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener ,
LocationListener {

	private static final String TAG = EsploratuFragment.class.getSimpleName();
	private static final String ACTION_SEARCH_PLACES_RESULT = "search-places-result";
	private static Integer SEARCH_ACTION = 104;
	protected GoogleMap mMap;
	private Context mContext;
	private SupportMapFragment mMapFragment;
	private Location mUserLocation;
	private LatLng mCoords;
	private Integer PAGE = 1;
	private Integer DISTANCE = 2;
	private Float LIMIT_DISTANCE = 1.3f;
	private ArrayList<Marker> mMarkers;
	private ArrayList<String> mMarkersId;
	private ArrayList<Long> mPlacesId;
	private LayoutInflater mLayoutInflater;
	private Boolean mIsNecesaryReload = true;
	private Bitmap mUserAvatar;
	private ImageButton mLocate;
	private CameraPosition mCameraPosition;
	private ViewSwitcher mViewSwitcher;
	private Boolean mIsFirstLoad = true;
	private LinearLayout mLoadingLayout;
	private LocationManager mLocationManager;
	private static final long MIN_TIME_FOR_LOCATION_UPDATES = 120000;
	private static final long MIN_DISTANCE_FOR_LOCATION_UPDATES = 500;
	private ArrayList<Place> mPlaces;
	private static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	private static final long UPDATE_INTERVAL =
			MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	private static final long FASTEST_INTERVAL =
			MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	LocationRequest mLocationRequest;
	LocationClient mLocationClient;
	boolean mUpdatesRequested;

	public EsploratuFragment() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(
				LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		mLocationClient = new LocationClient(getActivity().getBaseContext(), this, this);
		mUpdatesRequested = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		mContext = getActivity().getBaseContext();

		View v = inflater.inflate(R.layout.fragment_esploratu, container, false);
		//		mViewSwitcher = (ViewSwitcher)v.findViewById(R.id.viewSwitcher1);
		mLoadingLayout = (LinearLayout)v.findViewById(R.id.loadingLayout);
		mLocate = (ImageButton)v.findViewById(R.id.center);
		mLocate.setOnClickListener(this);
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		//		mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		//		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_LOCATION_UPDATES, MIN_DISTANCE_FOR_LOCATION_UPDATES, locationListener);

		mMapFragment = SupportMapFragment.newInstance();
		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.add(R.id.map_fragment, mMapFragment);
		fragmentTransaction.commit();
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if(status == ConnectionResult.SUCCESS && mIsNecesaryReload) {
			setupMap();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		//		mLocationManager.removeUpdates(LekuakFragment.this);
	}

	@Override
	public void onStart() {
		super.onStart();
		mLocationClient.connect();
		EasyTracker.getInstance(getActivity()).activityStart(getActivity());
	}

	@Override
	public void onStop() {
		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();
		super.onStop();
		EasyTracker.getInstance(getActivity()).activityStop(getActivity());
	}

	private void setupMap() {

		mMap = mMapFragment.getMap();
		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			private final View contents = mLayoutInflater.inflate(R.layout.map_info_window, null);
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}
			@Override
			public View getInfoContents(Marker marker) {
				String izena = marker.getTitle();
				TextView txtIzena = ((TextView) contents.findViewById(R.id.izena));
				TextView txtHelbidea = ((TextView) contents.findViewById(R.id.helbidea));
				txtIzena.setText(izena);
				String helbidea = marker.getSnippet();
				if (helbidea != null) {
					String[] temp = helbidea.split("-");
					txtHelbidea.setText(temp[1]);
				} else {
					txtHelbidea.setVisibility(View.GONE);
				}
				return contents;
			}
		});
		mMap.setOnCameraChangeListener(this);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		mMap.setMyLocationEnabled(false);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.getUiSettings().setCompassEnabled(false);
		mMap.getUiSettings().setMyLocationButtonEnabled(false);
		mMap.getUiSettings().setRotateGesturesEnabled(true);
		mMap.getUiSettings().setScrollGesturesEnabled(true);
		mMap.getUiSettings().setTiltGesturesEnabled(true);
		mMap.getUiSettings().setZoomGesturesEnabled(true);
		mMap.setTrafficEnabled(false);

		if(mUserLocation == null) {
			//			Criteria criteria = new Criteria();
			//			String provider = mLocationManager.getBestProvider(criteria, false);
			//			mUserLocation = mLocationManager.getLastKnownLocation(provider);
			if(mUserLocation != null) {
				mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
				profile();
			}
		} else {
			mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
			profile();
		}

		if (mCoords != null) {
			mCameraPosition = updateCamera(mCoords);
			if(mCameraPosition != null) {
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(updateCamera(mCoords)));
				//centerMap();
			}
		}

	}

	private void profile() {
		RequestParams paramsMe = new RequestParams();
		paramsMe.put("id", MintzatuAPI.getUserid(mContext).toString());
		paramsMe.put("idProfile", MintzatuAPI.getUserid(mContext).toString());
		paramsMe.put("token", MintzatuAPI.getToken(mContext));

		MintzatuAPI.post(MintzatuAPI.PROFILE, paramsMe, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0) {
						final Me me = new Me(response);
						new Thread(new Runnable() {
							public void run() {
								mUserAvatar = ImageHelper.getBitmapFromURL(me.img);
								if (getActivity() != null && !getActivity().isFinishing()) {
									getActivity().runOnUiThread(new Runnable() {
										public void run() {
											if (mUserAvatar != null) {
												mMap.addMarker(
														new MarkerOptions()
														.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(mUserAvatar, 80, 80, false)))
														.title(me.fullname)
														.position(mCoords)
														);
											}
										}
									});
								}
							}
						}).start();
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginCircles.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					explore();
				} catch (JSONException e) {
					e.printStackTrace();
					getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
			}
		});
	}

	public void explore() {
		RequestParams paramsExplore = new RequestParams();
		paramsExplore.put("token", MintzatuAPI.getToken(mContext));
		paramsExplore.put("id", MintzatuAPI.getUserid(mContext).toString());
		paramsExplore.put("lat", Double.valueOf(mUserLocation.getLatitude()).toString());
		paramsExplore.put("lng", Double.valueOf(mUserLocation.getLongitude()).toString());
		paramsExplore.put("distance", DISTANCE.toString());

		MintzatuAPI.post(MintzatuAPI.EXPLORE, paramsExplore, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0 && !response.isNull("places")) {
						JSONArray jsonPlaces = response.getJSONArray("places");
						if(jsonPlaces.length() > 0) {
							mMarkers = new ArrayList<Marker>();
							mPlacesId = new ArrayList<Long>();
							mMarkersId = new ArrayList<String>();
							mPlaces = new ArrayList<Place>();
							for (int i = 0; i < jsonPlaces.length(); i++) {
								final Place place = new Place(jsonPlaces.getJSONObject(i));
								if (place != null && place.lat != null && place.lng != null) {
									mPlaces.add(place);
									new Thread(new Runnable() {
										public void run() {
											final LatLng mPlaceCoords = new LatLng(place.lat, place.lng);
											final Bitmap mPlaceAvatar = ImageHelper.getBitmapFromURL(place.kategoriak.get(0).imgUrl);
											if (getActivity() != null && !getActivity().isFinishing()) {
												getActivity().runOnUiThread(new Runnable() {
													public void run() {
														if (mUserAvatar != null) {
															Marker marker = mMap.addMarker(
																	new MarkerOptions()
																	.position(mPlaceCoords)
																	.title(place.izena)
																	.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(mPlaceAvatar, 80, 80, false)))
																	.snippet(place.id_lekua + "-" + place.helbidea)
																	);
															mPlacesId.add(place.id_lekua);
															mMarkersId.add(marker.getId());
															mMarkers.add(marker);
														}
													}
												});
											}
										}
									}).start();
									mMap.setOnInfoWindowClickListener(EsploratuFragment.this);
								}
							}
						}
					}else if (code == 0 && response.isNull("places")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_places_not_found), Toast.LENGTH_LONG).show();
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginCircles.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					if (mIsFirstLoad) {
						//						mViewSwitcher.showNext();
						mLoadingLayout.setVisibility(View.GONE);
						mIsFirstLoad = false;
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

	public void exploreNewLimits(LatLng cameraPosition) {

		RequestParams paramsExplore = new RequestParams();
		paramsExplore.put("token", MintzatuAPI.getToken(mContext));
		paramsExplore.put("id", MintzatuAPI.getUserid(mContext).toString());
		paramsExplore.put("lat", Double.valueOf(cameraPosition.latitude).toString());
		paramsExplore.put("lng", Double.valueOf(cameraPosition.longitude).toString());
		paramsExplore.put("distance", DISTANCE.toString());

		MintzatuAPI.post(MintzatuAPI.EXPLORE, paramsExplore, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0 && !response.isNull("places")) {
						JSONArray jsonPlaces = response.getJSONArray("places");
						if(jsonPlaces.length() > 0) {
							mMarkers = new ArrayList<Marker>();
							mPlacesId = new ArrayList<Long>();
							mMarkersId = new ArrayList<String>();
							mPlaces = new ArrayList<Place>();
							for (int i = 0; i < jsonPlaces.length(); i++) {
								final Place place = new Place(jsonPlaces.getJSONObject(i));
								if (place != null && place.lat != null && place.lng != null) {
									mPlaces.add(place);
									new Thread(new Runnable() {
										public void run() {
											final LatLng mPlaceCoords = new LatLng(place.lat, place.lng);
											final Bitmap mPlaceAvatar = ImageHelper.getBitmapFromURL(place.kategoriak.get(0).imgUrl);
											if (getActivity() != null && !getActivity().isFinishing()) {
												getActivity().runOnUiThread(new Runnable() {
													public void run() {
														if (mUserAvatar != null) {
															Marker marker = mMap.addMarker(
																	new MarkerOptions()
																	.position(mPlaceCoords)
																	.title(place.izena)
																	.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(mPlaceAvatar, 80, 80, false)))
																	.snippet(place.id_lekua + "-" + place.helbidea)
																	);
															mPlacesId.add(place.id_lekua);
															mMarkersId.add(marker.getId());
															mMarkers.add(marker);
														}
													}
												});
											}
										}
									}).start();
									mMap.setOnInfoWindowClickListener(EsploratuFragment.this);
								}
							}
						}
					}else if (code == 0 && response.isNull("places")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_places_not_found), Toast.LENGTH_LONG).show();
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginCircles.class);
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

	@Override
	public void onInfoWindowClick(Marker marker) {
		String snippet = marker.getSnippet();
		if (snippet == null) {
			return;
		}
		String[] temp = snippet.split("-");
		if (temp != null && temp[0] != null && temp[0] != "") {
			Long tempPlaceId = Long.valueOf(temp[0]);
			Intent intent = new Intent(mContext, PlaceActivity.class);
			for (int i = 0; i < mPlaces.size(); i++) {
				if (mPlaces.get(i).id_lekua.equals(tempPlaceId)) {
					intent.putExtra("place", mPlaces.get(i));
					break;
				}
			}
			mIsNecesaryReload = true;
			startActivity(intent);
			getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
		}
	}

	public CameraPosition updateCamera(LatLng coords) {
		if(coords != null) {
			return new CameraPosition.Builder()
			.target(coords)
			.zoom(15)
			.bearing(0)
			.tilt(0)
			.build();			
		} else {
			return null;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SEARCH_ACTION) {
			if (resultCode == Activity.RESULT_OK) {
				mIsNecesaryReload = false;
				Bundle res = data.getExtras();
				Place place = res.getParcelable("search_result");
				LatLng markerPos = new LatLng(place.lat, place.lng);
				BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
				Marker marker = null;
				if (mPlacesId.contains(place.id_lekua)) {
					for (int i = 0; i < mMarkersId.size(); i++) {
						if (mPlacesId.get(i) == place.id_lekua) {
							marker = mMarkers.get(i);
							marker.setIcon(bitmapDescriptor);
							break;
						}
					}
				} else {
					marker = mMap.addMarker(
							new MarkerOptions()
							.position(markerPos)
							.title(place.izena)
							.icon(bitmapDescriptor)
							.snippet(place.id_lekua + "-" + place.helbidea)
							);
					mPlacesId.add(place.id_lekua);
					mMarkersId.add(marker.getId());
					mMarkers.add(marker);
				}
				marker.showInfoWindow();
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(updateCamera(markerPos)));
			}
		}
	}

	public GoogleMap getMap() {
		return mMap;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.esploratu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_websearch:
			Intent intent = new Intent(mContext, SearchActivity.class);
			intent.putExtra("action", ACTION_SEARCH_PLACES_RESULT);
			startActivityForResult(intent, SEARCH_ACTION);
			getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View view) {
		mCameraPosition = updateCamera(mCoords);
		if(view.getId() == mLocate.getId()) {
			if(mCameraPosition != null) {
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
			}
		}		
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		LatLng cameraPosition = null;
		if (position != null) {
			cameraPosition = position.target;
		}
		if (cameraPosition != null && mCoords != null) {
			float[] results = new float[1];
			Location.distanceBetween(mCoords.latitude, mCoords.longitude, cameraPosition.latitude, cameraPosition.longitude, results);
			Log.d(TAG, "Distancia: " + results[0] / 1000 + "km");
			if (results[0] > 0) {
				float km = results[0] / 1000;
				if (km >= LIMIT_DISTANCE) {
					Log.d(TAG, (results[0] >= LIMIT_DISTANCE) + "");
					exploreNewLimits(cameraPosition);
				}
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		mUserLocation = location;
		if(mUserLocation != null) {
			mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
			profile();
		}
		if (mCoords != null) {
			mCameraPosition = updateCamera(mCoords);
			mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
			if(mCameraPosition != null && mMap != null) {
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(updateCamera(mCoords)));
			}
		}
		mLocationClient.removeLocationUpdates(this);

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (mUpdatesRequested) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
	}

	@Override
	public void onDisconnected() {
	}

}
