package com.dhgg.appusagemonitor;

import java.io.IOException;
import java.util.ArrayList;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.res.Configuration;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class MyFirstActivity extends FragmentActivity 
{
	public static Db_handler m_db_handler;
	
	public static String TURN_OFF_UPDATES = "turn_off_updates";
	public static String SHOW_HIST_PREFS = "show_hist_prefs";
	public static String SHOW_CHART = "show_chart";
	public static String SHOW_LOG = "show_log";

	public static String SHOW_HIST_PREF_TODAY = "s_h_p_today";
	public static String SHOW_HIST_PREF_24_H = "s_h_p_24h";
	public static String SHOW_HIST_PREF_ALL = "s_h_p_all";	
	
	boolean m_show_list = true;
	boolean m_show_chart = false;
	boolean m_is_landscape = false;
	boolean m_show_log = false;
	
	final int m_max_data_size = 22;

	private static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final String PREF_KEY_ACCOUNT_NAME = "PREF_KEY_ACCOUNT_NAME";
	private GoogleAccountCredential credential;
	CloudBackendMessaging cloudBackend;

	private void clear_database() 
	{
		// Get data to display
		m_db_handler.clear_data();
		
		refresh_screen();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		//Log.w("DHGG","onCreate");
		super.onCreate(savedInstanceState);
		
		m_db_handler = new Db_handler(getApplicationContext());

		setContentView(R.layout.activity_my_first);
		
		// Check if Activity has been switched to landscape mode
	    // If yes, finished and go back to the start Activity
	    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) 
	    {
	    	m_is_landscape = true;
	    }
	    
	    setup_fragments( savedInstanceState );
		
        setup_admob_view();

		cloudBackend = new CloudBackendMessaging(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// use an inflater to populate the ActionBar with items
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.main_menu, menu);
		
		/*
		SharedPreferences show_chart_settings = getSharedPreferences( SHOW_CHART, 0);
		m_show_chart = show_chart_settings.getBoolean(SHOW_CHART, false);
		
		SharedPreferences show_log = getSharedPreferences( SHOW_LOG, 0);
		m_show_log = show_log.getBoolean(SHOW_LOG, false);
		*/
		
		//Log.w("DHGG","onCreateOptionsMenu c:"+m_show_chart+" l:"+m_show_log);
		if ( !m_show_chart ) {
			menu.findItem(R.id.item_show_chart).setTitle("Chart");
			if ( !m_show_log ) {
				menu.findItem(R.id.item_show_log).setTitle("Itemized");
			} else {
				menu.findItem(R.id.item_show_log).setTitle("Summary");
			}
		} else {
			menu.findItem(R.id.item_show_chart).setTitle("Hide Chart");
		}
		

		return true;
	}

	@Override
	public void onDestroy() 
	{		
		super.onDestroy();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		SharedPreferences settings = getSharedPreferences(SHOW_CHART, 0);
		SharedPreferences.Editor editor = settings.edit();

		SharedPreferences log_settings = getSharedPreferences(SHOW_LOG, 0);
		SharedPreferences.Editor log_editor = log_settings.edit();
		
		// same as using a normal menu
		switch(item.getItemId()) {
		case R.id.item_restart:
			clear_database();
		break;
		case R.id.item_start:

			Toast start_toast = Toast.makeText(getApplicationContext(), 
					                           "Monitoring Started.",
					                           Toast.LENGTH_SHORT);
			start_toast.show();
			
			send_start_broadcast();
		break;		
		case R.id.item_stop:
			
			Toast stop_toast = Toast.makeText(getApplicationContext(), 
											  "Monitoring Stopped.",
											  Toast.LENGTH_SHORT);
			stop_toast.show();
			
			send_stop_broadcast();
		break;
		case R.id.show_today:
			set_hist_prefs( SHOW_HIST_PREF_TODAY );
			refresh_screen();
		break;
		case R.id.show_24_hours:
			set_hist_prefs( SHOW_HIST_PREF_24_H );
			refresh_screen();
		break;
		case R.id.show_all:
			set_hist_prefs( SHOW_HIST_PREF_ALL );
			refresh_screen();
			break;
		case R.id.item_send_data:
			send_data();
		break;
		case R.id.item_show_chart:
			// Update the saved preference.
			m_show_chart = !m_show_chart;

			editor.putBoolean(SHOW_CHART, m_show_chart);
			editor.commit();

			if ( m_show_chart )
			{
				item.setTitle("Hide Chart");
				m_show_log = false;
				log_editor.putBoolean(SHOW_LOG, false);
			}
			else
			{
				item.setTitle("Chart");
			}
			
			refresh_screen();
		break;
		case R.id.item_show_log:
			// Update the saved preference.
			m_show_log = !m_show_log;
			
			log_editor.putBoolean(SHOW_LOG, m_show_log);
			log_editor.commit();
						
			if ( m_show_log ) {
				item.setTitle("Summary");
				m_show_chart = false;
				editor.putBoolean(SHOW_CHART, false);
			} else {
				item.setTitle("Itemized");
			}
			
			refresh_screen();
		break;
		}
		return true;
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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		menu.clear();
	    getMenuInflater().inflate(R.layout.main_menu, menu);
	    	 	
	    /*
		SharedPreferences show_chart_settings = getSharedPreferences( SHOW_CHART, 0);
		m_show_chart = show_chart_settings.getBoolean(SHOW_CHART, false);

		SharedPreferences show_log_settings = getSharedPreferences( SHOW_LOG, 0);
		m_show_log = show_log_settings.getBoolean(SHOW_LOG, false);
		*/
	    
		//Log.w("DHGG","onPrepareOptionsMenu c:"+m_show_chart+" l:"+m_show_log);
		if ( !m_show_chart ) {
			menu.findItem(R.id.item_show_chart).setTitle("Chart");
		    
			if ( !m_show_log ) {
				menu.findItem(R.id.item_show_log).setTitle("Itemized");
			} else {
				menu.findItem(R.id.item_show_log).setTitle("Summary");
			}
		} else {
			menu.findItem(R.id.item_show_chart).setTitle("Hide Chart");
			menu.findItem(R.id.item_show_log).setTitle("Itemized");
		}
		
		
		return super.onPrepareOptionsMenu(menu);
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
	
			send_start_broadcast();
		}
		
		SharedPreferences show_chart_settings = getSharedPreferences( SHOW_CHART, 0);
		m_show_chart = show_chart_settings.getBoolean(SHOW_CHART, false);
		
		SharedPreferences show_log = getSharedPreferences( SHOW_LOG, 0);
		m_show_log = show_log.getBoolean(SHOW_LOG, false);
		
		refresh_screen();
		super.onResume();
	}

	private void refresh_screen()
    {	
		//Log.w("DHGG","refresh_screen l:"+m_show_log);
		if (m_show_log) {
			refresh_audit_screen();
		} else {
			refresh_amount_screen();
		}
    }
	
	private void refresh_audit_screen() {
    	// allow only one type of lookup for now
		String hist_pref = SHOW_HIST_PREF_24_H;

		// Get data to display
		ArrayList<Time_log> data = m_db_handler.getTimeLog( hist_pref, "" );
		Time_log[] data_arr = data.toArray(new Time_log[data.size()]);
		
    	AppListFragment list_fragment = (AppListFragment) getSupportFragmentManager().findFragmentByTag("my_list_fragment");    	
    	if ( list_fragment != null ) {
        	list_fragment.refresh_screen_audit( data_arr );        	
    	}    	

    	update_screen_view();
    	
    	int data_returned_size = data_arr.length;
    	show_toast(hist_pref, data_returned_size);
    }

	private void refresh_amount_screen() {
		SharedPreferences show_chart_settings = getSharedPreferences( SHOW_CHART, 0);
		m_show_chart = show_chart_settings.getBoolean(SHOW_CHART, false);

		// Find out what type of data to display.
		SharedPreferences settings = getSharedPreferences( SHOW_HIST_PREFS, 0);
		String hist_pref = settings.getString( SHOW_HIST_PREFS, SHOW_HIST_PREF_ALL );

		// Get data to display
		ArrayList<Data_value> data = m_db_handler.getData( hist_pref, "" );
		Data_value[] data_arr = data.toArray(new Data_value[data.size()]);
		
    	Data_value[] normal_data_arr;
    	if ( m_show_chart ) {	
    		normal_data_arr = get_data_slices(data_arr);
    	} else {
    		normal_data_arr = data_arr;
    	}

    	AppListFragment list_fragment = (AppListFragment) getSupportFragmentManager().findFragmentByTag("my_list_fragment");    	
    	if ( list_fragment != null ) {
        	list_fragment.refresh_screen( normal_data_arr, m_show_chart );        	
    	}

    	AppChartFragment chart_fragment = (AppChartFragment) getSupportFragmentManager().findFragmentByTag("my_chart_fragment");    	
    	if ( chart_fragment != null ) {
        	chart_fragment.refresh_screen( normal_data_arr );        	
    	}

    	update_screen_view();
    	
    	int data_returned_size = data_arr.length;
    	show_toast(hist_pref, data_returned_size);
    }
	
	private void update_screen_view() {

		int list_fragment_id = R.id.list_fragment_container;
		int chart_fragment_id = R.id.chart_fragment_container;
		
    	FrameLayout list_layout = (FrameLayout) findViewById( list_fragment_id );
		if ( m_is_landscape ) {
			list_layout.setLayoutParams( new LinearLayout.LayoutParams( 0, LayoutParams.MATCH_PARENT, 1.0f) );
    	} else {
        	list_layout.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, 1.0f) );
    	}
    	
		FrameLayout chart_layout = (FrameLayout) findViewById( chart_fragment_id );
		if ( m_show_chart )	{
			if ( m_is_landscape ) {
				chart_layout.setLayoutParams( new LinearLayout.LayoutParams( 0, LayoutParams.MATCH_PARENT, .75f) );
			} else {
				chart_layout.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, .75f) );
			}
    	} else {  
    		chart_layout.setLayoutParams( new LinearLayout.LayoutParams( 0, 0, 0.0f) );
	    }   
	}

	public Data_value[] get_data_slices(Data_value[] data_arr)
	{
		int num_values = data_arr.length;
		float total = 0;
		for ( int i = 0; i < num_values; i++ )
		{
			total += data_arr[i].value;
		}
		
		int normal_data_arr_size = num_values;
		if ( num_values > m_max_data_size )
		{
			normal_data_arr_size = m_max_data_size ;
		}

		Data_value[] normal_data_arr = new Data_value[ normal_data_arr_size ];
		System.arraycopy( data_arr, 0, normal_data_arr, 0, normal_data_arr_size );
		
		int subtotal = 0;
		for ( int i = 0; i < normal_data_arr_size; i++)
		{
			subtotal += normal_data_arr[ i ].value;
			
			float fraction = (float)normal_data_arr[ i ].value / total;
			int percent = (int)(fraction * 100);
			normal_data_arr[ i ].value = percent;
		}
		
		if ( normal_data_arr_size == m_max_data_size)
		{
			float remaining = total - subtotal;
			
			float fraction = remaining / total;
			
			int percent = (int) ( fraction * 100);
			
			normal_data_arr[ normal_data_arr_size -1 ].value = percent;
			normal_data_arr[ normal_data_arr_size -1 ].description = "Other ...";
		}
		
		return normal_data_arr;
	}
	
	public void show_toast(String hist_pref, int data_returned_size)
    {
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
    	
    	if (m_show_log)
    	{
    		show_toast = true;
    		toast_msg = "Showing time log for last 24 hours.";
    	}

    	if ( show_toast )
    	{
    		Toast toast = Toast.makeText(getApplicationContext(), toast_msg, Toast.LENGTH_SHORT);
    		toast.show();
    	}
    	
    }
	
	public void old_send_data() 
	{
		// Get data to send
		ArrayList<Data_value> data = m_db_handler.getData( SHOW_HIST_PREF_ALL, "" );
		
		String data_to_send = "";
		data_to_send += "App Name   \tTime Spent Using\n";
		for (Data_value dv : data)
		{
			//data_to_send += dv.description + " \t" + get_time_str(dv.value) + "\n";
		}

		Intent send_intent = new Intent(android.content.Intent.ACTION_SEND);
		send_intent.setType("text/plain");
		send_intent.putExtra(Intent.EXTRA_SUBJECT, "App Usage Monitor data");
		send_intent.putExtra(Intent.EXTRA_TEXT, data_to_send);
	    
		startActivity(send_intent);
	}
	
	public boolean authenticate()
	{
	    // create credential
	    credential = GoogleAccountCredential.usingAudience(this, Consts.AUTH_AUDIENCE);
	    cloudBackend.setCredential(credential);
	
	    // if auth enabled, get account name from the shared pref
		String accountName = cloudBackend.getSharedPreferences().getString(PREF_KEY_ACCOUNT_NAME,null);
		if (accountName == null) {
			// let user pick an account
			super.startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			return false; // continue init in onActivityResult
		} else {
		    credential.setSelectedAccountName(accountName);
		    return true; // continue init in onActivityResult
	    }
	}

    protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
	    Log.w("DHGG","onActivityResult");
  
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_ACCOUNT_PICKER:
			if (data != null && data.getExtras() != null) 
			{
			    // set the picked account name to the credential
				String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			    credential.setSelectedAccountName(accountName);

		        // save account name to shared pref
		        SharedPreferences.Editor e = cloudBackend.getSharedPreferences().edit();
		        e.putString(PREF_KEY_ACCOUNT_NAME, accountName);
		        e.commit();
		        
		        send_data();
		    }
			break;
        default:
		    break;
	    }
	}
	  
	public void send_data() {	
		Log.w("DHGG","MyFirstActivity::send_data");

		if (!authenticate())
		{
			return;
		}
		
		// TODO: 
		// Ask appengine for last update.

		// 
		//m_db_handler.
		// end_time
		// Get data to display
		String hist_pref = SHOW_HIST_PREF_24_H;
		ArrayList<Time_log> data = m_db_handler.getTimeLog( hist_pref, "" );
		Time_log[] data_arr = data.toArray(new Time_log[data.size()]);
		
		
		
		int numLogs = data_arr.length;
		for ( int i = 0; i < numLogs; i++ )
		{
		    // create a CloudEntity with the new post
		    CloudEntity newPost = new CloudEntity("AppUsage");
		    newPost.put("appName", data_arr[i].description); 
		    newPost.put("appStartTime", data_arr[i].start_time); 
		    newPost.put("appEndTime", data_arr[i].end_time); 
			Log.w("DHGG","sendData "+i+" post:"+newPost);
	
		    // create a response handler that will receive the result or an error
		    CloudCallbackHandler<CloudEntity> handler = new CloudCallbackHandler<CloudEntity>() {
				@Override
				public void onComplete(final CloudEntity result) {
					Log.w("DHGG","sendData onComplete with ok result:"+result.toString());
				}
		
		        @Override
				public void onError(final IOException exception) {
					Log.w("DHGG","sendData onError:"+exception.toString());
				}
			};
					
		    // execute the insertion with the handler
		    cloudBackend.insert(newPost, handler);
		}
	}

	private void send_start_broadcast() {
		set_update_flag(false);
		
	    // Send start message
		Intent intent=new Intent( this, Broadcast_receiver_handler.class);
		intent.setAction("dhgg.app.usage.monitor.start");
		sendBroadcast(intent);		
	}

	private void send_stop_broadcast() 
	{
		set_update_flag(true);
		
	    // Send stop message
		Intent intent=new Intent( this, Broadcast_receiver_handler.class);
		intent.setAction("dhgg.app.usage.monitor.stop");
		sendBroadcast(intent);		
	}
	
	private void set_hist_prefs( String pref ) {
		// Update the saved preference.
		SharedPreferences settings = getSharedPreferences(SHOW_HIST_PREFS, 0);		
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SHOW_HIST_PREFS, pref);
		editor.commit();		
	}
	
	private void set_update_flag( boolean flag) {
		// Update the saved preference.
		SharedPreferences settings = getSharedPreferences(TURN_OFF_UPDATES, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(TURN_OFF_UPDATES, flag);
		editor.commit();		
	}

    private void setup_admob_view() {
		// Add the ADMOB view
		AdView adView = new AdView(this, AdSize.BANNER, "a150686c4e8460b");
		LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_for_adview);
		
		if ( layout != null )
		{
			layout.addView(adView);
			AdRequest adRequest = new AdRequest();
			adView.loadAd(adRequest);
		}
	}
    
    private void setup_fragments( Bundle savedInstanceState ) {
		// Check that the activity is using the layout version with
        // the fragment_container FrameLayout
		int list_fragment_id = R.id.list_fragment_container;
		int chart_fragment_id = R.id.chart_fragment_container;
		
		if ( savedInstanceState  != null ) {
			return;
		}
		
        if (findViewById( list_fragment_id ) != null) {
            // Create an instance of ExampleFragment
            AppListFragment m_list_fragment = new AppListFragment();
            
            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            m_list_fragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add( list_fragment_id, m_list_fragment, "my_list_fragment").commit();
        }
        
        if (findViewById( chart_fragment_id ) != null) {
            // Create an instance of ExampleFragment
            AppChartFragment m_chart_fragment = new AppChartFragment();
            
            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            m_chart_fragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add( chart_fragment_id , m_chart_fragment, "my_chart_fragment").commit();
        }

		FrameLayout layout = (FrameLayout) findViewById( chart_fragment_id );
		layout.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, 0f) );
	}
}
