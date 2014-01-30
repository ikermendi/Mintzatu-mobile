package com.irontec.fragments;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.irontec.adapters.MapListAdapter;
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


public class LekuakFragment extends SherlockFragment implements 
	LocationListener, OnMyLocationChangeListener, OnItemClickListener, OnClickListener, OnScrollListener, OnInfoWindowClickListener,
	GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

	private final static String TAG = LekuakFragment.class.getSimpleName();
	private final static String ACTION_SEARCH_PLACES = "search-places";
	private final static int FIRST_SPECIAL_ROW_MARGIN = 130;
	private final static int IN_ZOOM = 17;
	private Integer DISTANCE = 2;
	private Integer PAGE = 1;
	protected GoogleMap mMap;
	private Context mContext;
	private SupportMapFragment mMapFragment;
	private Location mUserLocation;
	private LatLng mCoords;
	private MapListAdapter mAdapter;
	private ListView mList;
	private Button mItxi;
	private LinearLayout mLayoutItxi;
	private ImageButton mLocate;
	private LinearLayout mLayoutLocate;
	private Boolean mHashBeenCalled = false;
	private View mView;
	private CameraPosition mCameraPosition;
	private LatLng mSpecialCoords;
	private Bitmap mUserAvatar;
	private ViewSwitcher mViewSwitcher;
	private Boolean mIsFirstLoad = true;
	private LinearLayout mLoadingLayout;
	private LocationManager mLocationManager;
	private ProgressBar mProgressBar;
	private static final long MIN_TIME_FOR_LOCATION_UPDATES = 500;
	private static final long MIN_DISTANCE_FOR_LOCATION_UPDATES = 500;
	private ArrayList<Place> mPlaces;
	private LayoutInflater mLayoutInflater;
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

	public LekuakFragment() {}

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

		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);

		mContext = getActivity().getBaseContext();

		mView = inflater.inflate(R.layout.fragment_lekuak, container, false);
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutItxi = (LinearLayout)mView.findViewById(R.id.layout_btn_itxi);
		mItxi = (Button)mView.findViewById(R.id.itxi);
		mItxi.setOnClickListener(this);
		mLayoutLocate = (LinearLayout)mView.findViewById(R.id.layout_btn_center);
		mLocate = (ImageButton)mView.findViewById(R.id.center);
		mLocate.setOnClickListener(this);
		mList = (ListView)mView.findViewById(R.id.map_list);
		mLoadingLayout = (LinearLayout)mView.findViewById(R.id.loadingLayout);
		mLoadingLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				return;
			}
		});
		mProgressBar = (ProgressBar)mView.findViewById(R.id.progressBar1);

//		mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//				MIN_TIME_FOR_LOCATION_UPDATES, MIN_DISTANCE_FOR_LOCATION_UPDATES, LekuakFragment.this);
//		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//				MIN_TIME_FOR_LOCATION_UPDATES, MIN_DISTANCE_FOR_LOCATION_UPDATES, LekuakFragment.this);
//		mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
//				MIN_TIME_FOR_LOCATION_UPDATES, MIN_DISTANCE_FOR_LOCATION_UPDATES, LekuakFragment.this);

		GoogleMapOptions options = new GoogleMapOptions();
		options.mapType(GoogleMap.MAP_TYPE_NORMAL)
		.zoomControlsEnabled(false);

		mMapFragment = SupportMapFragment.newInstance(options);
		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.add(R.id.map_fragment, mMapFragment);
		fragmentTransaction.commit();
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if(status == ConnectionResult.SUCCESS) {
			
//			if (mPrefs.contains("KEY_UPDATES_ON")) {
//	            mUpdatesRequested =
//	                    mPrefs.getBoolean("KEY_UPDATES_ON", false);
//	        } else {
//	            mEditor.putBoolean("KEY_UPDATES_ON", false);
//	            mEditor.commit();
//	        }
//			
//			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//					MIN_TIME_FOR_LOCATION_UPDATES, MIN_DISTANCE_FOR_LOCATION_UPDATES, LekuakFragment.this);
//			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//					MIN_TIME_FOR_LOCATION_UPDATES, MIN_DISTANCE_FOR_LOCATION_UPDATES, LekuakFragment.this);
//			mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
//					MIN_TIME_FOR_LOCATION_UPDATES, MIN_DISTANCE_FOR_LOCATION_UPDATES, LekuakFragment.this);
			ViewTreeObserver vto = mView.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					if(!mHashBeenCalled) {
						setupMap();
						mHashBeenCalled = true;
					}
				}
			});
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
		mMap.clear();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		mMap.setMyLocationEnabled(false);
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
//			mLocationManager.requestSingleUpdate(provider, LekuakFragment.this, null);
			if(mUserLocation != null) {
				mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
				loadProfile();
				loadExplore();
			}
		} else {
			mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
			loadProfile();
			loadExplore();
		}

		if (mCoords != null) {
			mCameraPosition = updateCamera(mCoords);
			if(mCameraPosition != null) {
				mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
				//centerMap();
			}
		}
	}

	private void centerMap(){
		final ViewTreeObserver vto = mMapFragment.getView().getViewTreeObserver();
		if (vto.isAlive()) {
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressLint("NewApi")
				@Override
				public void onGlobalLayout() {

					Point point = mMap.getProjection().toScreenLocation(mCoords);
					point.offset(0, point.y - FIRST_SPECIAL_ROW_MARGIN);
					LatLng center = mMap.getProjection().fromScreenLocation(point); // + (point.y - FIRST_SPECIAL_ROW_MARGIN)));

					if (vto.isAlive()) {
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
							vto.removeGlobalOnLayoutListener(this);
						} else {
							vto.removeOnGlobalLayoutListener(this);
						}
					}
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(updateCamera(center)));
					mSpecialCoords = center;
				}
			});
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

	public CameraPosition udateCameraWithZoom(LatLng coords, Integer zoom) {
		if(coords != null) {
			return new CameraPosition.Builder()
			.target(coords)
			.zoom(zoom)
			.bearing(0)
			.tilt(0)
			.build();			
		} else {
			return null;
		}
	}

	@Override
	public void onMyLocationChange(Location location) {
		mUserLocation = location;
	}

	public GoogleMap getMap() {
		return mMap;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(mList.getVisibility() == View.VISIBLE && position == 0) {
			Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_out);
			mList.startAnimation(anim);
			mLoadingLayout.startAnimation(anim);
			mList.setVisibility(View.GONE);
			mLoadingLayout.setVisibility(View.GONE);
			mLayoutItxi.setVisibility(View.VISIBLE);
			mLayoutLocate.setVisibility(View.VISIBLE);
			mCameraPosition = udateCameraWithZoom(mCoords, IN_ZOOM);
			if(mCameraPosition != null) {
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
			}
		} else {
			Place place = (Place) parent.getItemAtPosition(position);
			Intent intentToPlace = new Intent(mContext, PlaceActivity.class);
			intentToPlace.putExtra("place", place);
			startActivity(intentToPlace);
			getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
		}
	}

	@Override
	public void onClick(View view) {
		mCameraPosition = updateCamera(mCoords);
		if(view.getId() == mItxi.getId()) {
			if(mList.getVisibility() == View.GONE) {
				Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_in);
				mList.startAnimation(anim);
				mLoadingLayout.startAnimation(anim);
				mList.setVisibility(View.VISIBLE);
				mLoadingLayout.setVisibility(View.VISIBLE);
				mLayoutItxi.setVisibility(View.GONE);
				mLayoutLocate.setVisibility(View.GONE);
				if(mCameraPosition != null) {
					mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
					centerMap();
				}
			} 
		} else if(view.getId() == mLocate.getId()) {
			if(mCameraPosition != null) {
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
			}
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//		if (scrollState == SCROLL_STATE_IDLE) {
		//			if (mList.getLastVisiblePosition() >= mList.getCount() - 4) {
		//				getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
		//				PAGE++;
		//				loadExplore();
		//			}
		//		}
	}

	public void loadProfile() {
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
								if (getActivity() == null) {
									return;
								}
								getActivity().runOnUiThread(new Runnable() {
									public void run() {
										if (mUserAvatar != null && mMap != null) {
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
						}).start();
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

	public void loadExplore() {
		RequestParams paramsExplore = new RequestParams();
		paramsExplore.put("token", MintzatuAPI.getToken(mContext));
		paramsExplore.put("id", MintzatuAPI.getUserid(mContext).toString());
		paramsExplore.put("distance", DISTANCE.toString());
		paramsExplore.put("lat", Double.valueOf(mUserLocation.getLatitude()).toString());
		paramsExplore.put("lng", Double.valueOf(mUserLocation.getLongitude()).toString());

		MintzatuAPI.post(MintzatuAPI.EXPLORE, paramsExplore, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0 && !response.isNull("places")) {
						JSONArray jsonPlaces = response.getJSONArray("places");
						if(jsonPlaces.length() > 0) {
							mPlaces = new ArrayList<Place>();
							for (int i = 0; i < jsonPlaces.length(); i++) {
								Place place = new Place(jsonPlaces.getJSONObject(i));
								mPlaces.add(place);
							}
							if (PAGE > 1) {
								if (mAdapter == null) {
									mAdapter = (MapListAdapter) mList.getAdapter();
								}
								for(Place place : mPlaces) {
									mAdapter.addItem(place);
								}
								mAdapter.notifyDataSetChanged();
							} else {
								mAdapter = new MapListAdapter(mContext, mPlaces);
								mList.setAdapter(mAdapter);
								mList.setOnItemClickListener(LekuakFragment.this);
								mList.setOnScrollListener(LekuakFragment.this);
							}
							for(final Place place : mPlaces) {
								new Thread(new Runnable() {
									public void run() {
										final LatLng mPlaceCoords = new LatLng(place.lat, place.lng);
										final Bitmap mPlaceAvatar = ImageHelper.getBitmapFromURL(place.kategoriak.get(0).imgUrl);
										if (getActivity() == null) {
											return;
										}
										if (getActivity() != null && !getActivity().isFinishing()) {
											getActivity().runOnUiThread(new Runnable() {
												public void run() {
													if (mPlaceAvatar != null && mMap != null) {
														Marker marker = mMap.addMarker(
																new MarkerOptions()
																.position(mPlaceCoords)
																.title(place.izena)
																.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(mPlaceAvatar, 80, 80, false)))
																.snippet(place.id_lekua + "-" + place.helbidea)
																);
													}
												}
											});
										}
									}
								}).start();
								mMap.setOnInfoWindowClickListener(LekuakFragment.this);
							}
							//mLoadingLayout.setVisibility(View.GONE);
							mProgressBar.setVisibility(View.GONE);
							mList.setVisibility(View.VISIBLE);
						}
					} else if (code == 0 && response.isNull("places")) {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_places_not_found), Toast.LENGTH_LONG).show();	
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						Intent intent = new Intent(mContext, LoginCircles.class);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					}
					if (getSherlockActivity() != null) {
						getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
					}
					if (mIsFirstLoad) {
						mIsFirstLoad = false;
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
			startActivity(intent);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.lekuak, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_websearch:
			Intent intent = new Intent(mContext, SearchActivity.class);
			intent.putExtra("action", ACTION_SEARCH_PLACES);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public void onLocationChanged(Location location) {
    	mUserLocation = location;
    	if(mUserLocation != null) {
    		mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
    		loadProfile();
    		loadExplore();
    	}
    	if (mCoords != null) {
    		mCameraPosition = updateCamera(mCoords);
    		if(mCameraPosition != null && mMap != null) {
    			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
    			centerMap();
    		}
    	}
    	mLocationClient.removeLocationUpdates(this);
    }

//	@Override
//	public void onProviderDisabled(String provider) {}
//
//	@Override
//	public void onProviderEnabled(String provider) {}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {}

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
