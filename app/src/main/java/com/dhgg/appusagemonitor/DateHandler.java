package com.dhgg.appusagemonitor;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    public long getStartOfDayTodayMs() {
        GregorianCalendar date = new GregorianCalendar();
        date.set(Calendar.HOUR, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return date.getTimeInMillis();
    }

    public long getMillisFromYYYYMMDD(long YYYYMMDD) {
        int aDate = (int) YYYYMMDD;
        GregorianCalendar gcalendar = new GregorianCalendar(
                aDate / 10000,
                (aDate / 100 ) % 100,
                aDate % 100 );
        return gcalendar.getTimeInMillis();
    }

    public int getYYYYMMDD(GregorianCalendar date) {
        return  (date.get(Calendar.YEAR) * 10000) +
                ((date.get(Calendar.MONTH) + 1) * 100) +
                date.get(Calendar.DATE) ;
    }

} // close DateHandler
