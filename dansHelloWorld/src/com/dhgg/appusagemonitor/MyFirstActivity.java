package com.dhgg.appusagemonitor;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class MyFirstActivity extends Activity 
{
	public static Db_handler m_db_handler;
    public static BroadcastReceiver receiver = new Broadcast_receiver_handler();
    
	public static String TURN_OFF_UPDATES = "turn_off_updates";
	

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_first);
		
		m_db_handler = new Db_handler(getApplicationContext());
	}
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// use an inflater to populate the ActionBar with items
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.main_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{	
		// same as using a normal menu
		switch(item.getItemId()) {
		case R.id.item_restart:
			restartDb();
			break;
		case R.id.item_stop:
			set_update_flag(true);
			stopService();
			break;
		case R.id.item_start:
			set_update_flag(false);
			startService();
			break;
		}
		return true;
	}

	@Override
	public void onDestroy() 
	{		
		super.onDestroy();
	}

	@Override
	public void onResume() 
	{
		m_db_handler.update_or_add("screen_on", "screen_on");
		m_db_handler.update_or_add("App Usage Monitor", "com.dhgg.appusagemonitor");

		refreshScreen();

		// Check to see if we should send an initial message
		SharedPreferences settings = getSharedPreferences(TURN_OFF_UPDATES, 0);
		boolean updates_are_off = settings.getBoolean(TURN_OFF_UPDATES, false);		
		if (!updates_are_off) 
		{
			Intent intent=new Intent( this, Broadcast_receiver_handler.class);
			intent.setAction("dhgg.app.usage.monitor.start");
			sendBroadcast(intent);
		}

		super.onResume();
	}

	public void onPause() 
	{
		m_db_handler.update_or_add("App Usage Monitor", "com.dhgg.appusagemonitor");
		m_db_handler.update_or_add("screen_off", "screen_off");

		super.onPause();
	}

	public void set_update_flag( boolean flag) 
	{
		// Update the saved preference.
		SharedPreferences settings = getSharedPreferences(TURN_OFF_UPDATES, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(TURN_OFF_UPDATES, flag);
		editor.commit();		
	}

	public void startService() 
	{	
	    // Send start message
		Intent intent=new Intent( this, Broadcast_receiver_handler.class);
		intent.setAction("dhgg.app.usage.monitor.start");
		sendBroadcast(intent);		
	}

	public void stopService() 
	{
	    // Send stop message
		Intent intent=new Intent( this, Broadcast_receiver_handler.class);
		intent.setAction("dhgg.app.usage.monitor.stop");
		sendBroadcast(intent);		
	}

	public void refreshScreen() 
	{
		// Get data to display
		Db_handler db_handler = new Db_handler(this);
		ArrayList<Data_value> data = db_handler.getAllData();

		Data_value[] data_arr = data.toArray(new Data_value[data.size()]);
		Data_value_adapter adapter = new Data_value_adapter(this,
				R.layout.name_value_row, data_arr);

		ListView list_view = (ListView) findViewById(R.id.task_list_view);

		// Add rows to the list view.
		list_view.setAdapter(null);
		list_view.setAdapter(adapter);
	}

	public void restartDb() 
	{
		// Get data to display
		Db_handler db_handler = new Db_handler(this);
		db_handler.clear_data();
	}

}
