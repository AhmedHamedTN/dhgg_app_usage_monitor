package com.dhgg.appusagemonitor;

public class Time_log {

	String description;
    String process_name;
    long start_time;
    long end_time;
    
    public Time_log(String desc, String proc, long startTime, long endTime)
    {
    	super();
    	description = desc;
    	process_name = proc;
    	start_time = startTime;
    	end_time = endTime;
    }
}
