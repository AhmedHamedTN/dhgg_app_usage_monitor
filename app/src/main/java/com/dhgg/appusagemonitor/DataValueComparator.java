package com.dhgg.appusagemonitor;

import java.util.Comparator;

/**
 * Created by dhan on 7/5/14.
 */
public class DataValueComparator implements Comparator<DataValue> {
    public int compare(DataValue v1, DataValue v2)
    {
        return v2.value - v1.value;
    }
}
