package com.dhgg.appusagemonitor;

import java.util.Comparator;

public class Data_value {
    String description;
    String process_name;
    int value;

    public Data_value(String desc, String proc, int val) {
        super();
        description = desc;
        process_name = proc;
        value = val;
    }
}


class DataValueComparator implements Comparator<Data_value> 
{
    public int compare(Data_value v1, Data_value v2) 
    {
        return v2.value - v1.value;
    }
}
