package com.example.danshelloworld;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class Broadcast_receiver_handler extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		System.out.println("onReceive: " + action);
		if (action.equals(Intent.ACTION_SCREEN_ON)) 
		{
			save_to_db( context,"screen_on", "screen_on" );
			SetAlarm(context);
		} else if (action.equals(Intent.ACTION_SCREEN_OFF)) 
		{
			save_to_db( context, "screen_off", "screen_off" );
			CancelAlarm(context);
		} else if (action.equals("com.blah.blah.somemessage")) 
		{
			logAppInfo(context);
		}
	}

	public void SetAlarm(Context context) 
	{
		System.out.println("SetAlarm");
		CancelAlarm(context);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent("com.blah.blah.somemessage"), 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),1000 * 5, pi); 
	}

	public void CancelAlarm(Context context) 
	{
		System.out.println("CancelAlarm");
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, new Intent("com.blah.blah.somemessage"), 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	public void save_to_db(Context context, String name, String process_name) 
	{	
		if (name.length() <=0 || context == null || name.equals("dansHelloWorld"))
		{
			return;
		}
		
		Db_handler db_handler = new Db_handler(context);
		System.out.println( "Adding:"+ name);
		db_handler.update_or_add( name, process_name );
	}

	public void logAppInfo(Context context) 
	{
		// Get info about running application
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();

		int num_tasks = 1;
		List<ActivityManager.RecentTaskInfo> list = am.getRecentTasks(
				num_tasks, ActivityManager.RECENT_IGNORE_UNAVAILABLE);

		String last_task = "";
		for (int i = 0; i < num_tasks; i++) 
		{
			ActivityManager.RecentTaskInfo info = (ActivityManager.RecentTaskInfo) (list.get(i));
			Intent lastIntent = info.baseIntent;

			if (lastIntent.getComponent().getClassName() != null) 
			{
				last_task = lastIntent.getComponent().toString();
			}
		}

		List<ActivityManager.RunningAppProcessInfo> list2 = am.getRunningAppProcesses(); 
		int num_processes = list2.size(); 
		for (int i = 0; i < num_processes; i++) 
		{ 
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(list2.get(i));

			String process_name = info.processName;
			if ( process_name != "" &&
				 last_task != "" && 
			     last_task.indexOf( process_name ) > 0 )
			{
				String name = "";
				try { 
					CharSequence c = pm.getApplicationLabel(
							         pm.getApplicationInfo(
							             info.processName, 
							             PackageManager.GET_META_DATA )); 
					name = c.toString(); 
				} catch(Exception e) { }
				
				save_to_db( context, name, info.processName );
				break;
			}
		}
	}

}
