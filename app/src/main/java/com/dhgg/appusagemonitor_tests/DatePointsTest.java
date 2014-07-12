package com.dhgg.appusagemonitor_tests;

import com.dhgg.appusagemonitor.DatePoint;
import com.dhgg.appusagemonitor.DatePoints;
import com.dhgg.appusagemonitor.Point;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by dhan on 7/12/14.
 */
public class DatePointsTest extends TestCase {

    DatePoints m_datePoints;
    ArrayList<Point> m_local_arr_points;
    ArrayList<Point> m_cloud_arr_points;

    protected void setUp() {
        m_local_arr_points = new ArrayList<Point>();
        m_cloud_arr_points = new ArrayList<Point>();
    }

    protected void tearDown() {

    }

    public void testEmpty() {

        // Setup
        DatePoints datePoints =  new DatePoints(
                m_local_arr_points.toArray(new Point[m_local_arr_points.size()]) ,
                m_cloud_arr_points.toArray(new Point[m_local_arr_points.size()]) );

        // Run
        String units = datePoints.getUnits() ;
        DatePoint[] dp = datePoints.getDatePoints();

        // Verify
        assertEquals(0, dp.length);
        assertEquals("seconds", units);
    }

    // TODO: test case where local and cloud data overlap.
    // TODO: test case where we have non-overlapping local and cloud data .
    // TODO: test case where we only have local data.
    // TODO: test case where we only have cloud data.
}
