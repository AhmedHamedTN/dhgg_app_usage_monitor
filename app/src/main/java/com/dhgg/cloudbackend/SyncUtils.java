package com.dhgg.cloudbackend;

/*
 * 2014 - Daniel Han.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import com.dhgg.appusagemonitor.Consts;

import com.dhgg.appusagemonitor.MainActivity;
import com.dhgg.cloudbackend.AuthenticatorService;

/**
 * Static helper methods for working with the sync framework.
 */
public class SyncUtils {
    private static final long SYNC_FREQUENCY = 60 * 60 * 2;  // 120 minutes (in seconds)
    private static final String CONTENT_AUTHORITY = SyncProvider.CONTENT_AUTHORITY;

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */
    public static void CreateSyncAccount(Context context) {
        //Log.w(Consts.LOGTAG,"CreateSyncAccount::CreateSyncAccount - start");

        // Check if we've already set up an account
        boolean syncAccountCreated = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(MainActivity.SYNC_ACCOUNT_CREATED, false);

        if (syncAccountCreated) {
            // return;
        }

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        AuthenticatorService.SetContext(context);
        Account account = AuthenticatorService.GetAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        boolean newAccountAdded = accountManager.addAccountExplicitly(account, null, null);

        if (newAccountAdded) {
            //Log.i(TAG,"SyncUtils::CreateSyncAccount Updating sync settings");
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);
        }

        // Do a sync now (useful for testing).
        // In general, it is not immediately needed.
        if (true) {
            TriggerRefresh();
        }

        // Save preference that we do not need to create a new sync account.
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(MainActivity.SYNC_ACCOUNT_CREATED, true)
                .commit();
    }

    /**
     * Helper method to trigger an immediate sync ("refresh").
     *
     * <p>This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     *
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    public static void TriggerRefresh() {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                AuthenticatorService.GetAccount(),     // Sync account
                CONTENT_AUTHORITY,                     // Content authority
                b);                                    // Extras
    }
}
