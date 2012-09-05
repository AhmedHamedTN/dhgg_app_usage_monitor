package com.example.danshelloworld;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

public class Service_handler extends Service {
	
	public IBinder onBind(Intent intent) {
	return null;
	}
	
	public void onCreate() {
        System.out.println("onCreate");
	}
	
	public void onDestroy() {
        System.out.println("onDestroy");
	}
	
	public void onStart(Intent intent, int startid) {

        System.out.println("Service_handler onStart");
        
	
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
	        long time_on = System.currentTimeMillis();
	        if (name.length() > 0)
	        {
	        	db_handler.addData(name, (int) time_on);
	        }
        }
	}
}
