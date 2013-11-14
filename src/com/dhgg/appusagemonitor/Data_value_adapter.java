package com.dhgg.appusagemonitor;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Data_value_adapter extends ArrayAdapter<Data_value> 
{
	public int m_colors[] = { 
	        Color.CYAN,    //
	        Color.RED, 
	        Color.LTGRAY,
	        Color.DKGRAY,
	        Color.BLUE,
	        Color.GREEN,
	        Color.GRAY,
	        Color.YELLOW,
	        Color.BLACK,
	        Color.MAGENTA,
	        Color.WHITE };
	public int m_num_colors = 11;
	
	Context context;
    int layoutResourceId;   
    Data_value data[] = null;
    boolean m_use_colors = false;
    
    public Data_value_adapter(Context context, int layoutResourceId, Data_value[] data) 
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
        name_view.setText(data[position].description);
        /*
        if ( m_use_colors && ( position >= m_num_colors - 1) )
        {
        	name_view.setText( "Other ...");
        }
        */
        
        TextView value_view = (TextView)row.findViewById(R.id.value);
        
        int total_secs = data[position].value;
        if ( m_use_colors )
        {
        	value_view.setText( get_percent_str(total_secs) );
        }
        else
        {
        	value_view.setText( get_time_str(total_secs) );
        }

        try
        {
        	ImageView img_view = (ImageView)row.findViewById(R.id.imgIcon);
            
        	if ( m_use_colors )
        	{
        		img_view.setBackgroundColor( m_colors[ position % m_num_colors ] );
        	}
        	else
        	{
	        	PackageManager pm = context.getPackageManager();
	        	Drawable icon = pm.getApplicationIcon( data[position].process_name );
        		img_view.setImageDrawable( icon );
        	}
            
        } catch (Exception e) {}
        
        return row;
    }
    
    public void set_use_colors_flag( boolean flag )
    {
    	m_use_colors = flag;
    }

    public String get_percent_str( int value )
    {    
    	if ( value < 1 )
    	{
    		return "< 1%";
    	}
    	return  value + "%";
    }
    
    public String get_time_str( int time_in_seconds)
    {
    	int total_secs = time_in_seconds;
    	
        int hours = total_secs / 3600;
        int mins = (total_secs - (hours * 3600))/ 60;
        int secs = total_secs - (hours * 3600) - (mins * 60);
        
        String time_str = "";
        if (hours > 0)
        {
        	time_str += hours + "h ";
        }
        if (mins > 0)
        {
        	time_str += mins + "m ";
        }
        time_str += secs + "s";
        
    	return time_str;
    }
}


