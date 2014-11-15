package com.dhgg.appusagemonitor;

import java.util.ArrayList;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.Date;

import com.dhgg.cloudbackend.SyncAccount;
import com.dhgg.cloudbackend.SyncUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class MainActivity extends FragmentActivity {

    // Classes that compose this one.
	private static DbHandler m_db_handler;
    private Admob m_admob = null;
    private Util m_util = null;
    private SyncAccount m_syncAccount = null;
    private UsageStatsHandler m_usage_handler;


	// Tags used to save preferences
	public static String UI_PREFS = "ui_prefs";
	public static String SHOW_CHART = "show_chart";
	public static String SHOW_LOG = "show_log";
    public static String ASK_FOR_USAGE_PERMISSION = "ask_for_usage_permission";
	public static String SHOW_HIST_PREFS = "show_hist_prefs";
	public static String SHOW_HIST_PREF_TODAY = "s_h_p_today";
	public static String SHOW_HIST_PREF_24_H = "s_h_p_24h";
	public static String SHOW_HIST_PREF_ALL = "s_h_p_all";
    public static String PREF_KEY_ACCOUNT_NAME = "PREF_KEY_ACCOUNT_NAME";
    public static String SYNC_ACCOUNT_CREATED = "sync_account_created";


    // Member variables
	private boolean m_show_chart = false;
	private boolean m_show_log = false;
    private boolean m_is_permission_dialog_open = false;
    private final int m_max_data_size = 22;
	private static final int REQUEST_ACCOUNT_PICKER = 2;


    ///////////////////////////////////////////////////////
    // Override methods
    ///////////////////////////////////////////////////////
    @Override
    protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.w(Consts.LOGTAG,"MainActivity::onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null)  {
                    // set the picked account name to the Credential
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    m_syncAccount.getCredential().setSelectedAccountName(accountName);

                    // save account name to shared pref
                    SharedPreferences.Editor e = getSharedPreferences(
                            PREF_KEY_ACCOUNT_NAME,
                            Context.MODE_PRIVATE).edit();
                    e.putString(PREF_KEY_ACCOUNT_NAME, accountName);
                    e.commit();

                    // Set up sync account
                    //Log.w(Consts.LOGTAG,"MainActivity::onActivityResult "+requestCode+" accountName: "+accountName);
                    SyncUtils.CreateSyncAccount(this);
                }
                break;
            default:
                break;
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_util = new Util();

        m_syncAccount = new SyncAccount(this);

		m_db_handler = new DbHandler(getApplicationContext());

        setContentView(R.layout.activity_my_first);

	    setup_fragments( savedInstanceState );

        setup_admob_view();

        // Initialize UsageStatsHandler
        m_usage_handler = new UsageStatsHandler(this, m_db_handler);

        long end = new Date().getTime();
        //Log.w(Consts.LOGTAG, "Elapsed Time In onCreate:" + (end-start));
        //Log.w(Consts.LOGTAG, "MainActivity::onCreate done");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// use an inflater to populate the ActionBar with items
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.main_menu, menu);
		
		
		//Log.w(Consts.LOGTAG, "MainActivity::onCreateOptionsMenu c:"+m_show_chart+" l:"+m_show_log);
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

        if (m_usage_handler.getIsActive()) {
            menu.findItem(R.id.item_show_log).setVisible(false);
        }

		return true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        m_admob.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Log.d(Consts.LOGTAG,"MainActivity::onOptionsItemSelected "+item.getItemId());
		SharedPreferences settings = getSharedPreferences(UI_PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		// same as using a normal menu
		switch(item.getItemId()) {
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
			showAccountPicker();
		break;
		}
		return true;
	}

    @Override
	public void onPause() {
		super.onPause();

        m_usage_handler.onPause();

        m_admob.onPause();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
	    getMenuInflater().inflate(R.layout.main_menu, menu);
	    	 	
		//Log.w(Consts.LOGTAG,"MainActivity::onPrepareOptionsMenu c:"+m_show_chart+" l:"+m_show_log);
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
		//Log.i(Consts.LOGTAG,"MainActivity::onCreateOptions toggling update sync visibility for: "+accountName);
		if (accountName == null) {
			menu.findItem(R.id.item_add_sync_account).setEnabled(true);
		}
		else {
			menu.findItem(R.id.item_add_sync_account).setEnabled(false);
		}

        if (m_usage_handler.getIsActive()) {
            menu.findItem(R.id.item_show_log).setVisible(false);
        }

		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onResume() {
        super.onResume();

        String logCategory = "MainActivity::onResume: ";
        //Log.w(Consts.LOGTAG, logCategory + "start");

        m_usage_handler.setPermission();
        if (m_usage_handler.needsPermission()) {
            showPermissionDialog();
            return;
        }
        m_usage_handler.onResume();

		SharedPreferences ui_prefs = getSharedPreferences( UI_PREFS, 0);
		m_show_chart = ui_prefs.getBoolean(SHOW_CHART, false);
		m_show_log = ui_prefs.getBoolean(SHOW_LOG, false);
		
		refresh_screen();

        m_admob.onResume();
	}


    ///////////////////////////////////////////////////////
    // Start of private class functions.
    ///////////////////////////////////////////////////////
    public void showAccountPicker() {
        // check if google services is up to date
        int isGoogleAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (isGoogleAvailable == ConnectionResult.SUCCESS) {
            // let user pick an account
            super.startActivityForResult(
                    m_syncAccount.getCredential().newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER);
        } else {
            String toast_msg = "Google Services is not up to date. Cannot create sync account.";
            Toast toast = Toast.makeText(getApplicationContext(), toast_msg, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void showPermissionDialog() {

        // Prevent this dialog from being created multiple times
        if (m_is_permission_dialog_open) {
            return;
        }

        // Instantiate an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app requires permission to view \"Usage Access\".\n\nClick OK to open Settings.")
                .setTitle("Get permissions");

        // Add buttons (with callbacks)
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                m_is_permission_dialog_open = false;
                m_usage_handler.openPermissionsPage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                m_is_permission_dialog_open = false;
            }
        });

        // Add dismiss listener to update flags on closing
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
             @Override
             public void onDismiss(DialogInterface dialog) {
                 m_is_permission_dialog_open = false;
             }
         });

        // Create the AlertDialog
        m_is_permission_dialog_open = true;
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showToast(String hist_pref, int data_returned_size) {
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

	private void refresh_amount_screen() {
		SharedPreferences ui_prefs = getSharedPreferences( UI_PREFS, 0);
		m_show_chart = ui_prefs.getBoolean(SHOW_CHART, false);
		String hist_pref = ui_prefs.getString( SHOW_HIST_PREFS, SHOW_HIST_PREF_ALL );
		//Log.i(Consts.LOGTAG,"MainActivity::refresh_amount_screen hist_pref:"+hist_pref);

        boolean askForPermission = ui_prefs.getBoolean( ASK_FOR_USAGE_PERMISSION, false );
        if (askForPermission) {
            showPermissionDialog();
            return;
        }

        // Get data to display, possibly, from different sources
        ArrayList<DataValue> data = m_usage_handler.getAccumulatedUsage(hist_pref);

        DataValue[] data_arr = data.toArray(new DataValue[data.size()]);
    	DataValue[] normal_data_arr;
    	if ( m_show_chart ) {	
    		normal_data_arr = m_util.get_data_slices(data_arr, m_max_data_size);
    	} else {
    		normal_data_arr = data_arr;
    	}

    	AppListFragment list_fragment = (AppListFragment) getSupportFragmentManager().findFragmentByTag("my_list_fragment");    	
    	if ( list_fragment != null ) {
		    // pass account to the activity 
			SharedPreferences account_settings = getSharedPreferences(PREF_KEY_ACCOUNT_NAME,Context.MODE_PRIVATE);
			String accountName = account_settings.getString(PREF_KEY_ACCOUNT_NAME, null);	
			if (accountName == null)  {
	        	list_fragment.refresh_screen( normal_data_arr, m_show_chart, null);
			} else {
			    m_syncAccount.getCredential().setSelectedAccountName(accountName);
	        	list_fragment.refresh_screen( normal_data_arr, m_show_chart, m_syncAccount.getCredential());
			}
    	}

    	AppChartFragment chart_fragment = (AppChartFragment) getSupportFragmentManager().findFragmentByTag("my_chart_fragment");    	
    	if ( chart_fragment != null ) {
        	chart_fragment.refresh_screen( normal_data_arr );        	
    	}

    	update_screen_view();
    	
    	int data_returned_size = data_arr.length;
    	showToast(hist_pref, data_returned_size);
    }
	
	private void refresh_audit_screen() {

    	// allow only one type of lookup for now
		String hist_pref = SHOW_HIST_PREF_24_H;

		// Get data to display
		ArrayList<TimeLog> data = m_db_handler.getTimeLog( hist_pref, "" );
		TimeLog[] data_arr = data.toArray(new TimeLog[data.size()]);
		
    	AppListFragment list_fragment = (AppListFragment) getSupportFragmentManager().findFragmentByTag("my_list_fragment");    	
    	if ( list_fragment != null ) {
        	list_fragment.refresh_screen_audit( data_arr );        	
    	}    	

    	update_screen_view();
    	
    	int data_returned_size = data_arr.length;
    	showToast(hist_pref, data_returned_size);
    }

    private void refresh_screen() {
		if (m_show_log && !m_usage_handler.getIsActive()) {
			refresh_audit_screen();
		} else {
			refresh_amount_screen();
		}
    }

	private void sendData() {

        // Get data to display, possibly, from different sources
        ArrayList<DataValue> data = m_usage_handler.getAccumulatedUsage(SHOW_HIST_PREF_ALL);

		String data_to_send = "";
		data_to_send += "App Name   \tTime Spent Using\n";
		for (DataValue dv : data) {
			data_to_send += dv.description + " \t" + m_util.get_time_str(dv.value) + "\n";
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
	
	private void setup_admob_view() {
        Intent intent = getIntent();
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_for_adview);
        m_admob = new Admob(intent, this, layout);
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

	private void update_screen_view() {

		int list_fragment_id = R.id.list_fragment_container;
		int chart_fragment_id = R.id.chart_fragment_container;

        boolean is_landscape = false;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            is_landscape = true;
        }

    	FrameLayout list_layout = (FrameLayout) findViewById( list_fragment_id );
		if ( is_landscape ) {
			list_layout.setLayoutParams( new LinearLayout.LayoutParams( 0, LayoutParams.MATCH_PARENT, 1.0f) );
    	} else {
        	list_layout.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, 1.0f) );
    	}
    	
		FrameLayout chart_layout = (FrameLayout) findViewById( chart_fragment_id );
		if ( m_show_chart )	{
			if ( is_landscape ) {
				chart_layout.setLayoutParams( new LinearLayout.LayoutParams( 0, LayoutParams.MATCH_PARENT, .75f) );
			} else {
				chart_layout.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, 0, .75f) );
			}
    	} else {  
    		chart_layout.setLayoutParams( new LinearLayout.LayoutParams( 0, 0, 0.0f) );
	    }   
	}
}
