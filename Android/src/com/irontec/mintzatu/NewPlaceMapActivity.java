package com.irontec.mintzatu;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.irontec.helpers.LocationHelper;

public class NewPlaceMapActivity extends SherlockFragmentActivity implements OnClickListener,
GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener ,
LocationListener{

	private GoogleMap mMap;
	private Location mUserLocation;
	private LatLng mCoords;
	private Button mItxi;
	private LocationManager mLocationManager;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_place_map);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

//		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(
				LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		mLocationClient = new LocationClient(this, this, this);
		mUpdatesRequested = true;

//		if(mUserLocation == null) {
//			//			LocationHelper locationHelper = new LocationHelper(this);
//			//			mUserLocation = locationHelper.getBestLocation();
//			Criteria criteria = new Criteria();
//			String provider = mLocationManager.getBestProvider(criteria, false);
//			mUserLocation = mLocationManager.getLastKnownLocation(provider);
//			if(mUserLocation != null) {
//				mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
//			}
//		}

		mItxi = (Button)findViewById(R.id.itxi);
		mItxi.setOnClickListener(this);

	}
	
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();
		mLocationClient.connect();
	}
	
	@Override
	public void onStop() {
		if (mLocationClient.isConnected()) {
            mLocationClient.removeLocationUpdates(this);
        }
        mLocationClient.disconnect();
        super.onStop();
	}

	public void setupMap() {
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
				.getMap();

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

		Marker place = mMap.addMarker(new MarkerOptions()
		.position(mCoords)
		.title("Aukeratu lekua")
		.snippet("Zapaldu eta mugitu")
		.icon(BitmapDescriptorFactory.defaultMarker())
		.draggable(true));

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCoords, 18));

		mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

		mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker marker) {}
			@Override
			public void onMarkerDragEnd(Marker marker) {
				mCoords = marker.getPosition();
			}
			@Override
			public void onMarkerDrag(Marker marker) {}
		});
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == mItxi.getId()) {
			finishWithResult();
		}
	}

	private void finishWithResult() {
		Bundle conData = new Bundle();
		conData.putParcelable("map_location", mCoords);
		Intent intent = new Intent();
		intent.putExtras(conData);
		setResult(RESULT_OK, intent);
		finish();
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
		getSupportMenuInflater().inflate(R.menu.new_place_map, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		mUserLocation = location;
		if(mUserLocation != null) {
			mCoords = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
			setupMap();
			mLocationClient.removeLocationUpdates(this);
		}
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
