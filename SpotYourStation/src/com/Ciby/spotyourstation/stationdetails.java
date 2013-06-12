package com.Ciby.spotyourstation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.Ciby.spotyourstation.facebook.AsyncFacebookRunner;
import com.Ciby.spotyourstation.facebook.AsyncFacebookRunner.RequestListener;
import com.Ciby.spotyourstation.facebook.DialogError;
import com.Ciby.spotyourstation.facebook.Facebook;
import com.Ciby.spotyourstation.facebook.Facebook.DialogListener;
import com.Ciby.spotyourstation.facebook.FacebookError;
import com.Ciby.spotyourstation.twitter.TwitterUtils;

public class stationdetails extends Activity implements DialogListener,
OnClickListener {

	TextView destinationTextView;
	TextView myLocation;
	String destination;
	String longitude;
	String latitude;
	Button info_btn;
    Button setDestinationButton;
    private Sensor sensor;
    private static SensorManager sensorService;
    private ImageView imgTrack;
	private ImageView btnFB;
	private ImageView btnTwitter;
	private Facebook facebook;

	private SharedPreferences shareddPrefs;
	private final Handler mTwitterHandler = new Handler();

	final Runnable mUpdateTwitterNotification = new Runnable() {
		public void run() {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stationdetails);
		this.shareddPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		addListenerOnButton();

		

		init();

		Intent i = getIntent();
		Bundle extras = i.getExtras();
		destination = extras.getString("destination");
		latitude = extras.getString("latitude");
		longitude = extras.getString("longitude");
		destinationTextView.setText(destination);

		initLocationService();
		sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	    if (sensor != null) {
	      sensorService.registerListener(directionSensorEventListener, sensor,
	          SensorManager.SENSOR_DELAY_NORMAL);

	    } 
		initializefacebook();
		svc = new Intent(this, LocationService.class);
		

	}
	
	private Intent svc;
	@Override
	protected void onResume() {
	    super.onResume();
	     
		stopService(svc);
	    // Normal case behavior follows
	}

	
	boolean gps_enabled=false;
    boolean network_enabled=false;
    
	private void initLocationService() {
		LocationManager locationManager = null;
		if (locationManager == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			
			try{gps_enabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}

			try{network_enabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

	        //don't start listeners if no provider is enabled
	        if(!gps_enabled && !network_enabled){
				Toast.makeText(stationdetails.this, R.string.setttingsopen,
						Toast.LENGTH_LONG).show();
				Intent myIntent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				stationdetails.this.startActivity(myIntent);
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

			} 
			
			else {
				Toast.makeText(stationdetails.this, R.string.setttingsopen,
						Toast.LENGTH_LONG).show();
				Intent myIntent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				stationdetails.this.startActivity(myIntent);
				return;
			}

			if (locationManager.isProviderEnabled(providerName)) {
			if(gps_enabled)
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener);
	      
			if(network_enabled)
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNet);
			}
		}
	}

	private void initializefacebook() {
		btnFB.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					facebook = new Facebook("443385779084591");

					// facebook.authorize(SignIntermediateActivity.this, new
					// String[]
					// {"user_photos,photo_upload,publish_checkins,publish_actions,publish_stream,read_stream,offline_access"},Facebook.FORCE_DIALOG_AUTH,SignIntermediateActivity.this);
					facebook.authorize(
							stationdetails.this,
							new String[] { "user_photos,photo_upload,publish_checkins,publish_actions,publish_stream,read_stream,offline_access" },
							Facebook.FORCE_DIALOG_AUTH, stationdetails.this);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void init() {
		destinationTextView = (TextView) findViewById(R.id.destinationTextView);
		btnTwitter = (ImageView) findViewById(R.id.imgTw);
		btnTwitter.setOnClickListener(this);
		myLocation = (TextView) findViewById(R.id.distanceTextView);
		myLocation.setText("Please wait......");
		btnFB = (ImageView) findViewById(R.id.imgFb);
		setDestinationButton = (Button) findViewById(R.id.btndestination);
		setDestinationButton.setOnClickListener(this);
		imgTrack = (ImageView) findViewById(R.id.trackView);
		drawab = R.drawable.warning_green;
		imgTrack.setImageDrawable(getResources().getDrawable(R.drawable.warning_green));
	}

	private String getTweetMsg() {
		// VOUser obUser = new VOUser(this);
		return "Used Spot Your Station to find the distance from his current location to "
		+ this.destination
		+ " railway station. Find in google play store @ http://tinyurl.com/d6gxxfd";
	}

	public void sendTweet() {
		Thread t = new Thread() {
			public void run() {

				try {
					TwitterUtils.sendTweet(shareddPrefs, getTweetMsg());
					mTwitterHandler.post(mUpdateTwitterNotification);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		};
		t.start();
	}

	private double distanceBetween = 0.0;
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};
	  private Location locD = null;
	  private Location locC = null;
	  private int drawab = 0;
	 private SensorEventListener directionSensorEventListener = new SensorEventListener() {

		    public void onSensorChanged(SensorEvent event) {
		      
		      
		      if ( locC == null ) return;

		      float azimuth = event.values[0];
		      float baseAzimuth = azimuth;

		      GeomagneticField geoField = new GeomagneticField( Double
		          .valueOf( locC.getLatitude() ).floatValue(), Double
		          .valueOf( locC.getLongitude() ).floatValue(),
		          Double.valueOf( locC.getAltitude() ).floatValue(),
		          System.currentTimeMillis() );

		       
		        azimuth -= geoField.getDeclination(); // converts magnetic north into true north

		      // Store the bearingTo in the bearTo variable
		      float bearTo = locC.bearingTo(locD);

		      // If the bearTo is smaller than 0, add 360 to get the rotation clockwise.
		      if (bearTo < 0) {
		          bearTo = bearTo + 360;
		      }

		      //This is where we choose to point it
		      float directions = bearTo - azimuth;

		      // If the direction is smaller than 0, add 360 to get the rotation clockwise.
		      if (directions < 0) {
		          directions = directions + 360;
		      }
		      rotateImageView( imgTrack, drawab, directions );
		      //edDistance.setText(directions+"");
		    }

		    public void onAccuracyChanged(Sensor sensor, int accuracy) {
		      // TODO Auto-generated method stub

		    }
		  };

		  private void rotateImageView( ImageView imageView, int drawable, float rotate ) {

		    // Decode the drawable into a bitmap
		    if(drawab != 0)
		    {
		    Bitmap bitmapOrg = BitmapFactory.decodeResource( getResources(),
		            drawable );

		    // Get the width/height of the drawable
		    DisplayMetrics dm = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(dm);
		    int width = bitmapOrg.getWidth(), height = bitmapOrg.getHeight();

		    // Initialize a new Matrix
		    Matrix matrix = new Matrix();

		    // Decide on how much to rotate
		    rotate = rotate % 360;

		    // Actually rotate the image
		    matrix.postRotate( rotate, width, height );

		    // recreate the new Bitmap via a couple conditions
		    Bitmap rotatedBitmap = Bitmap.createBitmap( bitmapOrg, 0, 0, width, height, matrix, true );
		    //BitmapDrawable bmd = new BitmapDrawable( rotatedBitmap );

		    //imageView.setImageBitmap( rotatedBitmap );
		    imageView.setImageDrawable(new BitmapDrawable(getResources(), rotatedBitmap));
		    imageView.setScaleType( ScaleType.CENTER );
		    }
//		    RotateAnimation r = new RotateAnimation(fromD, rotate, imageView.getWidth()/2, imageView.getHeight()/2);
//		    r.setDuration(1000);
//		    imageView.setAnimation(r);
//		    fromD = rotate;
		}
	
	private LocationListener locationListenerNet = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};

	private void updateWithNewLocation(Location location) {
		

		if (location != null) {
			Location destinationLocation = new Location("");
			destinationLocation.setLatitude(Double.parseDouble(this.latitude));
			destinationLocation
			.setLongitude(Double.parseDouble(this.longitude));
			locD=destinationLocation;
			Globals.myVariable = null;
			Globals.myVariable = destinationLocation;
			 locC = location;
			distanceBetween = destinationLocation.distanceTo(location);
			distanceBetween = distanceBetween / 1000;

			myLocation.setText(String.format("%.2f", distanceBetween)
					+ " kms to go. ");

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	public void onComplete(Bundle values) {
		if (values.isEmpty()) {
			// "skip" clicked ?
			return;
		}
		if (!values.containsKey("post_id")) {
			try {
				valuesB = values;
				new postFacebook().execute();

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void onFacebookError(FacebookError e) {
		System.out.println("Error: " + e.getMessage());
	}

	public void onError(DialogError e) {
		System.out.println("Error: " + e.getMessage());
	}

	public void onCancel() {
		
	}

	public void updateStatus(String accessToken) {
		try {
			Bundle bundle = new Bundle();
			bundle.putString(Facebook.TOKEN, accessToken);

			bundle.putString(
					"message",
					"Used Spot Your Station to find the distance from his current location to "
							+ this.destination
							+ " railway station. Find in google play store @ http://tinyurl.com/d6gxxfd");
			
			String response = facebook.request("/me/feed", bundle, "POST");
			AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
			mAsyncRunner.request(null, bundle, "POST", new RequestListener() {
				public void onMalformedURLException(MalformedURLException e,
						Object state) {
					e.printStackTrace();
				}

				public void onIOException(IOException e, Object state) {
					e.printStackTrace();
				}

				public void onFileNotFoundException(FileNotFoundException e,
						Object state) {
					e.printStackTrace();
				}

				public void onFacebookError(FacebookError e, Object state) {
					e.printStackTrace();
				}

				public void onComplete(String response, Object state) {
				}

			}, null);
		} catch (Exception e) {
			String st = e.toString();
			st = st + "";
		}
	}

	private Bundle valuesB;

	class postFacebook extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			updateStatus(valuesB.getString(Facebook.TOKEN));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Toast.makeText(getApplicationContext(), "posted",
			// Toast.LENGTH_SHORT).show();
		}

	}
	
	

	public void addListenerOnButton() {

		info_btn = (Button) findViewById(R.id.btnEditt);
		info_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Intent i = new Intent(stationdetails.this,
						info_activity.class);
				startActivity(i);

			}

		});
		//
		
		
	
	}

	@Override
	public void onClick(View v) {
         
		
		startService(svc);
		
		
		// btnTwitter = (ImageView) findViewById(R.id.imgFb);
				// btnTwitter.setOnClickListener(new OnClickListener() {
				//
				// public void onClick(View v) {
				//
				// startService(new Intent(this, LocationService.class));
				// try
				// {
				//
				// new AsyncTask<SharedPreferences,Object, Boolean>() {
				//
				// @Override
				// protected Boolean doInBackground(SharedPreferences... params) {
				// return TwitterUtils.isAuthenticated(params[0]);
				// }
				//
				// @Override
				// protected void onPostExecute(Boolean isAuthenticated) {
				// if (isAuthenticated) {
				// // Do processing after successful authentication
				// sendTweet();
				// }
				// else {
				// // Do processing after authentication failure
				// Intent i = new Intent(getApplicationContext(),
				// PrepareRequestTokenActivity.class);
				// i.putExtra("tweet_msg",getTweetMsg());
				// startActivity(i);
				// }
				// }
				// }.execute(shareddPrefs);
				//
				// }
				// catch (Exception e) {
				// // 
				// String st = e.toString();
				// }
				//
				//
				// }
				// });

				// Criteria crta = new Criteria();
				// crta.setAccuracy(Criteria.ACCURACY_FINE);
				// crta.setAltitudeRequired(false);
				// crta.setBearingRequired(false);
				// crta.setCostAllowed(true);
				// crta.setPowerRequirement(Criteria.POWER_LOW);
				// String provider = locationManager.getBestProvider(crta, true);

				// String provider = LocationManager.GPS_PROVIDER;
				// Location location = locationManager.getLastKnownLocation("");
				// updateWithNewLocation(location);
				//
				// locationManager.requestLocationUpdates("", 1000, 0,
				// locationListener);
	}

	public static class Globals {
		public static Location myVariable;
	}

}
