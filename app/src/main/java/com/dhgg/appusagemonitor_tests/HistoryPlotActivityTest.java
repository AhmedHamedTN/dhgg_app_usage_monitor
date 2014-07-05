/*
 * Testing the historical plot activity with a range of data inputs
 * with Mockito.
 */
package com.dhgg.appusagemonitor_tests;

import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.test.ActivityInstrumentationTestCase2;

import com.dhgg.appusagemonitor.DbHandler;
import com.dhgg.appusagemonitor.HistoryPlotActivity;

public class HistoryPlotActivityTest extends ActivityInstrumentationTestCase2<HistoryPlotActivity> {

    private HistoryPlotActivity m_activity;
    private String m_logTag = "DHGG::HistoryPlotActivityTest";

    public HistoryPlotActivityTest() {
        super(HistoryPlotActivity.class);
    }

    DbHandler mock_db_handler;

    @Override
    protected void setUp() throws Exception {
        // Will be called before each test.
        Log.w(m_logTag, "setUp");

        // MockitoAnnotations.initMocks(this);
        // mock_db_handler = mock(DbHandler.class);

        // Passing a custom intent
        Intent intent = new Intent();
        intent.setClassName("com.dhgg.appusagemonitor","com.dhgg.appusagemonitor.HistoryPlotActivity");
        intent.putExtra("app_name","App Usage Monitor");
        intent.putExtra("package_name", "com.dhgg.appusagemonitor");
        intent.putExtra("refresh_data", true);
        setActivityIntent(intent);

        // when(mock_db_handler.)

        setActivityInitialTouchMode(false);
        m_activity = getActivity();

        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        // Will be called after each test.
        super.tearDown();
    }

    public void createDatabaseFixture(Context mockContext) {
        Log.w(m_logTag, "createDatabaseFixture");
    }

    public void testDoSomeTest() throws Exception {
        Log.w(m_logTag, "testDoSomeTest");
        int a = 1;
        int b = 1;
        assertEquals(a,b);

    }


    // TODO: Test how to handle no data.
    // TODO: Test how to handle a lot of data.
    // TODO: Test no network
}
