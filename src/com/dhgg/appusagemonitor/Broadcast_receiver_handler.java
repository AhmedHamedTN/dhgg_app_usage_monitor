package com.dhgg.appusagemonitor;

import java.util.List;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Broadcast_receiver_handler extends BroadcastReceiver 
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		String action = intent.getAction();
		//Log.w("DHGG:","--- action --- "+action);
		if ( action.equals("android.intent.action.BOOT_COMPLETED"))
		{
			save_to_db( context, "screen_off", "screen_off");
		}

		if ( action.equals("android.intent.action.ACTION_SHUTDOWN"))
		{
			save_to_db( context, "screen_off", "screen_off");
		}
	
		PowerManager pManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (pManager.isScreenOn())
		{
			int seconds = 3;
			if (action.equals("dhgg.app.usage.monitor.stop")) 
			{
				logAppInfo(context);
				CancelAlarm(context);
			}
			// for all other messages, tell broadcast receiver to 
			// do updates
			else // if (action.equals("dhgg.app.usage.monitor.stop")) 
			{
				logAppInfo(context);
				SetAlarm(context,seconds);
			} 
		}
		else
		{
			// Check if we are on the phone
			if ( on_phone( context ) )
			{
				logAppInfo(context);
				SetAlarm(context, 5);
			}
			else 
			{
				save_to_db( context, "screen_off", "screen_off");
				SetAlarm(context,15);
			}
		}
	}

	public void SetAlarm(Context context, int seconds) 
	{
		CancelAlarm(context);

		Intent intent=new Intent( context, Broadcast_receiver_handler.class);
		intent.setAction("dhgg.app.usage.monitor.start");
		
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC, System.currentTimeMillis() + (1000*seconds), pi);
	}
	
	public void CancelAlarm(Context context) 
	{
		Intent intent=new Intent( context, Broadcast_receiver_handler.class);
		intent.setAction("dhgg.app.usage.monitor.start");
		
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
	}

	public void save_to_db(Context context, String name, String process_name) 
	{	
		if (name.length() <=0 || context == null || name.equals("App Usage Tracker"))
		{
			return;
		}
		
		Db_handler db_handler = new Db_handler(context);
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
	
	public boolean on_phone( Context context)
	{
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		int call_state = tm.getCallState();
		if ( call_state == TelephonyManager.CALL_STATE_IDLE )
		{
			return false;
		}
		
		return true;
	}

}
