package com.dhgg.appusagemonitor;

import java.util.Comparator;

public class DataValue {
    String description;
    String process_name;
    int value;

    public DataValue(String desc, String proc, int val) {
        super();
        description = desc;
        process_name = proc;
        value = val;
    }
}


class DataValueComparator implements Comparator<DataValue> {
    public int compare(DataValue v1, DataValue v2)
    {
        return v2.value - v1.value;
    }
}
