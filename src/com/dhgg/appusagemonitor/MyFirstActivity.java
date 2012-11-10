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
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
	
	boolean m_show_list = true;
	boolean m_show_chart = false;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		m_db_handler = new Db_handler(getApplicationContext());

		setContentView(R.layout.activity_my_first);
		
		setTitle("");
		
		// Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.list_fragment_container) != null) 
        {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) 
            {
                return;
            }

            // Create an instance of ExampleFragment
            AppListFragment m_list_fragment = new AppListFragment();
            
            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            m_list_fragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.list_fragment_container, m_list_fragment, "my_list_fragment").commit();
        }
        
        if (findViewById(R.id.chart_fragment_container) != null) 
        {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) 
            {
                return;
            }

            // Create an instance of ExampleFragment
            AppChartFragment m_chart_fragment = new AppChartFragment();
            
            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            m_chart_fragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.chart_fragment_container, m_chart_fragment, "my_chart_fragment").commit();
        }

		FrameLayout layout = (FrameLayout) findViewById(R.id.chart_fragment_container);
		layout.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, 0f) );
		
        setup_admob_view();
		
		setup_action_bar();
	}
	
	private void setup_admob_view()
	{
		// Add the ADMOB view
		AdView adView = new AdView(this, AdSize.BANNER, "a150686c4e8460b");
		LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_for_adview);
		layout.addView(adView);
		AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);
	}
	
	public void setup_action_bar()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mSpinnerAdapter = 
			ArrayAdapter.createFromResource(
			this, R.array.action_list,
			android.R.layout.simple_spinner_dropdown_item);
		
		actionBar.setListNavigationCallbacks(
		mSpinnerAdapter, new OnNavigationListener() 
		{    
		    @Override
		    public boolean onNavigationItemSelected(int position, long itemId) 
		    {
		       	if ( itemId == 0 )
		       	{	
		       		m_show_list = true;
		       		m_show_chart = false;
		       	}
		       	else if ( itemId == 1 )
		       	{
		       		m_show_list = false;
		       		m_show_chart = true;
		       	}
		       	refreshScreen();

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

		// Get data to display
		Db_handler db_handler = new Db_handler(this);
		ArrayList<Data_value> data = db_handler.getData( hist_pref );
		Data_value[] data_arr = data.toArray(new Data_value[data.size()]);

		    	
		/*
		** Update data in all fragments
		*/
    	AppListFragment list_fragment = (AppListFragment) getFragmentManager().findFragmentByTag("my_list_fragment");    	
    	if ( list_fragment != null )
    	{
        	list_fragment.refreshScreen( data_arr );        	
    	}

    	AppChartFragment chart_fragment = (AppChartFragment) getFragmentManager().findFragmentByTag("my_chart_fragment");    	
    	if ( chart_fragment != null )
    	{
        	chart_fragment.refreshScreen( data_arr );        	
    	}

    	/*
    	 * Update weights to show and hide.
    	*/
    	FrameLayout list_layout = (FrameLayout) findViewById(R.id.list_fragment_container);
		if ( m_show_list )
    	{
        	list_layout.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, 1.0f) );
    	}
    	else
    	{
        	list_layout.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, 0.0f) );
    	}    			
    	
		FrameLayout chart_layout = (FrameLayout) findViewById(R.id.chart_fragment_container);
		if ( m_show_chart )
    	{
        	chart_layout.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, 1.0f) );
    	}
    	else
    	{
        	chart_layout.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, 0.0f) );
    	}    			
    	


    	int data_returned_size = data_arr.length;

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
