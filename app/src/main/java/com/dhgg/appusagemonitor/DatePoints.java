package com.dhgg.appusagemonitor;

import java.util.ArrayList;
import java.util.Date;
import android.util.Log;

/**
 * Helper class that
 */
public class DatePoints {
    private Point[] m_points = null;
    private DatePoint[] m_datepoints = null;
    private String m_units = "seconds";
    private int m_factor = 1;

    /**
     * Public Constructor
     */
    public DatePoints(Point [] first, Point [] second) {
        setMergedValues(first, second);
        setUnitsAndFactor();
        setDatePoints();
    }

    private Date getDateFromInt(int dt) {
        return new Date( (dt/ 10000) - 1900,
                ((dt / 100 ) % 100 )-  1,
                dt % 100 );
    }

    private void setDatePoints() {
        ArrayList<DatePoint> dps = new ArrayList<DatePoint>();
        int numPoints = m_points.length;
        for ( int i = 0; i < numPoints; i++ ) {
            DatePoint dp = new DatePoint(
                    getDateFromInt(m_points[i].x),
                    m_points[i].y = m_points[i].y / m_factor);
            dps.add(dp);
        }
        m_datepoints = dps.toArray(new DatePoint[dps.size()]);
    }

    /**
     * Analyzes points then sets member variables for the factor and units.
     */
    private void setUnitsAndFactor() {
        int numPoints = m_points.length;
        for ( int i = 0; i < numPoints; i++ )
        {
            if ( m_points[ i ].y > 60 )
            {
                m_units = "minutes";
                m_factor = 60;
            }
        }
    }

    /**
     *
     */
    private void setMergedValues(Point[] first, Point[] second) {

        ArrayList <Point> data = new ArrayList<Point>();

        int l = 0;
        int c = 0;
        while (l < first.length && second != null && c < second.length) {
            Point lPoint = first[l];
            Point cPoint = second[c];

            if (lPoint.x < cPoint.x) {
                //Log.w(Consts.LOGTAG, "AppListFragment::openChart adding from localData " + lPoint.x);
                data.add(lPoint);
                l++;
            } else if (lPoint.x > cPoint.x ) {
                //Log.w(Consts.LOGTAG,"AppListFragment::openChart adding from cloudData "+cPoint.x);
                data.add( cPoint );
                c++;
            } else  {
                // Data found in the cloud and on the local machine.
                // Default to using the local data.
                // May want to change to using cloud or summed data later.
                //Log.w(Consts.LOGTAG,"AppListFragment::openChart default from localData "+lPoint.x);
                data.add(lPoint);

                // Move to next item.
                l++;
                c++;
            }
        }

        while (l < first.length) {
            Point lPoint = first[l];
            //Log.w(Consts.LOGTAG,"AppListFragment::openChart finishing up localData "+lPoint.x);
            data.add( lPoint );
            l++;
        }

        while (second != null && c < second.length) {
            Point cPoint = second[c];
            //Log.w(Consts.LOGTAG,"AppListFragment::openChart finishing up cloudData "+cPoint.x);
            data.add( cPoint );
            c++;
        }

        m_points = data.toArray(new Point[data.size()]);
    }

    public DatePoint[] getDatePoints() {
        return m_datepoints;
    }

    public String getUnits() {
        return m_units;
    }


}
