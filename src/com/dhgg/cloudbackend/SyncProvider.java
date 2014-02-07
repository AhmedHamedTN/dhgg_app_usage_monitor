package com.dhgg.cloudbackend;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/*
 * Define an implementation of ContentProvider.
 */
public class SyncProvider extends ContentProvider {
	final static String TAG = "DHGG";
    final static String CONTENT_AUTHORITY = "com.dhgg.appusagemonitor.provider";

    /*
     * Return true if the provider loaded correctly.
     */
    @Override
    public boolean onCreate() {
    	//Log.i(TAG, "SyncProvider::onCreate");
        return true;
    }
    /*
     * Return MIME type string.
     */
    public String getType(Uri uri) {
    	//Log.i(TAG, "SyncProvider::getType");
        return new String();
    }
    /*
     * query() always returns no results
     *
     */
    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
    	//Log.i(TAG, "SyncProvider::query");
        return null;
    }
    /*
     * insert() always returns null (no URI)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
    	//Log.i(TAG, "SyncProvider::insert");
        return null;
    }
    /*
     * delete() return number of rows affected
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
    	//Log.i(TAG, "SyncProvider::delete");
        return 0;
    }
    /*
     * update() always returns "no rows affected" (0)
     */
    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
    	//Log.i(TAG, "SyncProvider::update");
        return 0;
    }
    
}