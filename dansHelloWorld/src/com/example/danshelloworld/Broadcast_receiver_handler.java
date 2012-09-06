package com.example.danshelloworld;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Broadcast_receiver_handler extends BroadcastReceiver 
{	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
        String action = intent.getAction();
        System.out.println("onReceive: "+action);
        if(action.equals(Intent.ACTION_SCREEN_ON))
        {
        	System.out.println("Screen on !!!");
        	
        	SetAlarm(context);
        }
        else if(action.equals(Intent.ACTION_SCREEN_OFF))
        {
        	System.out.println("Screen off !!!");
        	
        	CancelAlarm(context);
        }	
        else if(action.equals("com.blah.blah.somemessage"))
        {
	    	// Start service to check what the current app is
	      	Intent myIntent = new Intent(context, Service_handler.class);
	       	context.startService(myIntent);
        }
	 }
	
	 public void SetAlarm(Context context)
     {
		System.out.println("SetAlarm");

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent("com.blah.blah.somemessage"), 0);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pi); // Millisec * Second 
     }

     public void CancelAlarm(Context context)
     {
 		System.out.println("CancelAlarm");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, new Intent("com.blah.blah.somemessage"), 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
     }

}
