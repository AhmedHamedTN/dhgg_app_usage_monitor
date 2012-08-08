package com.example.danshelloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MyFirstActivity extends Activity {
	
	// adding comment to see difference
	public final static String MESSAGE_KEY = "com.example.myapp.MESSAGE";
	public static long time_msg_sent = 0;
	public static long time_msg_returned = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // see res/layout/activity_my_first.xml
        setContentView(R.layout.activity_my_first);

        logMsg("onCreate.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_my_first, menu);
        return true;
    }
    
    @Override
    public void onResume()
    {
        logMsg("onResume.");
        System.out.println("onResume");
        
        // if we've already sent a message, 
        // we must be receiving a message now
        if ( time_msg_sent != 0 )
        {
        	time_msg_returned = System.currentTimeMillis();
        	
        	long time_diff_s = (time_msg_returned - time_msg_sent) / 1000;
        	logMsg("onResume: "+time_diff_s+"s since msg sent.");
        	
            // reset
            time_msg_sent = 0;        	
        }
        super.onResume();
    }
    
    /** Called when the user selects the Send button */
    public void sendMessage(View view) {
    	
    	// initial time sent
    	time_msg_sent = System.currentTimeMillis();
    	logMsg("Sending message at " + time_msg_sent);
    	
    	// Intent is a class that provides runtime binding between 
    	// separate components.
    	// Press CTRL+SHIFT+O to import classes automatically.
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	
    	// Get the input text to send to the intent
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	
    	// Add the message as a key-value pair
    	intent.putExtra(MESSAGE_KEY, message);
    	
    	// Start an activity, and pass in the intent
    	startActivity(intent);
    }
    
    public void logMsg(String message) {
    	// Get the input text to send to the intent
    	TextView textView = (TextView) findViewById(R.id.time_info_text_view);
        textView.setText( textView.getText() + message);
    }
    
}
