package com.dhgg.appusagemonitor;

public class TimeLog {

	public String description;
    public String process_name;
    public long start_time;
    public long end_time;
    
    public TimeLog(String desc, String proc, long startTime, long endTime)
    {
    	super();
    	description = desc;
    	process_name = proc;
    	start_time = startTime;
    	end_time = endTime;
    }
}
