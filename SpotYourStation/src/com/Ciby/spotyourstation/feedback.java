package com.Ciby.spotyourstation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class feedback extends Activity {
	 Button info_btn;
	 TextView feedBack;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_feedback);
		
	    addListenerOnButton();
	    // TODO Auto-generated method stub
	}
	
	
	 public void addListenerOnButton() {
		 
			info_btn= (Button) findViewById(R.id.btnsubmit);
			feedBack=(TextView)findViewById(R.id.txtfeedcomment);
			
			info_btn.setOnClickListener(new OnClickListener() {
	 
				

				@Override
				public void onClick(View v) {
					Intent email = new Intent(Intent.ACTION_SEND);
					email.putExtra(Intent.EXTRA_EMAIL, new String[]{"sukeshdas@gmail.com","ciby.kj@gmail.com"});		  
					email.putExtra(Intent.EXTRA_SUBJECT, "Feed Back");
					email.putExtra(Intent.EXTRA_TEXT, feedBack.getText().toString());
					email.setType("message/rfc822");
					
					try {
						startActivity(Intent.createChooser(email, "Choose an Email client :"));
					} catch (android.content.ActivityNotFoundException ex) {
					    Toast.makeText(feedback.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
					}
					
				   
					
				}
	 
			});
	
		}
}
