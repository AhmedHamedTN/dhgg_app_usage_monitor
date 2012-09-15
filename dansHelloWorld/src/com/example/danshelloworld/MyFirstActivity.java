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
    public static String PREFS_REGISTERED = "prefs_registered";
    
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
    	this.startService();
    }
    
    public void startService()
    {
    	SharedPreferences settings = getSharedPreferences( PREFS_REGISTERED, 0 );
    	SharedPreferences.Editor editor = settings.edit();
    	boolean is_registered = settings.getBoolean(PREFS_REGISTERED, false);
    	if ( !is_registered )
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
            	
    		
    		// Update the status
    	    editor.putBoolean(PREFS_REGISTERED,true); 
    	    editor.commit();
    	}
    	else
    	{
    		System.out.println("already registered");
    	}
    }
    
    public void stopService(View view)
    {
    	SharedPreferences settings = getSharedPreferences( PREFS_REGISTERED, 0 );
    	SharedPreferences.Editor editor = settings.edit();
    	boolean is_registered = settings.getBoolean(PREFS_REGISTERED, true);
    	if ( is_registered )
    	{
    		System.out.println("Unregistering broadcast receiver");

            try 
            {
            	unregisterReceiver(mReceiver);
            }
            catch (Exception e){}
            

    		// Update the status
    	    editor.putBoolean(PREFS_REGISTERED,false); 
    	    editor.commit();
    	}
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
