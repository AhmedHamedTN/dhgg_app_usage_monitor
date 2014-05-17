package com.dhgg.cloudbackend;

import android.accounts.Account;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

/**
 * A bound Service that instantiates the authenticator
 * when started.
 */
public class AuthenticatorService extends Service {
	
    private static final String TAG = "DHGG";
    private static final String ACCOUNT_TYPE = "com.dhgg.appusagemonitor";
    private Authenticator mAuthenticator;

    private static final String PREF_KEY_ACCOUNT_NAME = "PREF_KEY_ACCOUNT_NAME";
    private static Context mContext;

    public static void SetContext(Context context) {
    	mContext = context;
    }

    /**
     * Obtain a handle to the {@link android.accounts.Account} used for sync in this application.
     *
     * @return Handle to application's account (not guaranteed to resolve unless CreateSyncAccount()
     *         has been called)
     */
    public static Account GetAccount() {
    	//Log.v(TAG, "AuthenticatorService::GetAccount");
    	
    	// if auth enabled, get account name from the shared pref
		SharedPreferences settings = mContext.getSharedPreferences(PREF_KEY_ACCOUNT_NAME,Context.MODE_PRIVATE);
		String accountName = settings.getString(PREF_KEY_ACCOUNT_NAME, null);	

		/*
		if (accountName == null) {
	    	Log.e(TAG, "AuthenticatorService::GetAccount Error - No Account set.");
		} else {
	    	Log.v(TAG, "AuthenticatorService::GetAccount found: " + accountName);
	    }
	    */

        return new Account(accountName, ACCOUNT_TYPE);
    }

    
    // Instance field that stores the authenticator object
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
