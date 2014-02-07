package com.dhgg.cloudbackend;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/*
 * Implement AbstractAccountAuthenticator and stub out all
 * of its methods
 */
public class Authenticator extends AbstractAccountAuthenticator {

    private static final String PREF_KEY_ACCOUNT_NAME = "PREF_KEY_ACCOUNT_NAME";
	public static Context mContext;

    // Simple constructor
    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }

    // Editing properties is not supported
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse r, String s) {
        throw new UnsupportedOperationException();
    }

    // Don't add additional accounts
    @Override
    public Bundle addAccount(
            AccountAuthenticatorResponse r,
            String s,
            String s2,
            String[] strings,
            Bundle bundle) throws NetworkErrorException {
    	//Log.i("DHGG","Authenticator::addAccount");
        return null;
    }

    // Ignore attempts to confirm credentials
    @Override
    public Bundle confirmCredentials(
            AccountAuthenticatorResponse r,
            Account account,
            Bundle bundle) throws NetworkErrorException {
        return null;
    }

    // Getting an authentication token is not supported
    @Override
    public Bundle getAuthToken(
            AccountAuthenticatorResponse r,
            Account account,
            String s,
            Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    // Getting a label for the auth token is not supported
    @Override
    public String getAuthTokenLabel(String s) {
        throw new UnsupportedOperationException();
    }

    // Updating user credentials is not supported
    @Override
    public Bundle updateCredentials(
            AccountAuthenticatorResponse r,
            Account account,
            String s, Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    // Checking features for the account is not supported
    @Override
    public Bundle hasFeatures(
        AccountAuthenticatorResponse r,
        Account account, String[] strings) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Bundle getAccountRemovalAllowed(
            AccountAuthenticatorResponse response, Account account)
            throws NetworkErrorException {
        Bundle result = super.getAccountRemovalAllowed(response, account);

        if (result != null && result.containsKey(AccountManager.KEY_BOOLEAN_RESULT)
                && !result.containsKey(AccountManager.KEY_INTENT)) {
            final boolean removalAllowed = result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT);

            if (removalAllowed) {
		        SharedPreferences.Editor e = mContext.getSharedPreferences(
		            PREF_KEY_ACCOUNT_NAME,
		            Context.MODE_PRIVATE).edit();
		        e.putString(PREF_KEY_ACCOUNT_NAME, null);
		        e.commit();
            }
        }
    	
	    return super.getAccountRemovalAllowed(response, account);
    }
}