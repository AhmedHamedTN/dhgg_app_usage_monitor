/*
 * A trivial test. Used to test if I can run a test.
 */

package com.dhgg.appusagemonitor_tests;

import android.test.ActivityInstrumentationTestCase2;

import com.dhgg.appusagemonitor.HistoryPlotActivity;
import com.dhgg.appusagemonitor.MainActivity;

//public class HistoryPlotActivityTest extends ActivityInstrumentationTestCase2<HistoryPlotActivity> {
public class HistoryPlotActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity m_activity;

    public HistoryPlotActivityTest() {
        // super("com.dhgg.appusagemonitor", HistoryPlotActivity.class);
        // super(MainActivity.class);
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        m_activity = getActivity();
    }

    public void testSendData() throws Exception {
        int a = 1;
        int b = 21;
        assertEquals(a,b);
    }

}