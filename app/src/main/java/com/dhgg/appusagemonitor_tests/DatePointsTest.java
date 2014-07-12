package com.dhgg.appusagemonitor_tests;

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

        // Verify
        assertEquals("seconds", units);
    }

}
