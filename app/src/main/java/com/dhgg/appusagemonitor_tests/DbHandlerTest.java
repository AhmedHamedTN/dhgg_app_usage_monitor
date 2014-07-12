package com.dhgg.appusagemonitor_tests;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

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
    private DbHandler m_db_handler;
    int m_todays_date;

    @Override
    public void setUp() {
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        m_db_handler = new DbHandler(context);

        GregorianCalendar gcalendar = new GregorianCalendar( );
        m_todays_date =  gcalendar.get( Calendar.YEAR ) * 10000 +
                (gcalendar.get( Calendar.MONTH ) + 1 )  * 100 +
                gcalendar.get( Calendar.DATE ) ;
    }

    @Override
    public void tearDown() throws Exception {
        m_db_handler.close();
        super.tearDown();
    }

    /**
     * Test that the db is blank to start with.
     */
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
        String app_name = "app_name";
        String process_name = "process_name";
        int expectedNumLogs = 1;
        int runTime = 5;

        // Run
        m_db_handler.update_or_add(app_name, process_name);
        sleepForSeconds(runTime);
        m_db_handler.update_or_add(app_name, process_name);

        // Verify 1 entry in db
        String hist_pref = "s_h_p_today";
        ArrayList<TimeLog> timeLogs = m_db_handler.getTimeLog(hist_pref, app_name);
        int numLogs = timeLogs.size();
        assertEquals(expectedNumLogs, numLogs);

        // Verify time analysis for this row
        TimeLog tl = timeLogs.get(0);
        assertTrue(runTime <=  ((tl.end_time - tl.start_time)/1000.0) );

        // Verify time analysis for this row
        Point[] points = m_db_handler.getHistoricalData(app_name);
        assertEquals(1, points.length);
        assertEquals(m_todays_date, points[0].x);
        assertEquals(runTime, points[0].y);

        // Verify mapping tables are updated ok.
        Map<String,String> app_to_process = m_db_handler.get_app_to_process_map(process_name);
        assertEquals(1, app_to_process.size());
        assertEquals(process_name, app_to_process.get(app_name));

        /*
        // Other things to add tests for. when I have time.
        public ArrayList<DataValue> getData( String hist_pref, String input_app_name )
        public ArrayList<TimeLog> getTimeLog( String hist_pref, String input_app_name ) {
        public ArrayList<TimeLog> getTimeLogFromTime(long lastAppDate, String lastAppName)
        */
        //Log.i("DHGG","Run time = " + ((tl.end_time - tl.start_time)/1000.0) );
    }

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


