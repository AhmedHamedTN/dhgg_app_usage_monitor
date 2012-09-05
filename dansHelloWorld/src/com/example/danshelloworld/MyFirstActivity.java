package com.example.danshelloworld;


import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
        
        // Initialize broadcast receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new Broadcast_receiver_handler();
        registerReceiver(mReceiver, filter);        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_my_first, menu);
        return true;
    }
    
    @Override
    public void onResume()
    {
    	// Get data to display
    	Db_handler db_handler = new Db_handler( this );
        ArrayList<String> data = db_handler.getAllData();
        
        // Set up rows for adding to the list view    
        ArrayAdapter<String> list_adapter = 
        		new ArrayAdapter<String>(this,
        				                 R.layout.name_value_row,
        				                 R.id.name, 
        				                 data);
        
        // Add rows to the list view.
        ListView list_view = (ListView) findViewById(R.id.task_list_view);        
        list_view.setAdapter(list_adapter);
    
        super.onResume();
    }
    
    public void onPause()
    {   
        super.onPause();
    }
    
    public void startService(View view)
    {
    }
    
    public void stopService(View view)
    {
    }
    
    
}
