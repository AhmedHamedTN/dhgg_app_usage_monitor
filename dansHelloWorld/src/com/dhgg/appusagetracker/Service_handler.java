package com.dhgg.appusagetracker;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

public class Service_handler extends Service 
{
	
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	public void onCreate() 
	{
	}
	
	public void onDestroy() 
	{
	}
	
	public void onStart(Intent intent, int startid) 
	{
        logAppInfo();

        this.onDestroy();
	}
        
	public void logAppInfo()
	{
        // Get info about running application
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();

        if ( !list.isEmpty() )
        {
        	PackageManager pm = this.getPackageManager();
        	ActivityManager.RunningAppProcessInfo info = 
        	    (ActivityManager.RunningAppProcessInfo)(list.get(0));
        	
        	String name = "";
        	try 
        	{
        		CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
        		name = c.toString();
        	} 
        	catch (Exception e)
        	{
        	}
	        
	        // Get database handler
	        Db_handler db_handler = new Db_handler( this );
	        
	        // Save to the database
	        if (name.length() > 0)
	        {
	        	db_handler.update_or_add(name, name);
	        }
        }
	}
}
