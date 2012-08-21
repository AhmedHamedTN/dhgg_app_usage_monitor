package com.example.danshelloworld;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MyFirstActivity extends Activity {
	
	// adding comment to see difference
	public final static String MESSAGE_KEY = "com.example.myapp.MESSAGE";
	public final static String PREFS_NAME = "myPrefsFile";
	public static long time_msg_sent = 0;
	public static long time_on_resume = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_my_first);

        // Retrieve the last message sent.
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String lastMsg = settings.getString("lastMessage", "No previous messages");
        logMsg("on Create lastMsg:"+lastMsg);
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
        time_on_resume = System.currentTimeMillis();
        
        // if we've already sent a message, 
        // we must be receiving a message now
        if ( time_msg_sent != 0 )
        {	
        	long time_diff_s = (System.currentTimeMillis() - time_msg_sent) / 1000;
        	logMsg("onResume: "+time_diff_s+"s since msg sent.");
        	

            // Retrieve the last message sent.
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String lastMsg = settings.getString("lastMessage", "No previous messages");

            // Create a database handler
            Db_handler db_handler = new Db_handler( this );
            db_handler.addData(lastMsg, (int) time_diff_s );
            
            // Dump out all the records
            db_handler.dumpAllData();
            
            // reset
            time_msg_sent = 0;        	
        }
        
        super.onResume();
    }
    
    public void onPause()
    {
        long time_diff = (System.currentTimeMillis() - time_on_resume) / 1000;
        logMsg("onPause: time in foreground:"+time_diff+"s.");
        
        super.onPause();
    }
    
    /** Called when the user selects the Send button */
    public void sendMessage(View view) {
    	
    	// initial time sent
    	time_msg_sent = System.currentTimeMillis();
    	logMsg("Sending message at " + time_msg_sent);
    	
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	
    	// Get the input text to send to the intent
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();

        // Save the last message sent. 
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lastMessage", message);
        editor.commit();
        
    	// Add the message as a key-value pair
    	intent.putExtra(MESSAGE_KEY, message);
    	
    	// Start an activity, and pass in the intent
    	startActivity(intent);
    }
    
    public void logMsg(String message) {
    	// Get the input text to send to the intent
    	TextView textView = (TextView) findViewById(R.id.time_info_text_view);
        textView.setText( message + '\n' + textView.getText() );

        System.out.println(message);
    }
    
    
}
