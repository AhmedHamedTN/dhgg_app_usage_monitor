package com.example.danshelloworld;


import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyFirstActivity extends Activity 
{
    public static BroadcastReceiver mReceiver;
    public static String TURN_OFF_BROADCAST = "prefs_turn_off_broadcast";
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {	
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_my_first);
        
        startService();
    }

    @Override
    public void onDestroy() 
    {
    	super.onDestroy();
    }
    
    @Override
    public void onResume()
    {
    	refreshScreen();
        super.onResume();
    }
    
    public void onPause()
    {   
        super.onPause();
    }
    
    public void startService(View view)
    {
    	SharedPreferences settings = getSharedPreferences( TURN_OFF_BROADCAST, 0 );
    	SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean(TURN_OFF_BROADCAST,false);
	    
    	this.startService();
    }
    
    public void startService()
    {
    	SharedPreferences settings = getSharedPreferences( TURN_OFF_BROADCAST, 0 );
    	boolean is_broadcast_off = settings.getBoolean(TURN_OFF_BROADCAST, false);

    	if ( !is_broadcast_off )
    	{
    		System.out.println("registering broadcast receiver");
    		
    		// Initialize broadcast receiver
    		mReceiver = new Broadcast_receiver_handler();
            IntentFilter mfilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            mfilter.addAction(Intent.ACTION_SCREEN_OFF);
            mfilter.addAction("com.blah.blah.somemessage");
            try 
            {
            	registerReceiver(mReceiver, mfilter);
            } 
            catch (Exception e) {}
    	}
    }
    
    public void stopService(View view)
    {
	    System.out.println("Unregistering broadcast receiver");
	    
	    SharedPreferences settings = getSharedPreferences( TURN_OFF_BROADCAST, 0 );
    	SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean(TURN_OFF_BROADCAST,false); 
	    editor.commit();
	 
	    try
	    {
	    	unregisterReceiver(mReceiver);
	    }
        catch (Exception e){}
    }

    public void refreshScreen(View view)
    {
    	refreshScreen();
    }

    public void refreshScreen()
    {
    	System.out.println("refreshScreen");
    	
    	// Get data to display
    	Db_handler db_handler = new Db_handler( this );
        ArrayList<Data_value> data = db_handler.getAllData();
        
        
        Data_value[] data_arr = data.toArray(new Data_value[data.size()]);
        Data_value_adapter adapter = new Data_value_adapter(
        							 this,
        							 R.layout.name_value_row,
        							 data_arr);
                
        
        ListView list_view = (ListView) findViewById(R.id.task_list_view);
        
        // Add rows to the list view.
        list_view.setAdapter(adapter);
    }
    
    public void restartDb(View view)
    {
    	// Get data to display
    	Db_handler db_handler = new Db_handler( this );
    	db_handler.clear_data();
    }

}
