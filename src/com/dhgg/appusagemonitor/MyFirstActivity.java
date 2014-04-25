package com.dhgg.appusagemonitor;

import java.util.ArrayList;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import com.dhgg.cloudbackend.SyncUtils;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class MyFirstActivity extends FragmentActivity 
{
	public static Db_handler m_db_handler;
	
	// User interface preferences
	public static String UI_PREFS = "ui_prefs";

	public static String SHOW_CHART = "show_chart";
	public static String SHOW_LOG = "show_log";

	public static String SHOW_HIST_PREFS = "show_hist_prefs";
	public static String SHOW_HIST_PREF_TODAY = "s_h_p_today";
	public static String SHOW_HIST_PREF_24_H = "s_h_p_24h";
	public static String SHOW_HIST_PREF_ALL = "s_h_p_all";	

	// Hidden from UI.
	public static String TURN_OFF_UPDATES = "turn_off_updates";
	
	// 
	boolean m_show_list = true;
	boolean m_show_chart = false;
	boolean m_is_landscape = false;
	boolean m_show_log = false;
	
	final int m_max_data_size = 22;

	private static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final String PREF_KEY_ACCOUNT_NAME = "PREF_KEY_ACCOUNT_NAME";
	private GoogleAccountCredential mCredential;
	
	// Content provider authority
    // Sync interval constants
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L; //60L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
            SECONDS_PER_MINUTE *
            MILLISECONDS_PER_SECOND;

	public boolean add_sync_account()
	{
		mCredential = GoogleAccountCredential.usingAudience(this, Consts.AUTH_AUDIENCE);

		// check if google services is up to date
		int isGoogleAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isGoogleAvailable == ConnectionResult.SUCCESS)
		{
			// let user pick an account
			super.startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		}
		else
		{
			String toast_msg = "Google Services is not up to date. Cannot create sync account.";
    		Toast toast = Toast.makeText(getApplicationContext(), toast_msg, Toast.LENGTH_LONG);
    		toast.show();
		}

	    return true; 
	}
	
	public boolean authenticate()
	{
	    // get account name from the shared pref
		SharedPreferences settings = getSharedPreferences(PREF_KEY_ACCOUNT_NAME,Context.MODE_PRIVATE);
		String accountName = settings.getString(PREF_KEY_ACCOUNT_NAME, null);	

		SharedPreferences prefTriedSync = getSharedPreferences("TRIED_SYNC", Context.MODE_PRIVATE);
		boolean triedSync = prefTriedSync.getBoolean("TRIED_SYNC", false);	

		mCredential = GoogleAccountCredential.usingAudience(this, Consts.AUTH_AUDIENCE);
		if (accountName == null) {
			//Log.i("DHGG","authenticate should we pick an account?" + triedSync);
			if ( !triedSync )
			{
				// check if google services is up to date
				int isGoogleAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
				if (isGoogleAvailable == ConnectionResult.SUCCESS)
				{
					// let user pick an account
					// Log.i("DHGG","authenticate pick account");
					super.startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
				}
				//Log.i("DHGG", "Google service status = "+isGoogleAvailable);
			}
		} 
		else {
			//Log.i("DHGG","using known account");
		    SyncUtils.CreateSyncAccount(this);
	    }
		SharedPreferences.Editor prefEditor = getSharedPreferences("TRIED_SYNC", Context.MODE_PRIVATE).edit();
		prefEditor.putBoolean("TRIED_SYNC", true);
		prefEditor.commit();

	    return true; 
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

	protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
	    //Log.w("DHGG","onActivityResult");
  
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_ACCOUNT_PICKER:
			if (data != null && data.getExtras() != null) 
			{
			    // set the picked account name to the mCredential
				String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			    mCredential.setSelectedAccountName(accountName);

		        // save account name to shared pref
		        SharedPreferences.Editor e = getSharedPreferences(
		            PREF_KEY_ACCOUNT_NAME,
		            Context.MODE_PRIVATE).edit();
		        e.putString(PREF_KEY_ACCOUNT_NAME, accountName);
		        e.commit();

		        // Set up sync account
				//Log.w("DHGG","onActivityResult "+requestCode+" accountName: "+accountName);
		        SyncUtils.CreateSyncAccount(this);
		    }
			break;
        default:
		    break;
	    }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

        // Get account for sync-ing data, if needed
		authenticate();

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// use an inflater to populate the ActionBar with items
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.main_menu, menu);
		
		
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
		//Log.d("DHGG","onOptionsItemSelected "+item.getItemId());
		SharedPreferences settings = getSharedPreferences(UI_PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		// same as using a normal menu
		switch(item.getItemId()) {
		/*
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
		*/
		case R.id.show_today:
			set_hist_prefs( SHOW_HIST_PREF_TODAY );
			m_show_log = false;
			editor.putBoolean(SHOW_LOG, m_show_log);
			editor.commit();
			refresh_screen();
		break;
		case R.id.show_24_hours:
			set_hist_prefs( SHOW_HIST_PREF_24_H );
			m_show_log = false;
			editor.putBoolean(SHOW_LOG, m_show_log);
			editor.commit();
			refresh_screen();
		break;
		case R.id.show_all:
			set_hist_prefs( SHOW_HIST_PREF_ALL );
			m_show_log = false;
			editor.putBoolean(SHOW_LOG, m_show_log);
			editor.commit();
			refresh_screen();
			break;
		case R.id.item_send_data:
			sendData();
		break;
		case R.id.item_show_chart:
			// Update the saved preference.
			m_show_chart = !m_show_chart;

			editor.putBoolean(SHOW_CHART, m_show_chart);

			if ( m_show_chart )
			{
				item.setTitle("Hide Chart");
				m_show_log = false;
				editor.putBoolean(SHOW_LOG, false);
			}
			else
			{
				item.setTitle("Chart");
			}

			editor.commit();
			refresh_screen();
		break;
		case R.id.item_show_log:
			// Update the saved preference.
			m_show_log = !m_show_log;
			
			editor.putBoolean(SHOW_LOG, m_show_log);
						
			if ( m_show_log ) {
				item.setTitle("Summary");
				m_show_chart = false;
				editor.putBoolean(SHOW_CHART, false);
			} else {
				item.setTitle("Itemized");
			}
			
			editor.commit();
			refresh_screen();
		break;
		case R.id.item_add_sync_account:
			add_sync_account();
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
	    	 	
		//Log.w("DHGG","onPrepareOptionsMenu c:"+m_show_chart+" l:"+m_show_log);
		if ( m_show_chart ) {
			menu.findItem(R.id.item_show_chart).setTitle("Hide Chart");
			menu.findItem(R.id.item_show_log).setTitle("Show Itemized");

		} else {
			menu.findItem(R.id.item_show_chart).setTitle("Chart");
		    
			if ( !m_show_log ) {
				menu.findItem(R.id.item_show_log).setTitle("Show Itemized");
			} else {
				menu.findItem(R.id.item_show_log).setTitle("Summary");
			}
		}
		
	    // get account name from the shared pref
		SharedPreferences settings = getSharedPreferences(PREF_KEY_ACCOUNT_NAME,Context.MODE_PRIVATE);
		String accountName = settings.getString(PREF_KEY_ACCOUNT_NAME, null);	
		//Log.i("DHGG","onCreateOptions toggling update sync visibility for: "+accountName);
		if (accountName == null) {
			menu.findItem(R.id.item_add_sync_account).setEnabled(true);
		}
		else {
			menu.findItem(R.id.item_add_sync_account).setEnabled(false);
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
		
		SharedPreferences ui_prefs = getSharedPreferences( UI_PREFS, 0);
		m_show_chart = ui_prefs.getBoolean(SHOW_CHART, false);
		m_show_log = ui_prefs.getBoolean(SHOW_LOG, false);
		
		refresh_screen();
		super.onResume();
	}
	
	private void refresh_amount_screen() {
		SharedPreferences ui_prefs = getSharedPreferences( UI_PREFS, 0);
		m_show_chart = ui_prefs.getBoolean(SHOW_CHART, false);
		String hist_pref = ui_prefs.getString( SHOW_HIST_PREFS, SHOW_HIST_PREF_ALL );
		//Log.i("DHGG","refresh_amount_screen hist_pref:"+hist_pref);
		
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
		    // pass account to the activity 
			SharedPreferences account_settings = getSharedPreferences(PREF_KEY_ACCOUNT_NAME,Context.MODE_PRIVATE);
			String accountName = account_settings.getString(PREF_KEY_ACCOUNT_NAME, null);	
			if (accountName == null) 
			{
	        	list_fragment.refresh_screen( normal_data_arr, m_show_chart, null);
			}
			else
			{
			    mCredential.setSelectedAccountName(accountName);
	        	list_fragment.refresh_screen( normal_data_arr, m_show_chart, mCredential);
			}
    	}

    	AppChartFragment chart_fragment = (AppChartFragment) getSupportFragmentManager().findFragmentByTag("my_chart_fragment");    	
    	if ( chart_fragment != null ) {
        	chart_fragment.refresh_screen( normal_data_arr );        	
    	}

    	update_screen_view();
    	
    	int data_returned_size = data_arr.length;
    	show_toast(hist_pref, data_returned_size);
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

    private void refresh_screen()
    {	
		//Log.w("DHGG","refresh_screen l:"+m_show_log);
		if (m_show_log) {
			refresh_audit_screen();
		} else {
			refresh_amount_screen();
		}
    }

	private void send_start_broadcast() {
		set_update_flag(false);
		
	    // Send start message
		Intent intent=new Intent( this, Broadcast_receiver_handler.class);
		intent.setAction("dhgg.app.usage.monitor.start");
		sendBroadcast(intent);		
	}

	public void sendData() 
	{
		// Get data to send
		ArrayList<Data_value> data = m_db_handler.getData( SHOW_HIST_PREF_ALL, "" );
		
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

	private void set_hist_prefs( String pref ) {
		// Update the saved preference.
		SharedPreferences.Editor editor = getSharedPreferences(UI_PREFS, 0).edit();
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
    	else if ( hist_pref.equals( SHOW_HIST_PREF_ALL ) )
    	{
    		show_toast = true;
    		toast_msg = "Showing usage for all history.";
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
}
