package com.samsung.apps.restaurantscan;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.samsung.apps.restaurantscan.model.ListHelper;
import com.samsung.apps.restaurantscan.model.Overview;

/**
 * @author Avdhoot Saple
 * 
 * Created on Apr 25, 2015
 * 
 * This is the main activity class implementation for Restaurant Scanner.
 * 
 *  */
public class MainActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener {

	private ListView list;
	private List<Overview> restaurantCollection;
	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	private String latitude;
	private String longitude;
	private String range;
	private LocationRequest mLocationRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_main);
		Intent i = getIntent();
		String rangeFromItent = null;
		if (i != null) {
			rangeFromItent = i.getStringExtra("range");
		}
		if (rangeFromItent != null) {
			setRange(rangeFromItent);
		} else {
			setRange("500");
		}

		// get location
		buildGoogleApiClient();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void settingsActivity() {
		Log.i(RestaurantConstants.INFO, "Settings Selected");
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			// trigger setting activity for user to reset range
			settingsActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected synchronized void buildGoogleApiClient() {
		setmGoogleApiClient(new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build());
		createLocationRequest();

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(RestaurantConstants.INFO,
				"Connection failed: ConnectionResult.getErrorCode() = "
						+ result.getErrorCode());

	}

	@Override
	public void onConnected(Bundle arg0) {
		setmLastLocation(LocationServices.FusedLocationApi
				.getLastLocation(getmGoogleApiClient()));
		startLocationUpdates();
		if (getmLastLocation() != null) {
			setLatitude(String.valueOf(getmLastLocation().getLatitude()));
			setLongitude(String.valueOf(getmLastLocation().getLongitude()));
			Log.i(RestaurantConstants.INFO,
					"succesfully retrieved  coordinates");

		} else {
			Log.i(RestaurantConstants.INFO, "Failed to  retrieve  coordinates");
			Toast.makeText(this, R.string.no_location_detected,
					Toast.LENGTH_LONG).show();
			setLatitude("40.7463956");
			setLongitude("-73.9852992");
		}
		updateBindingList();
	}

	private void updateBindingList() {
		this.restaurantCollection = ListHelper.getRestaurantList(latitude,
				longitude, range);
		BinderData bindingData = new BinderData(this, restaurantCollection);
		list = (ListView) findViewById(R.id.list);
		Log.i(RestaurantConstants.INFO, "Before SetAdapter");
		list.setAdapter(bindingData);
		Log.i(RestaurantConstants.INFO, "After SetAdapter");
		// Click event for single list row
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent detailIntent = new Intent();
				detailIntent.setClass(MainActivity.this, DetailsActivity.class);
				// parameters to set for intent
				detailIntent.putExtra(RestaurantConstants.POSITION,
						String.valueOf(position + 1));
				detailIntent.putExtra(RestaurantConstants.PLACEEID,
						restaurantCollection.get(position).getPlaceId());
				detailIntent.putExtra(RestaurantConstants.NAME,
						restaurantCollection.get(position).getName());
				// start the detail activity
				startActivity(detailIntent);
			}
		});
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.i(RestaurantConstants.INFO, "Connection suspended");
		mGoogleApiClient.connect();

	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	protected void createLocationRequest() {

		LocationRequest mLocRequest = new LocationRequest();
		mLocRequest.setInterval(10000);
		mLocRequest.setFastestInterval(5000);
		mLocRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		setmLocationRequest(mLocRequest);

	}
	
	protected void startLocationUpdates() {

	    LocationServices.FusedLocationApi.requestLocationUpdates(
	            mGoogleApiClient, getmLocationRequest(), this);
	}

	/**
	 * Listener for location changed
	 * 
	 * */
	@Override
	public void onLocationChanged(Location location) {
		setmLastLocation(location);
		if (getmLastLocation() != null) {
			setLatitude(String.valueOf(getmLastLocation().getLatitude()));
			setLongitude(String.valueOf(getmLastLocation().getLongitude()));
		
			Log.i(RestaurantConstants.INFO, "retrieved coordinates successfully");

			updateBindingList();
		}

	}

	public ListView getList() {
		return list;
	}

	public void setList(ListView list) {
		this.list = list;
	}

	public List<Overview> getRestaurantCollection() {
		return restaurantCollection;
	}

	public void setRestaurantCollection(List<Overview> restaurantCollection) {
		this.restaurantCollection = restaurantCollection;
	}

	public GoogleApiClient getmGoogleApiClient() {
		return mGoogleApiClient;
	}

	public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
		this.mGoogleApiClient = mGoogleApiClient;
	}

	public Location getmLastLocation() {
		return mLastLocation;
	}

	public void setmLastLocation(Location mLastLocation) {
		this.mLastLocation = mLastLocation;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public LocationRequest getmLocationRequest() {
		return mLocationRequest;
	}

	public void setmLocationRequest(LocationRequest mLocationRequest) {
		this.mLocationRequest = mLocationRequest;
	}
}
