package com.dhgg.appusagemonitor_tests;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.dhgg.appusagemonitor.Consts;
import com.dhgg.appusagemonitor.DateHandler;
import com.dhgg.appusagemonitor.DbHandler;
import com.dhgg.appusagemonitor.Point;
import com.dhgg.appusagemonitor.TimeLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * This test class tests DbHandler.
 * Every public method is run and tested for good behavior.
 * Tests  are run in a test context. So we are not modifying the standard db context.
 */
public class DbHandlerTest extends AndroidTestCase {
    private String LOGCAT = "DbHandlerTest::";
    private DbHandler m_dbHandler;
    private DateHandler m_dateHandler;

    //////////////////////////////////////////////////////////////////
    // Standard test case functions
    //////////////////////////////////////////////////////////////////

    @Override
    public void setUp() {
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        m_dbHandler = new DbHandler(context);
        m_dateHandler = new DateHandler();

        Log.w(Consts.LOGTAG, LOGCAT + "setUp "+m_dateHandler.getCurrTimeStr());
    }

    @Override
    public void tearDown() throws Exception {
        m_dbHandler.close();
        super.tearDown();
    }

    //////////////////////////////////////////////////////////////////
    // Test Cases
    //////////////////////////////////////////////////////////////////

    public void testDbIsBlank() {

        // Setup
        String name = "app_name";
        String process_name = "process_name";
        int expectedNumLogs = 0;

        // Run
        String hist_pref = "s_h_p_today";
        ArrayList<TimeLog> timeLogs = m_dbHandler.getTimeLog(hist_pref, name);
        int numLogs = timeLogs.size();

        // Verify
        assertEquals(expectedNumLogs, numLogs);
    }

    public void testAdding1Entry() {

        // Setup
        String app_name = "app_name";
        String process_name = "process_name";
        int expectedNumLogs = 1;
        int runTime = 5;

        // Run
        m_dbHandler.update_or_add(app_name, process_name);
        sleepForSeconds(runTime);
        m_dbHandler.update_or_add(app_name, process_name);

        // Verify 1 entry in db
        String hist_pref = "s_h_p_today";
        ArrayList<TimeLog> timeLogs = m_dbHandler.getTimeLog(hist_pref, app_name);
        int numLogs = timeLogs.size();
        assertEquals(expectedNumLogs, numLogs);

        // Verify time analysis for this row
        TimeLog tl = timeLogs.get(0);
        assertTrue(runTime <=  ((tl.end_time - tl.start_time)/1000.0) );

        // Verify Historical api
        Point[] points = m_dbHandler.getHistoricalData(app_name);
        assertEquals(2, points.length);

        GregorianCalendar gcal = new GregorianCalendar();
        int todayDate =  m_dateHandler.getYYYYMMDD(gcal);
        assertEquals(todayDate, points[1].x);
        assertEquals(runTime, points[1].y);

        gcal.add(Calendar.DATE, -1);
        int yestDate =  m_dateHandler.getYYYYMMDD(gcal);
        assertEquals(yestDate, points[0].x);
        assertEquals(0, points[0].y);


        /*
        // Verify mapping tables are updated ok.
        Map<String,String> app_to_process = m_dbHandler.get_app_to_process_map(process_name);
        assertEquals(3, app_to_process.size());
        assertEquals(process_name, app_to_process.get(app_name));
        */

        // Other things to add tests for. when I have time.
        // public ArrayList<DataValue> getData( String hist_pref, String input_app_name )
        // public ArrayList<TimeLog> getTimeLog( String hist_pref, String input_app_name ) {
        // public ArrayList<TimeLog> getTimeLogFromTime(long lastAppDate, String lastAppName)
        //Log.i("DHGG","Run time = " + ((tl.end_time - tl.start_time)/1000.0) );
    }


    //////////////////////////////////////////////////////////////////
    // Helper functions
    //////////////////////////////////////////////////////////////////
    private void sleepForSeconds(int seconds) {

        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}


