package com.dhgg.appusagemonitor_tests;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.dhgg.appusagemonitor.DbHandler;
import com.dhgg.appusagemonitor.TimeLog;

import java.util.ArrayList;
import java.util.Map;

/**
 * This test class tests DbHandler.
 * Every public method is run and tested for good behavior.
 */
public class DbHandlerTest extends AndroidTestCase {
    private DbHandler m_db_handler;

    @Override
    public void setUp() {
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        m_db_handler = new DbHandler(context);
    }

    @Override
    public void tearDown() throws Exception {
        m_db_handler.close();
        super.tearDown();
    }

    public void testDoStuff() {
        // test db. not connected to standard db.

        String[] dbList = mContext.databaseList();
        int numDb = dbList.length;
        for (int i = 0; i < numDb; i++) {
            Log.d("DHGG", dbList[i]);
        }
    }


    public void testBlankDb() {

        // Setup
        String name = "app_name";
        String process_name = "process_name";
        int expectedNumLogs = 0;

        // Run
        String hist_pref = "s_h_p_today";
        ArrayList<TimeLog> timeLogs = m_db_handler.getTimeLog(hist_pref, name);
        int numLogs = timeLogs.size();

        // Verify
        assertEquals(expectedNumLogs, numLogs);
    }

    /**
     * Tests adding enough data that the database will return 1 data point.
     * This assumes that the database is in a clean/empty state.
     * If the test fails, an assertion will cause the program to abort.
     */
    public void test1Entry() {

        // Setup
        String name = "app_name";
        String process_name = "process_name";
        int expectedNumLogs = 1;

        // Run
        m_db_handler.update_or_add(name, process_name);
        sleepForSeconds(5);
        m_db_handler.update_or_add(name, process_name);

        // Verify
        String hist_pref = "s_h_p_today";
        ArrayList<TimeLog> timeLogs = m_db_handler.getTimeLog(hist_pref, name);
        int numLogs = timeLogs.size();
        assertEquals(expectedNumLogs, numLogs);
    }


        // Map<String,String> app_to_process = m_db_handler.get_app_to_process_map(componentName);
        /*
        public Map<String,String> get_app_to_process_map(String componentName)
        public ArrayList<DataValue> getData( String hist_pref, String input_app_name )
        public Point[] getHistoricalData( String app_name )
        public ArrayList<TimeLog> getTimeLog( String hist_pref, String input_app_name ) {
        public ArrayList<TimeLog> getTimeLogFromTime(long lastAppDate, String lastAppName)
        public void onCreate(SQLiteDatabase db)	{
        public void onUpgrade(SQLiteDatabase db, int old_version, int new_version)
        */

    /**
     * A helper function used to sleep for a given number of seconds.
     *
     *  @param  seconds Number of seconds to sleep for.
     *  @return void    Nothing to return.
     */
    private void sleepForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}


