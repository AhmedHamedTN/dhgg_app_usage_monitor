package com.example.danshelloworld;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
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
        String shortName = am.getRunningTasks(1).get(0).topActivity.getShortClassName();
        System.out.println("onStart shortName of top app "+shortName);
        
        // Save to the database
	}
}
