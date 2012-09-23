package com.dhgg.appusagetracker;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MyFirstActivity extends Activity {
	public static BroadcastReceiver mReceiver = null;
	public static String TURN_OFF_UPDATES = "turn_off_updates";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_first);
		
		// gets the activity's default ActionBar
		ActionBar actionBar = getActionBar();
		actionBar.show();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// use an inflater to populate the ActionBar with items
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.main_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// same as using a normal menu
		switch(item.getItemId()) {
		case R.id.item_restart:
			restartDb();
			break;
		case R.id.item_stop:
			stopService();
			break;
		case R.id.item_start:
			set_update_flag(false);
			break;
		}
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		Db_handler db = new Db_handler(getApplicationContext());
		db.update_or_add("screen_on", "screen_on");
		db.update_or_add("App Usage Tracker", "com.dhgg.appusagetracker");

		refreshScreen();

		super.onResume();
	}

	public void onPause() {
		Db_handler db = new Db_handler(getApplicationContext());

		db.update_or_add("App Usage Tracker", "com.dhgg.appusagetracker");
		db.update_or_add("screen_off", "screen_off");

		this.startService();

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
		// Check if updates are turned off
		SharedPreferences settings = getSharedPreferences(TURN_OFF_UPDATES, 0);
		boolean updates_are_off = settings.getBoolean(TURN_OFF_UPDATES, false);		
		if (updates_are_off) 
		{
			return;
		}
		
		// Register the receiver.
		System.out.println("registering broadcast receiver");
		if (mReceiver == null)
		{
			mReceiver = new Broadcast_receiver_handler();
		}
		
		IntentFilter mfilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		mfilter.addAction(Intent.ACTION_SCREEN_OFF);
		mfilter.addAction("com.blah.blah.somemessage");
		
		try { registerReceiver(mReceiver, mfilter); }
		catch (Exception e) {}
		
		// Turn on the alarm to get data.
		Broadcast_receiver_handler bh = new Broadcast_receiver_handler();
		bh.SetAlarm(getApplicationContext());
	}

	public void stopService() 
	{
		set_update_flag(true);

		try {unregisterReceiver(mReceiver);} 
		catch (Exception e) {	}
	}

	public void refreshScreen() {
		System.out.println("refreshScreen");

		// Get data to display
		Db_handler db_handler = new Db_handler(this);
		ArrayList<Data_value> data = db_handler.getAllData();

		Data_value[] data_arr = data.toArray(new Data_value[data.size()]);
		Data_value_adapter adapter = new Data_value_adapter(this,
				R.layout.name_value_row, data_arr);

		ListView list_view = (ListView) findViewById(R.id.task_list_view);

		// Add rows to the list view.
		list_view.setAdapter(adapter);
	}

	public void restartDb() {
		// Get data to display
		Db_handler db_handler = new Db_handler(this);
		db_handler.clear_data();
	}

}
