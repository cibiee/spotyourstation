package com.Ciby.spotyourstation;

import com.Ciby.spotyourstation.stationdetails.Globals;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
	public static final String BROADCAST_ACTION = "Hello World";
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	public LocationManager locationManager;
	public MyLocationListener listener;
	public MyLocationListener listener2;
	public Location previousBestLocation = null;

	Intent intent;
	int counter = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		
		this.initLocationService();
	}
	boolean gps_enabled=false;
    boolean network_enabled=false;
    
	private void initLocationService() {
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		LocationManager locationManager = null;
		if (locationManager == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			
			try{gps_enabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}

			try{network_enabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

	        //don't start listeners if no provider is enabled
	        if(!gps_enabled && !network_enabled){
				Toast.makeText(LocationService.this, R.string.setttingsopen,
						Toast.LENGTH_LONG).show();
				Intent myIntent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				LocationService.this.startActivity(myIntent);
				return;
			}
	        
			
			Criteria locationCritera = new Criteria();
			locationCritera.setAccuracy(Criteria.ACCURACY_COARSE);
			locationCritera.setAltitudeRequired(false);
			locationCritera.setBearingRequired(false);
			locationCritera.setCostAllowed(true);
			locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

			String providerName = locationManager.getBestProvider(
					locationCritera, true);

			String provider = Settings.Secure.getString(getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (!provider.equals("")) {
				// GPS Enabled

			} else {
				Toast.makeText(LocationService.this, R.string.setttingsopen,
						Toast.LENGTH_LONG).show();
				Intent myIntent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				LocationService.this.startActivity(myIntent);
				return;
			}

			if (locationManager.isProviderEnabled(providerName)) {
			
			if(gps_enabled)
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,listener);
	      
			if(network_enabled)
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener2);
			}
		}
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	@Override
	public void onDestroy() {
		// handler.removeCallbacks(sendUpdatesToUI);
		super.onDestroy();
		Log.v("STOP_SERVICE", "DONE");
		locationManager.removeUpdates(listener);
	}

	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {

				}
			}
		};
		t.start();
		return t;
	}

	private double distanceBetween = 0.0;
    private int reqcounter=0;
    private MediaPlayer m = new MediaPlayer();
	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(final Location loc) {
			
			reqcounter+=1;
			Log.i("**************************************", "Location changed");
			if (isBetterLocation(loc, previousBestLocation)) {
				loc.getLatitude();
				loc.getLongitude();
				intent.putExtra("Latitude", loc.getLatitude());
				intent.putExtra("Longitude", loc.getLongitude());
				intent.putExtra("Provider", loc.getProvider());
                
				distanceBetween = Globals.myVariable.distanceTo(loc);
			distanceBetween = distanceBetween / 1000;

				if(distanceBetween<20){
				try {

					if (m.isPlaying()) {
						m.stop();
						m.release();
					}
					
					m = new MediaPlayer();
					AssetFileDescriptor descriptor = getAssets().openFd(
							"beep5Second.mp3");
					m.setDataSource(descriptor.getFileDescriptor(),
							descriptor.getStartOffset(), descriptor.getLength());

					m.prepare();
					
					m.start();
					
				} catch (Exception e) {
				}

				
				}
				
				Toast.makeText(
						getApplicationContext(),
						String.format("%.1f", distanceBetween) + " kms to go. ",
						Toast.LENGTH_LONG).show();
				sendBroadcast(intent);
			}

		}

		public void onProviderDisabled(String provider) {
			 Toast.makeText( getApplicationContext(), "Gps Disabled",
			 Toast.LENGTH_SHORT ).show();
		}

		public void onProviderEnabled(String provider) {
			// Toast.makeText( getApplicationContext(), "Gps Enabled",
			// Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}
}
