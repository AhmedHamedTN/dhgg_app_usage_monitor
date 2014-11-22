package com.dhgg.appusagemonitor_tests;

import com.dhgg.appusagemonitor.DateHandler;
import com.dhgg.appusagemonitor.DatePoint;
import com.dhgg.appusagemonitor.DatePoints;
import com.dhgg.appusagemonitor.Point;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by dhan on 11/22/14.
 */
public class DateHandlerTest extends  TestCase {

    private DateHandler mDateHandler;
    protected void setUp() {
        mDateHandler = new DateHandler();
    }

    protected void tearDown() {
    }

    public void testEmpty() {
        GregorianCalendar gcal = new GregorianCalendar();
        int todayYYYYMMDD = mDateHandler.getYYYYMMDD(gcal);

        // Verify
        assertEquals(
                mDateHandler.getStartOfDayTodayMs(),
                mDateHandler.getMillisFromYYYYMMDD(todayYYYYMMDD));
    }
}

