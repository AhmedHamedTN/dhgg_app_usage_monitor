package com.dhgg.appusagemonitor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

public class BroadcastReceiverHandler extends BroadcastReceiver
{
	private DbHandler m_db_handler;
    private String LOGTAG = "DHGG";
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
        String logCategory = "BroadcastReceiverHandler::onReceive: ";
		m_db_handler = new DbHandler(context);
		
		String action = intent.getAction();
		//Log.w(LOGTAG, logCategory + "--- action --- " + action);
		if ( action.equals("android.intent.action.BOOT_COMPLETED") ||
		     action.equals("android.intent.action.ACTION_SHUTDOWN"))
		{
			save_to_db( context, "screen_off", "screen_off");
		}
	
		PowerManager pManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (pManager.isScreenOn())
		{
			if (action.equals("dhgg.app.usage.monitor.stop")) 
			{
				logAppInfo(context);
				CancelAlarm(context);
			}
			// for all other messages, tell broadcast receiver to 
			// do updates
			else 
			{
				logAppInfo(context);
				SetAlarm(context,3);
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
				SetAlarm(context,30);
			}
		}
	}

	private void SetAlarm(Context context, int seconds)
	{
        String logCategory = "BroadcastReceiverHandler::SetAlarm: ";

		//CancelAlarm(context);
        //Log.w(LOGTAG, logCategory + "s:"+seconds);

		Intent intent=new Intent( context, BroadcastReceiverHandler.class);
		intent.setAction("dhgg.app.usage.monitor.start");
		
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC, System.currentTimeMillis() + (1000*seconds), pi);
	}
	
	private void CancelAlarm(Context context)
	{
        String logCategory = "BroadcastReceiverHandler::CancelAlarm: ";

		Intent intent=new Intent( context, BroadcastReceiverHandler.class);
		intent.setAction("dhgg.app.usage.monitor.start");
		
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
	}

	private void save_to_db(Context context, String name, String process_name)
	{
        String logCategory = "BroadcastReceiverHandler::save_to_db: ";

		//Log.w(LOGTAG,logCategory + "n:"+name+" p:"+process_name);
		if (name.isEmpty() || context == null || name.equals("App Usage Tracker"))
		{
			return;
		}
		
		m_db_handler.update_or_add( name, process_name );
	}

	private void logAppInfo(Context context) {
        String logCategory = "BroadcastReceiverHandler::logAppInfo: ";


		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		int num_tasks = 1;
		List<ActivityManager.RecentTaskInfo> taskList = am.getRecentTasks(num_tasks, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
		if (taskList.size() < 1) {
            Log.w(Consts.LOGTAG,logCategory + "no recent tasks. cannot log App Info");
			return;
		}
		
		String componentName = "";
		ActivityManager.RecentTaskInfo task_info = (ActivityManager.RecentTaskInfo) (taskList.get(0));
		Intent lastIntent = task_info.baseIntent;
		if (lastIntent.getComponent().getClassName() != null) 
		{
			componentName = lastIntent.getComponent().toString();
			if (componentName.isEmpty())
			{
				return;
			}
		}
		
		// From the task, find the application name and process name
	    Map<String,String> app_to_process = m_db_handler.get_app_to_process_map(componentName);
		if (app_to_process.isEmpty())
		{
			PackageManager pm = context.getPackageManager();
			List<ActivityManager.RunningAppProcessInfo> list2 = am.getRunningAppProcesses(); 
			int num_processes = list2.size(); 
			for (int i = 0; i < num_processes; i++) 
			{
				//Log.w(LOGTAG,logCategory + " +i);
				ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(list2.get(i));
	
				String process_name = info.processName;
	
				if ( process_name != "" && componentName.indexOf( process_name ) > 0 )
				{
					String name = "";
					try { 
						CharSequence c = pm.getApplicationLabel(
								         pm.getApplicationInfo(
								             info.processName, 
								             PackageManager.GET_META_DATA )); 
						name = c.toString(); 
					} catch(Exception e) { }
					
	
					//Log.w(LOGTAG,logCategory + "Used ActivityManager to find n:"+name+" => p:"+process_name);
					save_to_db( context, name, info.processName );
					break;
				}
			}
		}
		else
		{
			Iterator<Entry<String, String>> it = app_to_process.entrySet().iterator();
		    while (it.hasNext()) {
		        Entry<String, String> pairs = (Entry<String, String>)it.next();
		        //Log.w(LOGTAG,logCategory + "Used cache to get n:"+pairs.getKey() + " => " + pairs.getValue());

		        save_to_db(context, pairs.getKey(), pairs.getValue());
		        break;
		    }
		}
	}
	
	private boolean on_phone( Context context)
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
