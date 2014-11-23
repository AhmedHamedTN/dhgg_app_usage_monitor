package com.dhgg.appusagemonitor;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by dhan on 11/22/14.
 * This class should handle all date logic.
 */
public class DateHandler {

    public String getCurrTimeStr() {
        GregorianCalendar date = new GregorianCalendar();
        return
            (date.get(Calendar.MONTH) + 1) + "/" +
            date.get(Calendar.DATE) + "/" +
            date.get(Calendar.YEAR) + " " +
            date.get(Calendar.HOUR) + ":" +
            date.get(Calendar.MINUTE) + ":" +
            date.get(Calendar.SECOND) + " (" +
            date.getTimeZone().getDisplayName() + ")";
    }

    public long getCurrentMs() {
        GregorianCalendar date = new GregorianCalendar();
        return date.getTimeInMillis();
    }

    public long get24HoursAgoMs() {
        GregorianCalendar date = new GregorianCalendar();
        date.add(Calendar.DATE, -1);
        return date.getTimeInMillis();
    }

    public long getStartOfDayTodayMs() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    public long getStartOfDayTodayGmtMs() {
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        today.setTimeZone(TimeZone.getTimeZone("GMT"));

        return today.getTimeInMillis();
    }

    public long getStartOfDayYYYYMMDDGmtMs(int YYYYMMDD) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeZone(TimeZone.getTimeZone("GMT"));

        gcal.set(Calendar.YEAR, YYYYMMDD/10000);
        gcal.set(Calendar.MONTH, ((YYYYMMDD/100) % 100) - 1);
        gcal.set(Calendar.DATE, YYYYMMDD % 100);
        gcal.set(Calendar.HOUR_OF_DAY, 0);
        gcal.set(Calendar.MINUTE, 0);
        gcal.set(Calendar.SECOND, 0);
        gcal.set(Calendar.MILLISECOND, 0);

        return gcal.getTimeInMillis();
    }

    public long getMillisFromYYYYMMDD(int YYYYMMDD) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.set(Calendar.YEAR, YYYYMMDD/10000);
        gcal.set(Calendar.MONTH, ((YYYYMMDD/100) % 100) - 1);
        gcal.set(Calendar.DATE, YYYYMMDD % 100);
        gcal.set(Calendar.HOUR_OF_DAY, 0);
        gcal.set(Calendar.MINUTE, 0);
        gcal.set(Calendar.SECOND, 0);
        gcal.set(Calendar.MILLISECOND, 0);

        return gcal.getTimeInMillis();
    }

    public int getYYYYMMDD(GregorianCalendar date) {
        return  (date.get(Calendar.YEAR) * 10000) +
                ((date.get(Calendar.MONTH) + 1) * 100) +
                date.get(Calendar.DATE) ;
    }

    public int getGmtYYYYMMDDFromMs(long timestamp) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(timestamp);
        gcal.setTimeZone(TimeZone.getTimeZone("GMT"));

        return  (gcal.get(Calendar.YEAR) * 10000) +
                ((gcal.get(Calendar.MONTH) + 1) * 100) +
                gcal.get(Calendar.DATE) ;
    }

} // close DateHandler
