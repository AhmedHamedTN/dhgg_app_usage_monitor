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

    public void testConsistency() {
        GregorianCalendar gcal = new GregorianCalendar();
        int todayYYYYMMDD = mDateHandler.getYYYYMMDD(gcal);

        // Verify
        assertEquals(
                mDateHandler.getStartOfDayTodayMs(),
                mDateHandler.getMillisFromYYYYMMDD(todayYYYYMMDD));
    }

    public void test24HoursAgoIsOlderThanStartOfDay() {
        long startOfDayMs = mDateHandler.getStartOfDayTodayMs();
        long twenty4HourAgoMs = mDateHandler.get24HoursAgoMs();

        // Verify
        assertTrue(startOfDayMs > twenty4HourAgoMs);
    }

    public void testGetGmtTimeRelativeToLocalIsGood() {
        long gmtStartOfToday = mDateHandler.getStartOfDayTodayGmtMs();
        long localStartOfToday = mDateHandler.getStartOfDayTodayMs();

        GregorianCalendar gcal = new GregorianCalendar();
        int offset = gcal.getTimeZone().getRawOffset();

        assertEquals(gmtStartOfToday, localStartOfToday + offset);
    }

    /*
    public void testGetYYYYMMDDGmtRelativeToLocalIsGood() {
        long gmtStart = mDateHandler.getMillisFromYYYYMMDD(20141123);
        long localStart = mDateHandler.getStartOfDayYYYYMMDDGmtMs(20141123);

        GregorianCalendar gcal = new GregorianCalendar();
        int offset = gcal.getTimeZone().getRawOffset();

        assertEquals(gmtStart, localStart + offset);
    }
    */


}

