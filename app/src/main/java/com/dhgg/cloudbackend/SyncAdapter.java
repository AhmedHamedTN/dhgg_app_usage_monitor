package com.dhgg.cloudbackend;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageInsertRequest;
import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageRecord;
import com.appspot.appusagemonitor.appusagemonitor.model.AppusagemonitorApiMessagesAppUsageResponseMessage;
import com.dhgg.appusagemonitor.CloudBackend;
import com.dhgg.appusagemonitor.Consts;
import com.dhgg.appusagemonitor.DbHandler;
import com.dhgg.appusagemonitor.TimeLog;
import com.dhgg.appusagemonitor.UsageStatsHandler;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    // Define variables to contain a content resolver instance
    ContentResolver mContentResolver;
    Context mContext;

    // Variable for connecting to cloud endpoints api
	private CloudBackend mCloudBackend;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */

        mContentResolver = context.getContentResolver();
        mContext = context;
    }
    
    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */

        mContentResolver = context.getContentResolver();
        mContext = context;
    }

	@Override
	public void onPerformSync(Account arg0, Bundle arg1, String arg2,
			ContentProviderClient arg3, SyncResult arg4) {
		//Log.w(Consts.LOGTAG,"onPerformSync name:"+arg0.name+" type:"+arg0.type);
		if (!authenticate(arg0.name))
		{
			//Log.w(Consts.LOGTAG,"SyncAdapter::onPerformSync authenticate failed");
			return;
		}

        AppusagemonitorApiMessagesAppUsageInsertRequest request = makeRequest();
        sendRequest(request);
	}

    private AppusagemonitorApiMessagesAppUsageInsertRequest makeRequest() {
		// Check preferences to see last update.
		SharedPreferences settings = mContext.getSharedPreferences("CLOUD_INFO",0);
		long last_app_date = settings.getLong("LAST_APP_DATE", 0);
		String last_app_name = settings.getString("LAST_APP_NAME", "");
		// Log.w(Consts.LOGTAG,"SyncAdapter::makeRequest - saved last_app date:"+last_app_date+" name:"+last_app_name);

		// Use latest data.
		DbHandler dbHandler = new DbHandler(mContext);
        UsageStatsHandler usageStatsHandler = new UsageStatsHandler(mContext, dbHandler);

		ArrayList<TimeLog> data = usageStatsHandler.getTimeLogFromTime(last_app_date, last_app_name);
		TimeLog[] data_arr = data.toArray(new TimeLog[data.size()]);
		
		// Set up items to send to cloud backend.
	    List<AppusagemonitorApiMessagesAppUsageRecord> items = 
	        new ArrayList<AppusagemonitorApiMessagesAppUsageRecord>();

        // Get device name - send with each update
        String deviceName = getDeviceName();

	    // Package each item.
		int numLogs = data_arr.length;
		for ( int i = 0; i < numLogs; i++ ) {
			AppusagemonitorApiMessagesAppUsageRecord record = new AppusagemonitorApiMessagesAppUsageRecord();
            /*
     	    Log.w(Consts.LOGTAG, "next item :" + data_arr[i].description +
                  " d:" + deviceName +
                  " s:" + data_arr[i].start_time +
                  " e:"+data_arr[i].end_time);
            */

			record.setAppName(data_arr[i].description);
			record.setAppDate(data_arr[i].start_time);     // date
			record.setAppDuration(data_arr[i].end_time);   // duration
			record.setPackageName(data_arr[i].process_name);
            record.setPhoneName(deviceName);

			items.add(record);
		}
			

		// Create the request
        AppusagemonitorApiMessagesAppUsageInsertRequest request = 
            new AppusagemonitorApiMessagesAppUsageInsertRequest();
        request.setItems(items);
        //Log.i(Consts.LOGTAG,"sending "+items.size()+" items");
        
        return request;
	}
	
	private void sendRequest(AppusagemonitorApiMessagesAppUsageInsertRequest request) {

        if (request.getItems().isEmpty()) {
            //Log.i(Consts.LOGTAG,"sendRequest: No items to send. exiting now.");
            return;
        }

        try {
        	// insert data
			AppusagemonitorApiMessagesAppUsageResponseMessage result = mCloudBackend.insert(request);

            // TODO: check result for good response.

			// Save this to shared pref
			long appDate = result.getAppDate();
			String appName = result.getAppName();
			Log.w(Consts.LOGTAG, "Returned data:"+
                    "Last app date:"+appDate+
                    " name:"+appName+
                    " phoneName:" + result.getPhoneName()
            );
			
			if (appDate > 0)
			{
				SharedPreferences updateSettings = mContext.getSharedPreferences("CLOUD_INFO",0);
				SharedPreferences.Editor editor = updateSettings.edit();
				editor.putLong("LAST_APP_DATE", appDate);
				editor.putString("LAST_APP_NAME", appName);
				editor.commit();
			}
        } catch (IOException e) {
            // 
			//Log.w(Consts.LOGTAG,"syncData onError:"+e.toString());
        }
	}

	/* 
	 *  Helper used to do google account authentication.
	 *  Should be moved to provider.
	 */
	private boolean authenticate(String accountName) {
	    // create credential
	    GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(mContext, Consts.AUTH_AUDIENCE);
		mCloudBackend = new CloudBackend();
	    mCloudBackend.setCredential(credential);
	
		//Log.i(Consts.LOGTAG,"SyncAdapter::authenticate "+accountName);
		credential.setSelectedAccountName(accountName);
	    return true; 
	}


    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
