package com.dhgg.cloudbackend;

import android.content.Context;
import android.content.SharedPreferences;

import com.dhgg.appusagemonitor.Consts;
import com.dhgg.appusagemonitor.MainActivity;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Created by dhan on 11/12/14.
 */
public class SyncAccount {
    private GoogleAccountCredential mCredential;
    private Context m_context;

    public SyncAccount(Context context) {
        m_context = context;
        init();
    }

    private void init() {
	    // get account name from the shared pref
		SharedPreferences settings = m_context.getSharedPreferences(MainActivity.PREF_KEY_ACCOUNT_NAME, Context.MODE_PRIVATE);
		String accountName = settings.getString(MainActivity.PREF_KEY_ACCOUNT_NAME, null);

		mCredential = GoogleAccountCredential.usingAudience(m_context, Consts.AUTH_AUDIENCE);
		if (accountName != null) {
		    SyncUtils.CreateSyncAccount(m_context);
	    }
	}

    public GoogleAccountCredential getCredential() {
        return mCredential;
    }
}
