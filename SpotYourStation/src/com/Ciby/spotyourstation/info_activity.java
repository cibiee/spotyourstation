package com.Ciby.spotyourstation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class info_activity extends Activity {
	Button info_btn;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    
		setContentView(R.layout.info);
		addListenerOnButton();
	}
	
	public void addListenerOnButton() {
		 
		
		 
		
		
		info_btn= (Button) findViewById(R.id.btnEditt);
		
		
		info_btn.setOnClickListener(new OnClickListener() {
 
			

			@Override
			public void onClick(View v) {
				final Intent i=new Intent(info_activity.this,feedback.class);
			     startActivity(i);
			   
				
			}
 
		});
// 
	}


}
