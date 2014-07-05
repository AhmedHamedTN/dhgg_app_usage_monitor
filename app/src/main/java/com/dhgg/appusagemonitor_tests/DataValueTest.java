package com.dhgg.appusagemonitor_tests;

import com.dhgg.appusagemonitor.DataValue;
import com.dhgg.appusagemonitor.DataValueComparator;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by dhan on 7/5/14.
 */
public class DataValueTest extends TestCase {

    protected void setUp() {

    }

    public void testSort() {
        ArrayList<DataValue> data = new ArrayList<DataValue>();

        DataValue a = new DataValue("AAA","A process", 222);
        DataValue b = new DataValue("BBB","B process", 333);

        // Adding data with smallest value first.
        data.add(0,a);
        data.add(0,b);

        // This should sort the data so that highest value is first.
        Collections.sort(data, new DataValueComparator());

        // Check assertions.
        DataValue tmp = (DataValue) data.get(0);
        assertEquals("After sorting by value, we expect B to be first.",
                     "BBB", tmp.description);

        tmp = (DataValue) data.get(1);
        assertEquals("After sorting by value, we expect A to be second.",
                     "AAA", tmp.description);
    }

}
