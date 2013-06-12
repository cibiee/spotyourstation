package com.Ciby.spotyourstation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class list_stations extends Activity {
	
	private static final String TAG_NOTIFICATION = null;
	private EditText et;
	private ListView list;
	private SimpleAdapter mSchedule;
	private Button info_btn;
	private ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_stations);
		addListenerOnButton();

		 String json = null;
	        try {

	            JSONArray array = getStationList();
	      
	            parseStations(array);
	          
	            list = (ListView) findViewById(R.id.tablelayout);
	            
	             mSchedule = new SimpleAdapter(this, mylist, R.layout.component,
	                    new String[] {"ID", "Name", "Code"}, new int[] {R.id.TRAIN_CELL, R.id.FROM_CELL, R.id.TO_CELL});
	             list.setAdapter(mSchedule);
	                 
	             bindOnClickItem();

	        } catch (IOException ex) {
	            ex.printStackTrace();
	          
	        } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	       
	        et = (EditText) findViewById(R.id.EditText01);
	        
	       // list.setTextFilterEnabled(true);
	        et.addTextChangedListener(new TextWatcher()
	        {
	        	
	        public void afterTextChanged(Editable s)
	        {
	                                                                        // Abstract Method of TextWatcher Interface.
	        }
	        public void beforeTextChanged(CharSequence s,
	        int start, int count, int after)
	        {
	        // Abstract Method of TextWatcher Interface.
	        }
	        public void onTextChanged(CharSequence s,
	        int start, int before, int count)
	        {
	        	
	        	((SimpleAdapter) list_stations.this.mSchedule).getFilter().filter(s);
	        }
	        });
	     
    }

	private void bindOnClickItem() {
		list.setOnItemClickListener(new OnItemClickListener()
		 {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				final Intent i=new Intent(list_stations.this,stationdetails.class);
				
				@SuppressWarnings("unchecked")
				HashMap<String, String> selectedmap= (HashMap<String, String>) mSchedule.getItem(arg2);
				  String destination = selectedmap.get("Name");
				  String longitude= selectedmap.get("Longitude");
				  String latitude= selectedmap.get("Latitude");
				
				  i.putExtra("destination", destination);
		          i.putExtra("longitude", longitude);
		          i.putExtra("latitude", latitude);
		          
			   startActivity(i);
			   et.setText("");
			   
			}
		});
	}

	private void parseStations(JSONArray array) throws JSONException {
		// looping through All Contacts
		for(int i = 0; i < array.length(); i++){
		    JSONObject c = array.getJSONObject(i);
		    // Storing each json item in variable
		    String id = c.getString("ID");
		    String name = c.getString("Name");
		    String code = c.getString("Code");
		    String Longitude = c.getString("Longitude");
		    String Latitude = c.getString("Latitude");
		    HashMap<String, String> map = new HashMap<String, String>();
		    map.put("ID", id);
		    map.put("Name", name);
		    map.put("Code", code);
		    map.put("Longitude", Longitude);
		    map.put("Latitude", Latitude);
		    mylist.add(map);
		}
	}

	private JSONArray getStationList() throws IOException,
			UnsupportedEncodingException, JSONException {
		String json;
		InputStream is =    getAssets().open("stations/Stations.json");
		int size = is.available();
		byte[] buffer = new byte[size];
		is.read(buffer);
		is.close();
		json = new String(buffer, "UTF-8");
		JSONObject jObject = new JSONObject(json); 
		JSONArray array = jObject.getJSONArray("stations");
		return array;
	}

	 public void addListenerOnButton() {
			info_btn= (Button) findViewById(R.id.btnEditt);
			info_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final Intent i=new Intent(list_stations.this,info_activity.class);
				     startActivity(i);
				}
			});
		}
}



