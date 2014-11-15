package com.dhgg.appusagemonitor;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by dhan on 11/10/14.
 * Class that handles the UsageStatsMonitor.
 */
public class UsageStatsHandler {
    private String DEFAULT_CAT = "UsageStatsHandler: ";
    private Context m_context;
    private DbHandler m_dbHandler;
    private boolean m_isAbleToRun = false;
    private boolean m_hasPermission = false;
    private UsageStatsManager m_usageStatsManager;


    ///////////////////////////////////////////////////////
    // Public functions
    ///////////////////////////////////////////////////////
    public UsageStatsHandler(Context context, DbHandler dbHandler) {
        String logCat = "UsageStatsHandler";
        m_context = context;
        m_dbHandler = dbHandler;

        // Update active flag based on sdk version
        try {
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                m_isAbleToRun = true;
            }
        } catch (Exception e) {
            Log.e(Consts.LOGTAG, logCat + "Couldn't get api version");
        }

        init();
    }

    public ArrayList<DataValue> getAccumulatedUsage(String dateRange) {
        String logCat = "UsageStatsHandler::getAccumulatedUsage: ";
        if (!m_isAbleToRun) {
            return m_dbHandler.getData(dateRange, "");
        }

        long end = System.currentTimeMillis();

        long start = 0;
        if (dateRange.equals(MainActivity.SHOW_HIST_PREF_24_H)) {
            start = end - 24 * 60 * 60 * 1000;
        } else if (dateRange.equals(MainActivity.SHOW_HIST_PREF_TODAY)) {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            start = c.getTimeInMillis();
        }

        //Log.i(Consts.LOGTAG, logCat + " start:" + start + " end:" + end);

        List<UsageStats> myStats = m_usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end);
        if (myStats.size() == 0) {
            m_hasPermission = false;
        } else {
            m_hasPermission = true;
        }

        Map<String, DataValue> mp_obj=new HashMap<String, DataValue>();
        for (int i = 0; i < myStats.size(); ++i) {
            String packageName = myStats.get(i).getPackageName();
            String appName = getAppName(packageName);
            int value = (int) (myStats.get(i).getTotalTimeInForeground()/1000);

            // store name + object value map
            if (mp_obj.containsKey(appName)) {
                DataValue dv = (mp_obj.get(appName));
                mp_obj.put(appName, new DataValue(appName, packageName, dv.value + value));
            } else {
                DataValue dv = new DataValue(appName, packageName, value);
                mp_obj.put(appName, dv);
            }

        }

        ArrayList <DataValue> data = new ArrayList<DataValue>();
        for (Map.Entry<String, DataValue> entry : mp_obj.entrySet()) {
            data.add(0,entry.getValue());
        }

        Collections.sort(data, new DataValueComparator());
        return data;
    }

    public Point[] getDataForOneApp(String appName) {
        String logCat = "UsageStatsHandler::getData: ";
        if (!m_isAbleToRun) {
            return m_dbHandler.getHistoricalData(appName);
        }
        Log.i(Consts.LOGTAG, logCat + "looking for "+appName);

        long end = System.currentTimeMillis();
        long start = 0;
        List<UsageStats> myStats = m_usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
        if (myStats.size() == 0) {
            m_hasPermission = false;
        } else {
            m_hasPermission = true;
        }

        int today = getTodayAsYYYYMMDD();
        int yest = getYestAsYYYYMMDD();
        int tomorrow = getTomorrowAsYYYYMMDD();
        boolean todayDone = false, yestDone = false, tomorrowDone = false;

        String goodPackageName = "";
        ArrayList <Point> data = new ArrayList<Point>();
        for (int i = 0; i < myStats.size(); ++i) {
            String packageName = myStats.get(i).getPackageName();
            if (goodPackageName.isEmpty()) {
                String thisAppName = getAppName(packageName);
                if (appName.equals(thisAppName)) {
                    goodPackageName = packageName;
                } else {
                    continue;
                }
            }

            if (packageName.equals(goodPackageName)) {
                long datetime = myStats.get(i).getLastTimeUsed();
                int yyyymmdd = getYYYYMMDDFromMs(datetime);
                int value = (int) (myStats.get(i).getTotalTimeInForeground()/1000);
                data.add(new Point(yyyymmdd, value));

                if (yyyymmdd == today) {todayDone = true;}
                if (yyyymmdd == yest) {yestDone = true;}
                if (yyyymmdd == tomorrow) {tomorrowDone = true;}
            }
        }

        if (!todayDone) {data.add(new Point(today, 0));}
        if (!yestDone) {data.add(new Point(yest, 0));}
        if (!tomorrowDone) {data.add(new Point(tomorrow, 0));}

        Point[] point_arr = data.toArray(new Point[data.size()]);
        return point_arr;
    }

    public boolean getIsActive() {
        return m_isAbleToRun;
    }

    public boolean needsPermission() {
        return (m_isAbleToRun && !m_hasPermission);
    }

    public void openPermissionsPage() {
        String logCat = "UsageStatsHandler::openPermissionsPage: ";
        Log.w(Consts.LOGTAG, logCat + "start");

        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        m_context.startActivity(intent);
    }

    public void setPermission() {
        String logCat = "UsageStatsHandler::setPermission: ";
        if (!m_isAbleToRun) {
            return;
        }

        long end = System.currentTimeMillis();
        long start = end - 24 * 60 * 60 * 3600;

        m_usageStatsManager = (UsageStatsManager) m_context.getSystemService("usagestats");
        //Log.i(Consts.LOGTAG, logCat + " start:" + start + " end:" + end);

        SharedPreferences settings = m_context.getSharedPreferences(MainActivity.UI_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        List<UsageStats> myStats = m_usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
        if (myStats.size() == 0) {
            m_hasPermission = false;
            editor.putBoolean(MainActivity.ASK_FOR_USAGE_PERMISSION, true);
        } else {
            m_hasPermission = true;
            editor.putBoolean(MainActivity.ASK_FOR_USAGE_PERMISSION, false);
        }
        editor.commit();
    }


    ///////////////////////////////////////////////////////
    // Functions to calculate app usage statistics
    // This functionality is replaced by UsageStatsManager,
    // but is kept here for backwards compatibility
    ///////////////////////////////////////////////////////
    public void onPause() {
        if (!m_isAbleToRun) {
            m_dbHandler.update_or_add("App Usage Monitor", "com.dhgg.appusagemonitor");
            m_dbHandler.update_or_add("screen_off", "screen_off");
        }
    }

    public void onResume() {
        if (!m_isAbleToRun) {
            m_dbHandler.update_or_add("screen_on", "screen_on");
            m_dbHandler.update_or_add("App Usage Monitor", "com.dhgg.appusagemonitor");

            // Send start message
            Intent intent = new Intent(m_context, BroadcastReceiverHandler.class);
            intent.setAction("dhgg.app.usage.monitor.start");
            m_context.sendBroadcast(intent);
        }
    }


    ///////////////////////////////////////////////////////
    // Start of private class functions.
    ///////////////////////////////////////////////////////
    private void init() {
        setPermission();
    }

    private String getAppName(String packageName) {
        String logCat = "UsageStatsHandler::getAppName: ";
        String appName = "";

        // From the task, find the application name and process name
        Map<String,String> app_to_process = m_dbHandler.get_app_to_process_map(packageName);
        if (app_to_process.isEmpty()) {
            updateAppToProcessCache();
        }

        app_to_process = m_dbHandler.get_app_to_process_map(packageName);
        Iterator<Map.Entry<String, String>> it = app_to_process.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
            //Log.w(Consts.LOGTAG,logCat + "Used cache to get n:"+pairs.getKey() + " => " + pairs.getValue());

            appName = pairs.getKey();
            break;
        }

        return appName;
    }

    private int getYYYYMMDDFromMs(long timeInMillis) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(timeInMillis);

        return gcal.get( Calendar.YEAR ) * 10000 +
               (gcal.get( Calendar.MONTH ) + 1 )  * 100 +
               gcal.get( Calendar.DATE ) ;
    }

    private int getTodayAsYYYYMMDD() {
        GregorianCalendar gcalendar = new GregorianCalendar();
        return gcalendar.get(Calendar.YEAR) * 10000 +
                (gcalendar.get(Calendar.MONTH) + 1) * 100 +
                gcalendar.get(Calendar.DATE);
    }

    private int getYestAsYYYYMMDD() {
        GregorianCalendar gcalendar = new GregorianCalendar();
        gcalendar.add(Calendar.DATE, -1);
        return gcalendar.get(Calendar.YEAR) * 10000 +
                (gcalendar.get(Calendar.MONTH) + 1) * 100 +
                gcalendar.get(Calendar.DATE);
    }

    private int getTomorrowAsYYYYMMDD() {
        GregorianCalendar gcalendar = new GregorianCalendar();
        gcalendar.add(Calendar.DATE, +1);
        return gcalendar.get(Calendar.YEAR) * 10000 +
                (gcalendar.get(Calendar.MONTH) + 1) * 100 +
                gcalendar.get(Calendar.DATE);
    }

    private void updateAppToProcessCache(){
        ActivityManager am=(ActivityManager)m_context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm=m_context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo>list2=am.getRunningAppProcesses();
        int num_processes=list2.size();
        for(int i=0;i<num_processes;i++){
            ActivityManager.RunningAppProcessInfo info=list2.get(i);

            String processName=info.processName;
            try{
                CharSequence c = pm.getApplicationLabel(
                        pm.getApplicationInfo(
                                processName,
                                PackageManager.GET_META_DATA));

                String appName=c.toString();

                // save this to db for future casting
                m_dbHandler.update_or_add_to_mapping_table(appName, processName);
            }catch(Exception e){}
        }
    }


    ///////////////////////////////////////////////////////
    // End
    ///////////////////////////////////////////////////////
}

