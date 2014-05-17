package com.dhgg.appusagemonitor;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Time_log_adapter extends ArrayAdapter<Time_log> 
{	
	Context context;
    int layoutResourceId;   
    Time_log data[] = null;
    boolean m_use_colors = false;
    
    public Time_log_adapter(Context context, int layoutResourceId, Time_log[] data) 
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	// System.out.println("+++ Data_value_adapter::getView +++" );    	
        View row = convertView;
        
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);
        
        TextView name_view = (TextView)row.findViewById(R.id.name);
        name_view.setText(data[position].description+"\n");
        
        TextView value_view = (TextView)row.findViewById(R.id.value);
        
        long startTime = data[position].start_time;
        long endTime =  data[position].end_time;
        value_view.setText( get_time_str(startTime, endTime) );
        
        try
        {
        	PackageManager pm = context.getPackageManager();
        	Drawable icon = pm.getApplicationIcon( data[position].process_name );
        	
        	ImageView img_view = (ImageView)row.findViewById(R.id.imgIcon);
            
        	img_view.setImageDrawable( icon );            
        } catch (Exception e) {}
        
        return row;
    }
    
    public String get_time_str( long start_time, long end_time)
    {
    	//Log.w("DHGG","start_time:"+start_time);
    	
    	//SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
    	SimpleDateFormat start_sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
    	Date startDate = new Date(start_time);
    	
    	SimpleDateFormat end_sdf = new SimpleDateFormat("HH:mm:ss");
    	Date endDate = new Date(end_time);
    	
    	String time_str = start_sdf.format(startDate)+"-"+end_sdf.format(endDate);
    	return time_str;
    }
}


