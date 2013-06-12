package com.Ciby.spotyourstation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent; 
public class HomeActivity extends Activity {

	ImageView button;
	Button info_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		
		addListenerOnButton();
		
		 String provider = Settings.Secure.getString(getContentResolver(),
       	      Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
       	       if(!provider.equals("")){
       	           //GPS Enabled
       	       
       	       }else{
       	    	   Toast.makeText(HomeActivity.this,R.string.setttingsopen, Toast.LENGTH_LONG).show();
//       	           Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//       	           HomeActivity.this.startActivity(myIntent);
//       	            return;
       	       }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	public void addListenerOnButton() {
		 
		
		 
		button = (ImageView) findViewById(R.id.spotyourstation_imageButton);
		
		info_btn= (Button) findViewById(R.id.btnEditt);
		button.setOnClickListener(new OnClickListener() {
 
			

			@Override
			public void onClick(View v) {
				final Intent i=new Intent(HomeActivity.this,list_stations.class);
			     startActivity(i);
			   
				
			}
 
		});
		
		
		info_btn.setOnClickListener(new OnClickListener() {
 
			

			@Override
			public void onClick(View v) {
				final Intent i=new Intent(HomeActivity.this,info_activity.class);
			     startActivity(i);
			   
				
			}
 
		});
// 
	}

}

