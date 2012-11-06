package com.dhgg.appusagemonitor;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MyFirstActivity extends Activity 
{
	public static Db_handler m_db_handler;
	public static BroadcastReceiver receiver = new Broadcast_receiver_handler();
	public static String TURN_OFF_UPDATES = "turn_off_updates";
	public static String SHOW_HIST_PREFS = "show_hist_prefs";

	public static String SHOW_HIST_PREF_TODAY = "s_h_p_today";
	public static String SHOW_HIST_PREF_24_H = "s_h_p_24h";
	public static String SHOW_HIST_PREF_ALL = "s_h_p_all";	
	

	SpinnerAdapter mSpinnerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_my_first);
		
		setTitle("");
		
		m_db_handler = new Db_handler(getApplicationContext());

		// Add the ADMOB view
		AdView adView = new AdView(this, AdSize.BANNER, "a150686c4e8460b");
		LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_for_adview);
		layout.addView(adView);
		AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);
		
		setup_action_bar();
	}
	
	public void setup_action_bar()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mSpinnerAdapter = 
			ArrayAdapter.createFromResource(
			this, R.array.action_list,
			android.R.layout.simple_spinner_dropdown_item);
		
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, 
		    new OnNavigationListener() 
			{    
		        @Override
		        public boolean onNavigationItemSelected(int position, long itemId) 
		        {
	        		AppListFragment list_fragment = (AppListFragment)
	        			getFragmentManager().findFragmentById(R.id.app_list_fragment);
	        		AppChartFragment chart_fragment = (AppChartFragment)
	        			getFragmentManager().findFragmentById(R.id.app_chart_fragment);
			        	
		        	FragmentTransaction ft = getFragmentManager().beginTransaction();

		        	if ( itemId == 0 )
		        	{
			        	ft.show( list_fragment );
			        	list_fragment.refreshScreen( SHOW_HIST_PREF_TODAY );
			        	
			        	ft.show( chart_fragment );
			        	chart_fragment.refreshScreen( SHOW_HIST_PREF_TODAY );
			        	
			        	ft.commit();
		        	}
		        	else if ( itemId == 1 )
		        	{
			        	ft.show( list_fragment );
			        	list_fragment.refreshScreen( SHOW_HIST_PREF_TODAY );
			        	
			        	ft.show( chart_fragment );
			        	chart_fragment.refreshScreen( SHOW_HIST_PREF_TODAY );
			        	
			        	ft.commit();		        		
		        	}
		        	
		            return true;
		        }
			});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// use an inflater to populate the ActionBar with items
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.main_menu, menu);
		
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		menu.clear();
	    getMenuInflater().inflate(R.layout.main_menu, menu);
	    	 	
	    return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// same as using a normal menu
		switch(item.getItemId()) {
		case R.id.item_restart:
			restartDb();
			break;
		case R.id.item_start:

			Toast start_toast = Toast.makeText(getApplicationContext(), 
					                           "Monitoring Started.",
					                           Toast.LENGTH_SHORT);
			start_toast.show();
			
			startService();
			break;		
		case R.id.item_stop:
			
			Toast stop_toast = Toast.makeText(getApplicationContext(), 
											  "Monitoring Stopped.",
											  Toast.LENGTH_SHORT);
			stop_toast.show();
			
			stopService();
			break;
		case R.id.show_today:
			set_hist_prefs( SHOW_HIST_PREF_TODAY );
			refreshScreen();
			break;
		case R.id.show_24_hours:
			set_hist_prefs( SHOW_HIST_PREF_24_H );
			refreshScreen();
			break;
		case R.id.show_all:
			set_hist_prefs( SHOW_HIST_PREF_ALL );
			refreshScreen();
			break;
		case R.id.item_send_data:
			send_data();
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
		// Check to see if we should start the broadcast system.
		SharedPreferences settings = getSharedPreferences(TURN_OFF_UPDATES, 0);
		boolean updates_are_off = settings.getBoolean(TURN_OFF_UPDATES, false);		
		if (!updates_are_off) 
		{
			m_db_handler.update_or_add("screen_on", "screen_on");
			m_db_handler.update_or_add("App Usage Monitor", "com.dhgg.appusagemonitor");
	
			startService();
		}
		
		refreshScreen();
		super.onResume();
	}

	public void onPause() 
	{
		// Check to see if we should send an initial message
		SharedPreferences settings = getSharedPreferences(TURN_OFF_UPDATES, 0);
		boolean updates_are_off = settings.getBoolean(TURN_OFF_UPDATES, false);		
		if (!updates_are_off) 
		{
			m_db_handler.update_or_add("App Usage Monitor", "com.dhgg.appusagemonitor");
			m_db_handler.update_or_add("screen_off", "screen_off");
		}
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
	
	public void set_hist_prefs( String pref )
	{
		// Update the saved preference.
		SharedPreferences settings = getSharedPreferences(SHOW_HIST_PREFS, 0);		
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SHOW_HIST_PREFS, pref);
		editor.commit();		
	}

	public void startService() 
	{
		set_update_flag(false);
		
	    // Send start message
		Intent intent=new Intent( this, Broadcast_receiver_handler.class);
		intent.setAction("dhgg.app.usage.monitor.start");
		sendBroadcast(intent);		
	}

	public void stopService() 
	{
		set_update_flag(true);
		
	    // Send stop message
		Intent intent=new Intent( this, Broadcast_receiver_handler.class);
		intent.setAction("dhgg.app.usage.monitor.stop");
		sendBroadcast(intent);		
	}

		
	public void send_data() 
	{	
		// Get data to send
		Db_handler db_handler = new Db_handler(this);
		ArrayList<Data_value> data = db_handler.getAllData( );
		
		String data_to_send = "";
		data_to_send += "App Name   \tTime Spent Using\n";
		for (Data_value dv : data)
		{
			data_to_send += dv.description + " \t" + get_time_str(dv.value) + "\n";
		}

		Intent send_intent = new Intent(android.content.Intent.ACTION_SEND);
		send_intent.setType("text/plain");
		send_intent.putExtra(Intent.EXTRA_SUBJECT, "App Usage Monitor data");
		send_intent.putExtra(Intent.EXTRA_TEXT, data_to_send);
	    
		startActivity(send_intent);
	}
	
	public void restartDb() 
	{
		// Get data to display
		Db_handler db_handler = new Db_handler(this);
		db_handler.clear_data();
		
		refreshScreen();
	}

    public String get_time_str( int time_in_seconds)
    {
    	int total_secs = time_in_seconds;
    	
        int hours = total_secs / 3600;
        int mins = (total_secs - (hours * 3600))/ 60;
        int secs = total_secs - (hours * 3600) - (mins * 60);
        
        String time_str = "";
        if (hours > 0)
        {
        	time_str += hours + "h ";
        }
        if (mins > 0)
        {
        	time_str += mins + "m ";
        }
        time_str += secs + "s";
        
    	return time_str;
    }
    
    public void refreshScreen()
    {
		// Find out what type of data to display.
		SharedPreferences settings = getSharedPreferences( SHOW_HIST_PREFS, 0);
		String hist_pref = settings.getString(SHOW_HIST_PREFS,SHOW_HIST_PREF_ALL);
		
    	// Update the app list fragment.
    	AppListFragment a_fragment = (AppListFragment) 
    			getFragmentManager().findFragmentById(R.id.app_list_fragment);
    	int data_returned_size = a_fragment.refreshScreen( hist_pref );

    	// Show a toast to indicate what we are displaying
    	String toast_msg = "Showing usage ...";
    	boolean show_toast = false;
    	if ( hist_pref.equals( SHOW_HIST_PREF_TODAY ) )
    	{
    		show_toast = true;
    		toast_msg = "Showing usage for today.";
    	}
    	else if ( hist_pref.equals( SHOW_HIST_PREF_24_H ) )
    	{
    		show_toast = true;
    		toast_msg = "Showing usage for last 24 hours.";
    	}
    			
    	if ( data_returned_size == 1)
    	{
			show_toast = true;
			toast_msg = "Welcome! Return later to see updated stats.";
    	}

    	// Check to see if we should start the broadcast system.
    	SharedPreferences update_pref = getSharedPreferences(TURN_OFF_UPDATES, 0);
    	boolean updates_are_off = update_pref.getBoolean(TURN_OFF_UPDATES, false);
    	if ( updates_are_off )
    	{
    		toast_msg += "\nMonitoring is off.";
    	}
    	else
    	{
    		toast_msg += "\nMonitoring is on.";
    	}

    	if ( show_toast )
    	{
    		Toast toast = Toast.makeText(getApplicationContext(),									
                 toast_msg, Toast.LENGTH_LONG);
    		toast.show();
    	}
    }
}
