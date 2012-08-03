package com.example.danshelloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MyFirstActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.myapp.MESSAGE";
	public static long time_msg_sent = 0;
	public static long time_msg_returned = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // see res/layout/activity_my_first.xml
        setContentView(R.layout.activity_my_first);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_my_first, menu);
        return true;
    }
    
    @Override
    public void onResume()
    {
        System.out.println("onResume");
        
        // if we've already sent a message, 
        // we must be receiving a message now
        if ( time_msg_sent != 0 )
        {
        	time_msg_returned = System.currentTimeMillis();
        	
        	long time_diff_s = (time_msg_returned - time_msg_sent) / 1000;
        	

        	String message = "You took "+time_diff_s+" seconds to return to this page";
        	System.out.println(message);
        	
        	time_msg_sent = 0;
        	
        	// Get the input text to send to the intent
        	TextView textView = (TextView) findViewById(R.id.time_info_text_view);
            textView.setText(message);
        }
        super.onResume();
    }
    
    /** Called when the user selects the Send button */
    public void sendMessage(View view) {
    	
    	// initial time sent
    	time_msg_sent = System.currentTimeMillis();
    	System.out.println("Sending message at " + time_msg_sent);
    	
    	// Intent is a class that provides runtime binding between 
    	// separate components.
    	// Press CTRL+SHIFT+O to import classes automatically.
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	
    	// Get the input text to send to the intent
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	
    	// Add the message as a key-value pair
    	intent.putExtra(EXTRA_MESSAGE, message);
    	
    	// Start an activity, and pass in the intent
    	startActivity(intent);
    }
    
}
